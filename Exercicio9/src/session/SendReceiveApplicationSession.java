package session;

import java.net.InetSocketAddress;
import java.util.Timer;
import java.util.TimerTask;

import model.CustomProcess;
import model.ProcessList;
import net.sf.appia.core.AppiaEventException;
import net.sf.appia.core.Channel;
import net.sf.appia.core.Direction;
import net.sf.appia.core.Event;
import net.sf.appia.core.Layer;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.channel.ChannelInit;
import net.sf.appia.core.message.Message;
import net.sf.appia.protocols.common.RegisterSocketEvent;
import event.HeartbeatEvent;
import event.TrustEvent;

/**
 * Session implementing the Print Application protocol. <br>
 * Reads strings, requests their printing and displays confirmations.
 * 
 * @author alexp
 */
public class SendReceiveApplicationSession extends Session {

	public SendReceiveApplicationSession(Layer layer) {
		super(layer);
	}

	public void handle(Event event) {
		if (event instanceof ChannelInit)
			handleChannelInit((ChannelInit) event);
		else if (event instanceof TrustEvent)
			handleReceiverConfirm((TrustEvent) event);
		else if (event instanceof HeartbeatEvent)
			handleSenderRequest((HeartbeatEvent) event);
	}

	private Timer timer = null;
	private ProcessList processes;
	private ProcessList candidates;
	private CustomProcess leader = null;
	private Channel channel = null;
	
	private long delta = 2000;

	private void handleChannelInit(ChannelInit init) {
		candidates = new ProcessList();
		leader = processes.getSelf();
		channel = init.getChannel();
		
		if (timer == null){
			timer = new Timer();
			timer.schedule(new Timeout(), 0, delta);
		}

		try {
			RegisterSocketEvent rse = new RegisterSocketEvent(
					init.getChannel(), Direction.DOWN, this);

			InetSocketAddress address = processes.getSelf()
					.getCompleteAddress();
			
			rse.port = address.getPort();
			rse.localHost = address.getAddress();
			System.out.println("port: " + rse.port);
			rse.go();
		} catch (AppiaEventException e1) {
			e1.printStackTrace();
		}
		
		try {
			init.go();
		} catch (AppiaEventException ex) {
			ex.printStackTrace();
		}
		System.out.println("Channel is open.");
	}

	private void handleReceiverConfirm(TrustEvent conf) {
		/*conf.getMessage().popString();
		
		System.out
				.println("[Sender: received confirmation of request "
						+ conf.getMessage().peekInt() + "]");*/
		try {
			conf.go();
		} catch (AppiaEventException ex) {
			ex.printStackTrace();
		}
	}

	private void handleSenderRequest(HeartbeatEvent conf) {
		if( conf.getDir() == Direction.DOWN ){
			try {
				conf.go();
			} catch (AppiaEventException e) {
				e.printStackTrace();
			}
		} else {
			Message message = conf.getMessage();
			
			System.out.println("[Message received: "
							+ message.peekString() + "]");
			
			TrustEvent event = new TrustEvent();
			/*event.setMessage(message);
			event.setDest(processes.getOther());
			event.setSendSource(processes.getSelf());*/
			event.setChannel(conf.getChannel());
			event.setDir(Direction.DOWN);
			event.setSourceSession(this);
			
			try {
				event.init();
				event.go();
			} catch (AppiaEventException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void init(ProcessList processes) {
		this.processes = processes;
	}
	
	private class Timeout extends TimerTask {

		@Override
		public void run() {
			CustomProcess newleader = candidates.selectLeader();
			if( newleader.getId() != leader.getId() ){
				delta += delta;
				leader = newleader;
				
				TrustEvent event = new TrustEvent();
				event.setLeader(leader);
				
				try {
					event.asyncGo(channel, Direction.UP);
				} catch (AppiaEventException e) {
					e.printStackTrace();
				}
			}
			
			HeartbeatEvent heartbeat = null;
			Message message = null;
			
			for( CustomProcess process : processes ){
				heartbeat = new HeartbeatEvent();
				message = new Message();
				
				message.pushObject(processes.getSelf());
				
				heartbeat.setMessage(message);
				heartbeat.setDest(process);
				heartbeat.setSendSource(processes.getSelf());
				
				try {
					heartbeat.asyncGo(channel, Direction.DOWN);
				} catch (AppiaEventException e) {
					e.printStackTrace();
				}
			}
			
			candidates.clear();
			timer.schedule(new Timeout(), 0, delta);
		}
		
	}
}

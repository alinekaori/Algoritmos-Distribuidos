package session;

import java.net.InetSocketAddress;
import java.util.Timer;
import java.util.TimerTask;

import javax.management.timer.TimerMBean;

import model.CustomProcess;
import model.ProcessList;
import net.sf.appia.core.AppiaEventException;
import net.sf.appia.core.Direction;
import net.sf.appia.core.Event;
import net.sf.appia.core.Layer;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.channel.ChannelInit;
import net.sf.appia.core.message.Message;
import net.sf.appia.protocols.common.RegisterSocketEvent;
import event.ReceiverConfirmEvent;
import event.SenderRequestEvent;

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
		else if (event instanceof ReceiverConfirmEvent)
			handleReceiverConfirm((ReceiverConfirmEvent) event);
		else if (event instanceof SenderRequestEvent)
			handleSenderRequest((SenderRequestEvent) event);
	}

	private Timer timer = null;
	private ProcessList processes;
	private ProcessList candidates;
	private CustomProcess leader = null;
	private long delta = 2000;

	private void handleChannelInit(ChannelInit init) {
		candidates = new ProcessList();
		
		if (timer == null){
			timer = new Timer();
			timer.scheduleAtFixedRate(new Timeout(), 0, delta);
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

	private void handleReceiverConfirm(ReceiverConfirmEvent conf) {
		conf.getMessage().popString();
		
		System.out
				.println("[Sender: received confirmation of request "
						+ conf.getMessage().peekInt() + "]");
		try {
			conf.go();
		} catch (AppiaEventException ex) {
			ex.printStackTrace();
		}
	}

	private void handleSenderRequest(SenderRequestEvent conf) {
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
			
			ReceiverConfirmEvent event = new ReceiverConfirmEvent();
			event.setMessage(message);
			event.setDest(processes.getOther());
			event.setSendSource(processes.getSelf());
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
				
				// TODO trust!
			}
			
			for( CustomProcess p : processes ){
				// TODO heartbeat!
			}
			
			candidates.clear();
		}
		
	}
}

package session;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
	
	private long delta;

	private void handleChannelInit(ChannelInit init) {
		candidates = new ProcessList();
		leader = processes.getSelf();
		channel = init.getChannel();
		delta = 2000;		
		
		TrustEvent event = new TrustEvent();
		event.setLeader(leader);
		
		try {
			event.setChannel(channel);
			event.setDir(Direction.UP);
			event.setSourceSession(this);
			
			event.init();
			event.go();
		} catch (AppiaEventException e) {
			e.printStackTrace();
		}
		
		File f = new File("epoch"+processes.getSelf().getId()+".txt");
		if( f.exists() ){
			 try {
				BufferedReader input =  new BufferedReader(new FileReader(f));
				int epoch = Integer.parseInt(input.readLine());
				processes.getSelf().setEpoch(epoch + 1);
				store();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				processes.getSelf().setEpoch(0);
				store();
			} catch (IOException e) {
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
				heartbeat.setChannel(channel);
				heartbeat.setDir(Direction.DOWN);
				heartbeat.setSourceSession(this);
				
				heartbeat.init();
				heartbeat.go();
			} catch (AppiaEventException e) {
				e.printStackTrace();
			}
		}

		try {
			RegisterSocketEvent rse = new RegisterSocketEvent(
				init.getChannel(), Direction.DOWN, this);

			InetSocketAddress address = processes.getSelf()
				.getCompleteAddress();
				
			rse.port = address.getPort();
			rse.localHost = address.getAddress();

			rse.go();
		} catch (AppiaEventException e1) {
			e1.printStackTrace();
		}
		
		try {
			init.go();
		} catch (AppiaEventException ex) {
			ex.printStackTrace();
		}
		
		if (timer == null){
			timer = new Timer();
			timer.schedule(new Timeout(), 0, delta);
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
	
	private void store() throws IOException{
		FileWriter outFile = new FileWriter("epoch"+processes.getSelf().getId()+".txt", false);
		PrintWriter out = new PrintWriter(outFile);

		out.print(processes.getSelf().getEpoch());
		out.close();
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

package session;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import model.ProcessList;
import model.SimpleMessage;
import net.sf.appia.core.AppiaEventException;
import net.sf.appia.core.Channel;
import net.sf.appia.core.Direction;
import net.sf.appia.core.Event;
import net.sf.appia.core.Layer;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.channel.ChannelInit;
import net.sf.appia.core.message.Message;
import event.DeliverEvent;
import event.SendEvent;

/**
 * Session implementing the Print Application protocol. <br>
 * Reads strings, requests their printing and displays confirmations.
 * 
 * @author alexp
 */
public class PerfectLinkSession extends Session {

	public PerfectLinkSession(Layer layer) {
		super(layer);
	}

	public void handle(Event event) {
		if (event instanceof ChannelInit)
			handleChannelInit((ChannelInit) event);
		else if (event instanceof DeliverEvent)
			handleDeliverEvent((DeliverEvent) event);
	}

	private MessageReader reader = null;
	private ProcessList processes;
	private ArrayList<SimpleMessage> delivered = null;

	private void handleChannelInit(ChannelInit init) {
		try {
			init.go();
		} catch (AppiaEventException ex) {
			ex.printStackTrace();
		}

		if (reader == null)
			reader = new MessageReader(init.getChannel());
		
		delivered = new ArrayList<SimpleMessage>();
	}
	
	private void handleDeliverEvent(DeliverEvent conf) {
		SimpleMessage message = (SimpleMessage)conf.getMessage().popObject();
		boolean messageDelivered = false;
		
		for(SimpleMessage m : delivered){
			if( message.getId() == m.getId() ){
				messageDelivered = true;
				break;
			}
		}
		
		if(!messageDelivered){
			System.out.println("[Deliver: message delivered: "
					+ message.getId() + "]");
			
			delivered.add(message);
		}
		
		try {
			conf.go();
		} catch (AppiaEventException ex) {
			ex.printStackTrace();
		}
	}

	private class MessageReader extends Thread {

		public boolean ready = false;
		public Channel channel;
		private BufferedReader stdin = new BufferedReader(
				new InputStreamReader(System.in));
		private int rid = 0;

		public MessageReader(Channel channel) {
			ready = true;
			if (this.channel == null)
				this.channel = channel;
			this.start();
		}

		public void run() {
			boolean running = true;

			while (running) {
				rid++;
				System.out.println();
				System.out.print("[Sender](" + rid + ")> ");
				try {
					String s = stdin.readLine();
					
					SendEvent request = new SendEvent();
					request.isOriginalMessage(true);
					
					Message m = new Message();
					m.pushObject(new SimpleMessage(rid, s));
					request.setMessage(m);
					request.setDestProcess(processes.getOther());
					request.setSourceProcess(processes.getSelf());
					
					request.asyncGo(channel, Direction.DOWN);
				} catch (AppiaEventException ex) {
					ex.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					Thread.sleep(1500);
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				synchronized (this) {
					if (!ready)
						running = false;
				}
			}
		}
	}

	public void init(ProcessList processes) {
		this.processes = processes;
	}
}

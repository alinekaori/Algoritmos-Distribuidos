package session;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import model.SimpleMessage;
import net.sf.appia.core.AppiaEventException;
import net.sf.appia.core.Channel;
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
public class SendApplicationSession extends Session {

	public SendApplicationSession(Layer layer) {
		super(layer);
	}

	public void handle(Event event) {
		System.out.println();

		if (event instanceof ChannelInit)
			handleChannelInit((ChannelInit) event);
		else if (event instanceof ReceiverConfirmEvent)
			handleReceiverConfirm((ReceiverConfirmEvent) event);
	}

	private MessageReader reader = null;
	private SocketAddress[] addresses;

	private void handleChannelInit(ChannelInit init) {
		try {
			init.go();
		} catch (AppiaEventException ex) {
			ex.printStackTrace();
		}

		if (reader == null)
			reader = new MessageReader(init.getChannel());
		
		
		try {
		      // sends this event to open a socket in the layer that is used has perfect
		      // point to point
		      // channels or unreliable point to point channels.
		      RegisterSocketEvent rse = new RegisterSocketEvent(init.getChannel(),
		          Direction.DOWN, this);
		      rse.port = ((InetSocketAddress) addresses[0]).getPort();
		      rse.localHost = ((InetSocketAddress)addresses[0]).getAddress();
		      rse.go();
		    } catch (AppiaEventException e1) {
		      e1.printStackTrace();
		    }
		    System.out.println("Channel is open.");
	}

	private void handleReceiverConfirm(ReceiverConfirmEvent conf) {
		System.out.println("[Sender: received confirmation of request "
						+ ((SimpleMessage)conf.getMessage().popObject()).getId() + "]");
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
					
					SenderRequestEvent request = new SenderRequestEvent();
					request.setId(rid);
					
					Message m = new Message();
					m.pushObject(new SimpleMessage(rid, s));
					request.setMessage(m);
					request.setDest(addresses[1]);
					request.setSendSource(addresses[0]);
					System.out.println("sending... " );
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

	public void init(SocketAddress[] addresses) {
		this.addresses = addresses;
	}
}

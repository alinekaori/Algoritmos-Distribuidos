package session;


import java.net.InetSocketAddress;

import model.ProcessList;
import model.SimpleMessage;
import net.sf.appia.core.AppiaEventException;
import net.sf.appia.core.Direction;
import net.sf.appia.core.Event;
import net.sf.appia.core.Layer;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.channel.ChannelInit;
import net.sf.appia.core.message.Message;
import net.sf.appia.protocols.common.RegisterSocketEvent;
import event.DeliverEvent;
import event.SendEvent;

/**
 * Session implementing the Print Application protocol. <br>
 * Reads strings, requests their printing and displays confirmations.
 * 
 * @author alexp
 */
public class ReceiverSession extends Session {

	public ReceiverSession(Layer layer) {
		super(layer);
	}

	public void handle(Event event) {
		if (event instanceof ChannelInit)
			handleChannelInit((ChannelInit) event);
		else if (event instanceof SendEvent)
			handleSendEvent((SendEvent) event);
	}

	private ProcessList processes;

	private void handleChannelInit(ChannelInit init) {
		try {
			init.go();
		} catch (AppiaEventException ex) {
			ex.printStackTrace();
		}
		
		try {
	      RegisterSocketEvent rse = new RegisterSocketEvent(init.getChannel(),
	          Direction.DOWN, this);
	      
	      InetSocketAddress address = processes.getSelf().getCompleteAddress();
	      
	      rse.port = address.getPort();
	      rse.localHost = address.getAddress();
	      
	      rse.go();
	    } catch (AppiaEventException e1) {
	      e1.printStackTrace();
		}
	}
	
	private void handleSendEvent(SendEvent conf) {
		SimpleMessage message = (SimpleMessage)conf.getMessage().popObject();
		
		System.out.println("[Message received: "
						+ message.getString() + "]");
		
		DeliverEvent event = new DeliverEvent();
		
		Message m = new Message();
		m.pushObject(message);
		event.setMessage(m);
		event.setDestProcess(processes.getOther());
		event.setSourceProcess(processes.getSelf());
		event.setChannel(conf.getChannel());
		event.setDir(Direction.DOWN);
		event.setSourceSession(this);
		
		try {
			//conf.go();
			
			event.init();
			event.go();
		} catch (AppiaEventException ex) {
			ex.printStackTrace();
		}
	}

	public void init(ProcessList processes) {
		this.processes = processes;
	}
}

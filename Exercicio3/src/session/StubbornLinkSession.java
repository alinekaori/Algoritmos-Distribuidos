package session;
/*
 *
 * Hands-On code of the book Introduction to Reliable Distributed Programming
 * by Christian Cachin, Rachid Guerraoui and Luis Rodrigues
 * Copyright (C) 2005-2011 Luis Rodrigues
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 *
 * Contact
 * 	Address:
 *		Rua Alves Redol 9, Office 605
 *		1000-029 Lisboa
 *		PORTUGAL
 * 	Email:
 * 		ler@ist.utl.pt
 * 	Web:
 *		http://homepages.gsd.inesc-id.pt/~ler/
 * 
 */


import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

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
import net.sf.appia.protocols.common.RegisterSocketEvent;
import event.DeliverEvent;
import event.SendEvent;

/**
 * Session implementing the Print Application protocol. <br>
 * Reads strings, requests their printing and displays confirmations.
 * 
 * @author alexp
 */
public class StubbornLinkSession extends Session {

	public StubbornLinkSession(Layer layer) {
		super(layer);
	}

	public void handle(Event event) {
		System.out.println();
		
		if (event instanceof ChannelInit)
			handleChannelInit((ChannelInit) event);
		else if (event instanceof SendEvent)
			handleSendEvent((SendEvent) event);
		else if (event instanceof DeliverEvent)
			handleDeliverEvent((DeliverEvent) event);
	}

	private ProcessList processes;
	private ArrayList<SimpleMessage> sent = null;
	private Timer timer = null;
	private Channel channel = null;

	private void handleChannelInit(ChannelInit init) {
		channel = init.getChannel();
		sent = new ArrayList<SimpleMessage>();
		timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				sendMessage();
			}
		}, 0, 5000);
		
		
		try {
			init.go();
		} catch (AppiaEventException ex) {
			ex.printStackTrace();
		}


		try {
		      // sends this event to open a socket in the layer that is used has perfect
		      // point to point
		      // channels or unreliable point to point channels.
		      RegisterSocketEvent rse = new RegisterSocketEvent(init.getChannel(),
		          Direction.DOWN, this);
		      
		      InetSocketAddress address = processes.getSelf().getCompleteAddress();
		      
		      rse.port = address.getPort();
		      rse.localHost = address.getAddress();
		      
		      rse.go();
		    } catch (AppiaEventException e1) {
		      e1.printStackTrace();
		    }
		    System.out.println("Channel is open.");
	}

	private void handleSendEvent(SendEvent event) {
		SimpleMessage message = (SimpleMessage) event.getMessage().peekObject();
		sent.add(message);
		
		try {
			event.go();
		} catch (AppiaEventException e) {
			e.printStackTrace();
		}
	}
	
	private void sendMessage() {
		
		for( SimpleMessage message : sent ){
			SendEvent event = new SendEvent();
			
			
			
			try {
				event.init();
				event.asyncGo(channel, Direction.DOWN);
			} catch (AppiaEventException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void handleDeliverEvent(DeliverEvent event) {
		SimpleMessage s = (SimpleMessage)event.getMessage().popObject();
		String receivedMessage = s.getString();
		System.out.println("[Receiver: received message: " + receivedMessage + "]");
		DeliverEvent confirmationEvent = new DeliverEvent();
		confirmationEvent.setChannel(event.getChannel());
		confirmationEvent.setId(event.getId());
		Message m = new Message();
		m.pushObject(s);
		confirmationEvent.setMessage(m);
		confirmationEvent.setDir(Direction.DOWN);
		confirmationEvent.setSourceSession(this);
		confirmationEvent.setSendSource(processes[1]);
		confirmationEvent.setDest(processes[0]);
		try {
			confirmationEvent.init();
			confirmationEvent.go();
		} catch (AppiaEventException e) {
			e.printStackTrace();
		}
	}

	public void init(ProcessList processes) {
		this.processes = processes;
	}
}

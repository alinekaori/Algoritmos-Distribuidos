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
		if (event instanceof ChannelInit)
			handleChannelInit((ChannelInit) event);
		else if (event instanceof SendEvent)
			handleSendEvent((SendEvent) event);
		else if (event instanceof DeliverEvent)
			handleDeliverEvent((DeliverEvent) event);
	}

	private ProcessList processes;
	private ArrayList<Message> sent = null;
	private Timer timer = new Timer();
	private Channel channel = null;

	private void handleChannelInit(ChannelInit init) {
		channel = init.getChannel();
		sent = new ArrayList<Message>();
		timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				if( !sent.isEmpty() ){
					sendMessage();
				}
			}
		}, 0, 5000);
		
		
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

	private void handleSendEvent(SendEvent event) {
		Message message = event.getMessage();
		
		try {
			if( event.isOriginalMessage() ){
				sent.add((Message) message.clone());
			}
			
			event.go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void sendMessage() {
		SendEvent event = null;

		for( Message message : sent ){
			event = new SendEvent();
			event.isOriginalMessage(false);
			
			event.setSourceProcess(processes.getSelf());
			event.setDestProcess(processes.getOther());
			
			try {
				event.setMessage((Message)message.clone());
				event.asyncGo(channel, Direction.DOWN);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void handleDeliverEvent(DeliverEvent event) {
		try {
			event.go();
		} catch (AppiaEventException e) {
			e.printStackTrace();
		}
	}

	public void init(ProcessList processes) {
		this.processes = processes;
	}
}

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
import java.net.SocketAddress;

import model.SimpleMessage;
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
public class ReceiveApplicationSession extends Session {

	public ReceiveApplicationSession(Layer layer) {
		super(layer);
	}

	public void handle(Event event) {
		System.out.println();
		
		if (event instanceof ChannelInit)
			handleChannelInit((ChannelInit) event);
		else if (event instanceof SenderRequestEvent)
			handleSenderRequest((SenderRequestEvent) event);
	}

	private SocketAddress[] addresses;

	private void handleChannelInit(ChannelInit init) {
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
		      rse.port = ((InetSocketAddress) addresses[1]).getPort();
		      rse.localHost = ((InetSocketAddress)addresses[1]).getAddress();
		      rse.go();
		    } catch (AppiaEventException e1) {
		      e1.printStackTrace();
		    }
		    System.out.println("Channel is open.");
	}

	private void handleSenderRequest(SenderRequestEvent conf) {
		SimpleMessage s = (SimpleMessage)conf.getMessage().popObject();
		String receivedMessage = s.getString();
		System.out.println("[Receiver: received message: " + receivedMessage + "]");
		ReceiverConfirmEvent confirmationEvent = new ReceiverConfirmEvent();
		confirmationEvent.setChannel(conf.getChannel());
		confirmationEvent.setId(conf.getId());
		Message m = new Message();
		m.pushObject(s);
		confirmationEvent.setMessage(m);
		confirmationEvent.setDir(Direction.DOWN);
		confirmationEvent.setSourceSession(this);
		confirmationEvent.setSendSource(addresses[1]);
		confirmationEvent.setDest(addresses[0]);
		try {
			confirmationEvent.init();
			confirmationEvent.go();
		} catch (AppiaEventException e) {
			e.printStackTrace();
		}
	}

	public void init(SocketAddress[] addresses) {
		this.addresses = addresses;
	}
}

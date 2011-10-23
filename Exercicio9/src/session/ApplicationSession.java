package session;

import net.sf.appia.core.AppiaEventException;
import net.sf.appia.core.Event;
import net.sf.appia.core.Layer;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.channel.ChannelInit;
import event.TrustEvent;

/**
 * Session implementing the Print Application protocol. <br>
 * Reads strings, requests their printing and displays confirmations.
 * 
 * @author alexp
 */
public class ApplicationSession extends Session {

	public ApplicationSession(Layer layer) {
		super(layer);
	}

	public void handle(Event event) {
		if (event instanceof ChannelInit)
			handleChannelInit((ChannelInit) event);
		else if (event instanceof TrustEvent)
			handleTrust((TrustEvent) event);
	}

	private void handleChannelInit(ChannelInit init) {
		try {
			init.go();
		} catch (AppiaEventException ex) {
			ex.printStackTrace();
		}
	}

	private void handleTrust(TrustEvent event) {
		System.out.println(event.getLeader().getId());
	}
	
}
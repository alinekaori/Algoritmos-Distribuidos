package layer;

import net.sf.appia.core.Layer;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.channel.ChannelInit;
import session.ReceiverSession;
import event.DeliverEvent;
import event.SendEvent;


/**
 * Layer of the Print Application.
 * 
 * @author akt & kcg
 */
public class ReceiverLayer extends Layer {
	
  /** Creates a new instance of PrintApplicationLayer */
  public ReceiverLayer() {
    /* events that the protocol will create */
    evProvide = new Class[1];
    evProvide[0] = DeliverEvent.class;

    /*
     * events that the protocol requires to work This is a subset of the
     * accepted events.
     */
    evRequire = new Class[0];

    /* events that the protocol will accept */
    evAccept = new Class[2];
    evAccept[0] = SendEvent.class;
    evAccept[1] = ChannelInit.class;
  }

  public Session createSession() {
    return new ReceiverSession(this);
  }
}

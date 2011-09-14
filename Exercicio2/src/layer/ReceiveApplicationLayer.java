package layer;

import event.ReceiverConfirmEvent;
import event.SenderRequestEvent;
import session.ReceiveApplicationSession;
import net.sf.appia.core.Layer;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.channel.ChannelInit;


/**
 * Layer of the Print Application.
 * 
 * @author akt & kcg
 */
public class ReceiveApplicationLayer extends Layer {

  /** Creates a new instance of PrintApplicationLayer */
  public ReceiveApplicationLayer() {
    /* events that the protocol will create */
    evProvide = new Class[1];
    evProvide[0] = ReceiverConfirmEvent.class;

    /*
     * events that the protocol requires to work This is a subset of the
     * accepted events.
     */
    evRequire = new Class[0];

    /* events that the protocol will accept */
    evAccept = new Class[2];
    evAccept[0] = SenderRequestEvent.class;
    evAccept[1] = ChannelInit.class;
  }

  public Session createSession() {
    return new ReceiveApplicationSession(this);
  }
}

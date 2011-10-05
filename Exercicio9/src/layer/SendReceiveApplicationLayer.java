package layer;

import event.ReceiverConfirmEvent;
import event.SenderRequestEvent;
import session.SendReceiveApplicationSession;
import net.sf.appia.core.Layer;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.channel.ChannelInit;


/**
 * Layer of the Print Application.
 * 
 * @author akt & kcg
 */
public class SendReceiveApplicationLayer extends Layer {

  /** Creates a new instance of PrintApplicationLayer */
  public SendReceiveApplicationLayer() {
    /* events that the protocol will create */
    evProvide = new Class[2];
    evProvide[0] = SenderRequestEvent.class;
    evProvide[1] = ReceiverConfirmEvent.class;

    /*
     * events that the protocol requires to work This is a subset of the
     * accepted events.
     */
    evRequire = new Class[0];

    /* events that the protocol will accept */
    evAccept = new Class[3];
    evAccept[0] = ReceiverConfirmEvent.class;
    evAccept[1] = SenderRequestEvent.class;
    evAccept[2] = ChannelInit.class;
  }

  public Session createSession() {
    return new SendReceiveApplicationSession(this);
  }
}

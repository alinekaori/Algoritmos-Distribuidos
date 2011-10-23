package layer;

import event.TrustEvent;
import event.HeartbeatEvent;
import session.SendReceiveApplicationSession;
import net.sf.appia.core.Layer;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.channel.ChannelInit;


/**
 * Layer of the Print Application.
 * 
 * @author akt & kcg
 */
public class ApplicationLayer extends Layer {

  /** Creates a new instance of PrintApplicationLayer */
  public ApplicationLayer() {
    /* events that the protocol will create */
    evProvide = new Class[0];
    
    /*
     * events that the protocol requires to work This is a subset of the
     * accepted events.
     */
    evRequire = new Class[0];

    /* events that the protocol will accept */
    evAccept = new Class[2];
    evAccept[0] = TrustEvent.class;
    evAccept[1] = ChannelInit.class;
  }

  public Session createSession() {
    return new SendReceiveApplicationSession(this);
  }
}

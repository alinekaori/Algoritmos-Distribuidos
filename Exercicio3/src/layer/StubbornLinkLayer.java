package layer;

import net.sf.appia.core.Layer;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.channel.ChannelInit;
import session.StubbornLinkSession;
import event.DeliverEvent;
import event.SendEvent;


/**
 * Layer of the Print Application.
 * 
 * @author akt & kcg
 */
public class StubbornLinkLayer extends Layer {

  /** Creates a new instance of PrintApplicationLayer */
  public StubbornLinkLayer() {
    /* events that the protocol will create */
    evProvide = new Class[0];

    /*
     * events that the protocol requires to work This is a subset of the
     * accepted events.
     */
    evRequire = new Class[0];

    /* events that the protocol will accept */
    evAccept = new Class[3];
    evAccept[0] = DeliverEvent.class;
    evAccept[1] = ChannelInit.class;
    evAccept[2] = SendEvent.class;
  }

  public Session createSession() {
    return new StubbornLinkSession(this);
  }

}

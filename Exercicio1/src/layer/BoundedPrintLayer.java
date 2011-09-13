package layer;

import event.PrintAlarmEvent;
import event.PrintConfirmEvent;
import event.PrintRequestEvent;
import event.PrintStatusEvent;
import session.BoundedPrintSession;
import net.sf.appia.core.Layer;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.channel.ChannelInit;

/**
 * Layer of the Bounded Print protocol.
 * 
 * @author akt & kcg
 */
public class BoundedPrintLayer extends Layer {

  public BoundedPrintLayer() {
    /* events that the protocol will create */
    evProvide = new Class[2];
    evProvide[0] = PrintStatusEvent.class;
    evProvide[1] = PrintAlarmEvent.class;

    /*
     * events that the protocol requires to work. This is a subset of the
     * accepted events.
     */
    evRequire = new Class[1];
    evRequire[0] = PrintConfirmEvent.class;

    /* events that the protocol will accept */
    evAccept = new Class[3];
    evAccept[0] = PrintRequestEvent.class;
    evAccept[1] = PrintConfirmEvent.class;
    evAccept[2] = ChannelInit.class;
  }

  public Session createSession() {
    return new BoundedPrintSession(this);
  }
}

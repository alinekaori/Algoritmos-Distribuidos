package layer;


import event.PrintAlarmEvent;
import event.PrintConfirmEvent;
import event.PrintRequestEvent;
import event.PrintStatusEvent;
import session.PrintApplicationSession;
import net.sf.appia.core.Layer;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.channel.ChannelInit;


/**
 * Layer of the Print Application.
 * 
 * @author  akt & kcg
 */
public class PrintApplicationLayer extends Layer {

  /** Creates a new instance of PrintApplicationLayer */
  public PrintApplicationLayer() {
    /* events that the protocol will create */
    evProvide = new Class[1];
    evProvide[0] = PrintRequestEvent.class;

    /*
     * events that the protocol requires to work This is a subset of the
     * accepted events.
     */
    evRequire = new Class[0];

    /* events that the protocol will accept */
    evAccept = new Class[4];
    evAccept[0] = PrintConfirmEvent.class;
    evAccept[1] = PrintStatusEvent.class;
    evAccept[2] = PrintAlarmEvent.class;
    evAccept[3] = ChannelInit.class;
  }

  public Session createSession() {
    return new PrintApplicationSession(this);
  }
}

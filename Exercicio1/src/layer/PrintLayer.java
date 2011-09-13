package layer;


import event.PrintConfirmEvent;
import event.PrintRequestEvent;
import session.PrintSession;
import net.sf.appia.core.Layer;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.channel.ChannelInit;


/**
 * Layer of the Print protocol.
 * @author  akt & kcg
 */
public class PrintLayer extends Layer {

  public PrintLayer() {
    /* events that the protocol will create */
    evProvide = new Class[1];
    evProvide[0] = PrintConfirmEvent.class;

    /*
     * events that the protocol requires to work This is a subset of the
     * accepted events.
     */
    evRequire = new Class[0];

    /* events that the protocol will accept */
    evAccept = new Class[2];
    evAccept[0] = PrintRequestEvent.class;
    evAccept[1] = ChannelInit.class;
  }

  public Session createSession() {
    return new PrintSession(this);
  }
}

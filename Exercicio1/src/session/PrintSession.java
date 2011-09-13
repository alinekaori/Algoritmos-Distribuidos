package session;


import event.PrintConfirmEvent;
import event.PrintRequestEvent;
import net.sf.appia.core.AppiaEventException;
import net.sf.appia.core.Direction;
import net.sf.appia.core.Event;
import net.sf.appia.core.Layer;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.channel.ChannelInit;


/**
 * Session implementing the Print protocol.
 * <br>
 * Receives print requests and prints them on screen.
 * 
 * @author  akt & kcg
 */
public class PrintSession extends Session {

  public PrintSession(Layer layer) {
    super(layer);
  }

  public void handle(Event event) {
    if (event instanceof ChannelInit)
      handleChannelInit((ChannelInit) event);
    else if (event instanceof PrintRequestEvent) {
      handlePrintRequest((PrintRequestEvent) event);
    }
  }

  private void handleChannelInit(ChannelInit init) {
    try {
      init.go();
    } catch (AppiaEventException e) {
      e.printStackTrace();
    }
  }

  private void handlePrintRequest(PrintRequestEvent request) {
    try {
      PrintConfirmEvent ack = new PrintConfirmEvent();

      System.out.println();
      System.out.println("[Print] " + request.getString());
      request.go();

      ack.setChannel(request.getChannel()); // set the Appia channel where the
                                            // event will travel
      ack.setDir(Direction.UP); // set events direction
      ack.setSourceSession(this); // set the session that created the event
      ack.setId(request.getId()); // set the request ID
      // initializes the event, defining all internal structures,
      // for instance the path the event will take (sessions to visit)
      ack.init();
      ack.go(); // send the event
    } catch (AppiaEventException e) {
      e.printStackTrace();
    }
  }
}

package session;

import model.Status;
import net.sf.appia.core.AppiaEventException;
import net.sf.appia.core.Direction;
import net.sf.appia.core.Event;
import net.sf.appia.core.Layer;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.channel.ChannelInit;
import event.PrintAlarmEvent;
import event.PrintConfirmEvent;
import event.PrintRequestEvent;
import event.PrintStatusEvent;

/**
 * Session implementing the Bounded Print protocol.
 * <br>
 * After a defined number of print requests, an alarm
 * is sent and further request are denied.
 * 
 * @author  akt & kcg
 */
public class BoundedPrintSession extends Session {
  int bound;

  /** Creates a new instance of BoundedPrintSession */
  public BoundedPrintSession(Layer layer) {
    super(layer);
  }

  public void handle(Event event) {
    if (event instanceof ChannelInit) {
      handleChannelInit((ChannelInit) event);
    } else if (event instanceof PrintRequestEvent) {
      handlePrintRequest((PrintRequestEvent) event);
    } else if (event instanceof PrintConfirmEvent) {
      handlePrintConfirm((PrintConfirmEvent) event);
    }
  }

  private void handleChannelInit(ChannelInit init) {
    try {
      bound = 5;

      init.go();
    } catch (AppiaEventException e) {
      e.printStackTrace();
    }
  }

  private void handlePrintRequest(PrintRequestEvent request) {
    if (bound > 0) {
      bound = bound - 1;
      try {
        request.go();
      } catch (AppiaEventException e) {
        e.printStackTrace();
      }
      if (bound == 0) {
        PrintAlarmEvent alarm = new PrintAlarmEvent();
        alarm.setChannel(request.getChannel());
        alarm.setDir(Direction.UP);
        alarm.setSourceSession(this);
        try {
          alarm.init();
          alarm.go();
        } catch (AppiaEventException e) {
          e.printStackTrace();
        }
      }
    } else {
      PrintStatusEvent status = new PrintStatusEvent();
      status.setChannel(request.getChannel());
      status.setDir(Direction.UP);
      status.setSourceSession(this);
      status.setId(request.getId());
      status.setStatus(Status.NOK);
      try {
        status.init();
        status.go();
      } catch (AppiaEventException e) {
        e.printStackTrace();
      }
    }
  }

  private void handlePrintConfirm(PrintConfirmEvent conf) {
    PrintStatusEvent status = new PrintStatusEvent();
    status.setId(conf.getId());
    status.setStatus(Status.OK);

    try {
      status.setChannel(conf.getChannel());
      status.setDir(Direction.UP);
      status.setSourceSession(this);
      status.init();
      status.go();
    } catch (AppiaEventException e) {
      e.printStackTrace();
    }

  }
}

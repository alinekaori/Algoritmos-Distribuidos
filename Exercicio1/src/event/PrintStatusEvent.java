package event;


import model.Status;
import net.sf.appia.core.Event;

/**
 * Print status notification event.
 * @author  akt & kcg
 */
public class PrintStatusEvent extends Event {
  int rqid;
  Status status;

  public void setId(int rid) {
    rqid = rid;
  }

  public void setStatus(Status s) {
    status = s;
  }

  public int getId() {
    return rqid;
  }

  public Status getStatus() {
    return status;
  }
}

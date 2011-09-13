package event;


import net.sf.appia.core.Event;


/**
 * Print request confirmation event.
 * 
 * @author  akt & kcg
 */
public class PrintConfirmEvent extends Event {
  int rqid;

  public void setId(int rid) {
    rqid = rid;
  }

  public int getId() {
    return rqid;
  }
}

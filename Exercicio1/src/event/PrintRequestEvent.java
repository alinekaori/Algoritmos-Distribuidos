package event;


import net.sf.appia.core.Event;


/**
 * Print request event.
 * 
 * @author  akt & kcg
 */
public class PrintRequestEvent extends Event {
  int rqid;
  String str;

  public void setId(int rid) {
    rqid = rid;
  }

  public void setString(String s) {
    str = s;
  }

  public int getId() {
    return rqid;
  }

  public String getString() {
    return str;
  }
}

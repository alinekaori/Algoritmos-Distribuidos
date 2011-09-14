package event;

import java.net.SocketAddress;

import net.sf.appia.core.events.SendableEvent;


/**
 * Print request event.
 * 
 * @author akt & kcg
 */
public class SenderRequestEvent extends SendableEvent {
  static int rqid;
  static int str;

  public void setId(int rid) {
    rqid = rid;
  }

  public void setString(int s) {
    str = s;
  }

  public int getId() {
    return rqid;
  }

  public int getString() {  
	return str;
  }
  
  public void setDest(SocketAddress ad ){
	  dest = ad;
  }
  
  public void setSendSource(SocketAddress ad){
	  source = ad;
  }
  
}

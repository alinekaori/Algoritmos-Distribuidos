package event;

import java.net.SocketAddress;

import net.sf.appia.core.events.SendableEvent;


/**
 * Print request confirmation event.
 * 
 * @author akt & kcg
 */
public class ReceiverConfirmEvent extends SendableEvent {
  static int rqid;

  public void setId(int rid) {
    rqid = rid;
  }

  public int getId() {
    return rqid;
  }
  
  public void setDest(SocketAddress ad ){
	  dest = ad;
  }
  
  public void setSendSource(SocketAddress ad){
	  source = ad;
  }
}

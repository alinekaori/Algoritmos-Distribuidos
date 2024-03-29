package event;

import model.CustomProcess;
import net.sf.appia.core.Event;

/**
 * Print request confirmation event.
 * 
 * @author akt & kcg
 */
public class TrustEvent extends Event {
	CustomProcess leader = null;

	public CustomProcess getLeader() {
		return leader;
	}

	public void setLeader(CustomProcess leader) {
		this.leader = leader;
	}

}

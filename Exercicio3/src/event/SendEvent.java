package event;

import model.CustomProcess;
import model.SimpleMessage;
import net.sf.appia.core.events.SendableEvent;


/**
 * Print request event.
 * 
 * @author akt & kcg
 */
public class SendEvent extends SendableEvent {
	private CustomProcess processDest;
	private CustomProcess processSource;

	public CustomProcess getDestProcess() {
		return processDest;
	}
	
	public CustomProcess getSourceProcess() {
		return processSource;
	}

	public void setDestProcess(CustomProcess process) {
		this.processDest = process;
		
		dest = process.getCompleteAddress();
	}
	
	public void setSourceProcess(CustomProcess process) {
		this.processSource = process;
		
		source = process.getCompleteAddress();
	}

	public int getId() {
		String procId = Integer.toString(processSource.getId());
		
		SimpleMessage message = (SimpleMessage) getMessage().peekObject();
		String messageId = Integer.toString(message.getId());
		
		return Integer.parseInt(procId + messageId);
	}
}

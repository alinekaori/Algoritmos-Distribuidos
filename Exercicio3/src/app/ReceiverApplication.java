package app;


import layer.ReceiverLayer;
import model.CustomProcess;
import model.ProcessList;
import net.sf.appia.core.Appia;
import net.sf.appia.core.AppiaCursorException;
import net.sf.appia.core.AppiaDuplicatedSessionsException;
import net.sf.appia.core.AppiaInvalidQoSException;
import net.sf.appia.core.Channel;
import net.sf.appia.core.ChannelCursor;
import net.sf.appia.core.Layer;
import net.sf.appia.core.QoS;
import net.sf.appia.protocols.udpsimple.UdpSimpleLayer;
import session.ReceiverSession;

/**
 * This is the MAIN class to run the Print protocols.
 * 
 * @author akt & kcg
 */
public class ReceiverApplication {

	private static int PROC_ID_SENDER = 0;
	private static int PROC_ID_RECEIVER = 1;
	private static String ADDR_SENDER = "localhost";
	private static String ADDR_RECEIVER = "localhost";
	private static int PORT_SENDER = 8080;
	private static int PORT_RECEIVER = 9090;
	
	public static void main(String[] args) {
		
		if ( args == null || args.length == 0 || args.length > 1 ){
			System.out.println("Invalid arguments.");
			System.exit(0);
		}
	    
	    /* Create layers and put them on a array */
	    Layer[] qos = {new UdpSimpleLayer(), new ReceiverLayer()};

	    /* Create a QoS */
	    QoS myQoS = null;
	    try {
	      myQoS = new QoS("UDP Simple Example", qos);
	    } catch (AppiaInvalidQoSException ex) {
	      System.err.println("Invalid QoS");
	      System.err.println(ex.getMessage());
	      System.exit(1);
	    }
	    
	    Channel channel = myQoS
	        .createUnboundChannel("UDP Simple Channel");
	    
	    ReceiverSession rs = (ReceiverSession) qos[qos.length - 1]
	        .createSession();
	    rs.init(buildProcessSet(args[0]));
	    ChannelCursor cc = channel.getCursor();
	    
	    try {
	      cc.top();
	      cc.setSession(rs);
	    } catch (AppiaCursorException ex) {
	      System.err.println("Unexpected exception in main. Type code:" + ex.type);
	      System.exit(1);
	    }
	    
		try {

			channel.start();
		} catch (AppiaDuplicatedSessionsException ex) {
			System.err.println("Error in start");
			System.exit(1);
		}

		System.out.println("Starting Appia...");
		Appia.run();
	}

	private static ProcessList buildProcessSet(String arg) {
		int current = Integer.parseInt(arg);
		ProcessList processes = new ProcessList();

		processes.add(new CustomProcess(PROC_ID_RECEIVER, PORT_RECEIVER, ADDR_RECEIVER));
		processes.add(new CustomProcess(PROC_ID_SENDER, PORT_SENDER, ADDR_SENDER)); 
		
		processes.getProcessById(current).setSelf(true);
		
		return processes;
	}

}

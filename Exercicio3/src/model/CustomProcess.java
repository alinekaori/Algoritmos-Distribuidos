package model;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class CustomProcess {
	
	private int id;
	private boolean isSelf;
	private InetSocketAddress completeAddress = null;
		
	public CustomProcess(int id, int port, String address) {
		super();
		this.id = id;
		this.isSelf = false;
		
		createAddress(port, address);
	}
	
	private InetSocketAddress createAddress(int port, String address) {
		try {
			completeAddress = new InetSocketAddress(
					InetAddress.getByName(address),
					port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		return completeAddress;
	}

	public boolean isSelf() {
		return isSelf;
	}
	
	public void setSelf(boolean isSelf) {
		this.isSelf = isSelf;
	}
	
	public int getId() {
		return id;
	}
	
	public InetSocketAddress getCompleteAddress() {
		return completeAddress;
	}

}

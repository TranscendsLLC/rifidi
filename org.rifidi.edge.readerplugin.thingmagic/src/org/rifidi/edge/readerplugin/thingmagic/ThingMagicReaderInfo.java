package org.rifidi.edge.readerplugin.thingmagic;

import org.rifidi.edge.core.readerplugin.ReaderInfo;

/**
 * @author Jerry Maine - jerry@pramari.com
 *
 */
public class ThingMagicReaderInfo implements ReaderInfo {

	String ipAddress;
	int port;
	
	/**
	 * @return the ipAddress
	 */
	@Override()
	public String getIpAddress() {
		return ipAddress;
	}
	/**
	 * @param ipAddress the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	/**
	 * @return the port
	 */
	@Override()
	public int getPort() {
		return port;
	}
	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}
	
}

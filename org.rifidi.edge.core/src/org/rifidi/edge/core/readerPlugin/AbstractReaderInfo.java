package org.rifidi.edge.core.readerPlugin;

import java.io.Serializable;

public abstract class AbstractReaderInfo implements Serializable {

	private int port;

	private String ip;

	/**
	 * @return The IP address of the Reader
	 */
	public String getIPAddress() {
		return ip;
	}

	/**
	 * @param ipAddress The IP address of the Reader
	 */
	public void setIPAddress(String ipAddress) {
		ip = ipAddress;
	}

	/**
	 * @return The IP port of the reader
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port The IP port of the reader
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return A string version of the reader.
	 */
	public abstract String getReaderType();
	
}

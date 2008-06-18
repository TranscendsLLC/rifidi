package org.rifidi.edge.core.readerPlugin;

import java.io.Serializable;

public abstract class AbstractReaderInfo implements Serializable {

	private int port;

	private String ip;

	public String getIPAddress() {
		return ip;
	}

	public void setIPAddress(String ipAddress) {
		ip = ipAddress;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public abstract String getReaderType();
}

package org.rifidi.edge.core.readerplugin;

import java.io.Serializable;

public abstract class ReaderInfo implements Serializable {

	private String ipAddress;
	private int port;

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}

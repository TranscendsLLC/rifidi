package org.rifidi.edge.core.readerAdapter;

public abstract class AbstractConnectionInfo {

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

	//TODO Think about the sense of this method
	public abstract Class<? extends AbstractConnectionInfo> getReaderAdapterType();
}

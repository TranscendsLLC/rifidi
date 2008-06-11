package org.rifidi.edge.core.readerAdapter;

public abstract class AbstractConnectionInfo {
	
	public String getIPAddress() {
		return null;
	}

	public void setIPAddress(String IPAddress) {

	}

	public int getPort() {
		return 0;
	}

	public void setPort(int port) {

	}

	public abstract Class<? extends AbstractConnectionInfo> getReaderAdapterType();
}

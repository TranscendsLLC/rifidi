package org.rifidi.edge.core.readerAdapter;

import java.io.Serializable;

public abstract class AbstractConnectionInfo implements Serializable {

	private int port;

	private String ip;

	private String readerType;

	private String readerDescription;

	public String getReaderDescription() {
		return readerDescription;
	}

	public void setReaderDescription(String readerDescription) {
		this.readerDescription = readerDescription;
	}

	public String getReaderType() {
		return readerType;
	}

	public void setReaderType(String readerType) {
		this.readerType = readerType;
	}

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

	// TODO Think about the sense of this method
	@Deprecated
	public abstract Class<? extends AbstractConnectionInfo> getReaderAdapterType();
}

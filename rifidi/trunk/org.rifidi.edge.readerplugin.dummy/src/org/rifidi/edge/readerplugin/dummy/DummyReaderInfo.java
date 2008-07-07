package org.rifidi.edge.readerplugin.dummy;

import org.rifidi.edge.core.readerplugin.ReaderInfo;

public class DummyReaderInfo implements ReaderInfo {

	private String IpAddress;
	private int port;

	public void setIpAddress(String ipAddress) {
		IpAddress = ipAddress;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public String getIpAddress() {
		return IpAddress;
	}

	@Override
	public int getPort() {
		return port;
	}

}

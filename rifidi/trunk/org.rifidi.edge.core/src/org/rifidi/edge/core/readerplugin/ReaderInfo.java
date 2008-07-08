package org.rifidi.edge.core.readerplugin;

import java.io.Serializable;

public abstract class ReaderInfo implements Serializable {

	private String ipAddress;
	private int port;

	private long reconnectionIntervall;
	
	private int maxNumConnectionsAttemps;
	
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

	/**
	 * @return the reconnectionIntervall
	 */
	public long getReconnectionIntervall() {
		return reconnectionIntervall;
	}

	/**
	 * @param reconnectionIntervall the reconnectionIntervall to set
	 */
	public void setReconnectionIntervall(long reconnectionIntervall) {
		this.reconnectionIntervall = reconnectionIntervall;
	}

	/**
	 * @return the maxNumConnectionsAttemps
	 */
	public int getMaxNumConnectionsAttemps() {
		return maxNumConnectionsAttemps;
	}

	/**
	 * @param maxNumConnectionsAttemps the maxNumConnectionsAttemps to set
	 */
	public void setMaxNumConnectionsAttemps(int maxNumConnectionsAttemps) {
		this.maxNumConnectionsAttemps = maxNumConnectionsAttemps;
	}

}

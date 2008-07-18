package org.rifidi.edge.core.readerplugin.connectionmanager;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Container for Input and OutputStream of a Connection
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public class ConnectionStreams {

	private InputStream inputStream;
	private OutputStream outputStream;

	/**
	 * Create a new ConnectionStreams
	 * 
	 * @param inputStream
	 *            InputStream of the connection (Socket.getInputStream())
	 * @param outputStream
	 *            OutputStream of the connection (Socket.getOutputStream())
	 */
	public ConnectionStreams(InputStream inputStream, OutputStream outputStream) {
		this.inputStream = inputStream;
		this.outputStream = outputStream;
	}

	/**
	 * Get the InputStream of the connection
	 * 
	 * @return InputStream
	 */
	public InputStream getInputStream() {
		return inputStream;
	}

	/**
	 * Get the OutputStream of the connection
	 * 
	 * @return OutputStream
	 */
	public OutputStream getOutputStream() {
		return outputStream;
	}

}

package org.rifidi.edge.core.readerplugin.connectionmanager;

import java.io.InputStream;
import java.io.OutputStream;

public class ConnectionStreams {

	private InputStream inputStream;
	private OutputStream outputStream;

	public ConnectionStreams(InputStream inputStream, OutputStream outputStream) {
		this.inputStream = inputStream;
		this.outputStream = outputStream;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

}

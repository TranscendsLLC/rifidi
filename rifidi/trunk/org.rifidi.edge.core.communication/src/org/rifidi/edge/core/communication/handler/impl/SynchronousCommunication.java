package org.rifidi.edge.core.communication.handler.impl;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.SynchronousQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.buffer.ConnectionBuffer;
import org.rifidi.edge.core.communication.buffer.impl.ConnectionBufferImpl;
import org.rifidi.edge.core.communication.handler.Communication;
import org.rifidi.edge.core.communication.protocol.Protocol;
import org.rifidi.edge.core.communication.threads.ReadWriteThread;

public class SynchronousCommunication implements Communication{

	private Log logger = LogFactory.getLog(SynchronousCommunication.class);

	private Socket socket;
	private Protocol protocol;

	private SynchronousQueue<Object> readQueue;
	private SynchronousQueue<Object> writeQueue;

	private ReadWriteThread readWriteThread;

	public SynchronousCommunication(Socket socket, Protocol protocol) {
		this.socket = socket;
		this.protocol = protocol;

		this.readQueue = new SynchronousQueue<Object>();
		this.writeQueue = new SynchronousQueue<Object>();
	}

	@Override
	public ConnectionBuffer startCommunication() throws IOException {
		logger.debug("Trying to start communications");

		this.readWriteThread = new ReadWriteThread("ReadWriteThread "
				+ socket.getInetAddress().toString() + ":" + socket.getPort(),
				protocol, socket.getInputStream(), socket.getOutputStream(),
				readQueue, writeQueue);

		ConnectionBuffer connectionBuffer = new ConnectionBufferImpl(readQueue,
				writeQueue);

		readWriteThread.start();

		// FIXME ExceptionHandling
		// readThread.setUncaughtExceptionHandler(connectionBuffer);
		// writeThread.setUncaughtExceptionHandler(connectionBuffer);

		return connectionBuffer;
	}

	@Override
	public void stopCommunication() throws IOException {
		readWriteThread.stop();

		logger.debug("Disconnecting: " + socket.getInetAddress() + ":"
				+ socket.getPort() + " ...");

		socket.close();
	}

}

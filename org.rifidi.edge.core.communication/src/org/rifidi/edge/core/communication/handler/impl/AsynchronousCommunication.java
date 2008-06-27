/*
 *  AsynchronousConnectionBuffer.java
 *
 *  Created:	Jun 27, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.core.communication.handler.impl;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.buffer.impl.AsynchronousConnectionBufferImpl;
import org.rifidi.edge.core.communication.handler.Communication;
import org.rifidi.edge.core.communication.protocol.Protocol;
import org.rifidi.edge.core.communication.service.impl.CommunicationServiceImpl;
import org.rifidi.edge.core.communication.threads.ReadThread;
import org.rifidi.edge.core.communication.threads.WriteThread;

public class AsynchronousCommunication implements Communication{
	private static final Log logger = LogFactory
			.getLog(CommunicationServiceImpl.class);

	private ReadThread readThread;
	private WriteThread writeThread;

	private LinkedBlockingQueue<Object> readQueue;
	private LinkedBlockingQueue<Object> writeQueue;

	private Socket socket;
	private Protocol protocol;

	/**
	 * Creates a communication to a reader.
	 * 
	 * @param socket
	 *            The socket this communication class will use.
	 * @param protocol
	 *            The adapter supplied protocol handler.
	 */
	public AsynchronousCommunication(Socket socket, Protocol protocol) {

		this.socket = socket;
		this.protocol = protocol;

		readQueue = new LinkedBlockingQueue<Object>();
		writeQueue = new LinkedBlockingQueue<Object>();
	}

	/**
	 * Starts the communication with a reader
	 * 
	 * @return ICommunicationConnection The connection to talk with the reader.
	 * @throws IOException
	 */
	@Override
	public AsynchronousConnectionBufferImpl startCommunication() throws IOException {
		logger.debug("Trying to start communications");

		readThread = new ReadThread("Read Thread: ("
				+ socket.getInetAddress().toString() + ":" + socket.getPort()
				+ ")", protocol, readQueue, socket.getInputStream());

		writeThread = new WriteThread("Write Thread: ("
				+ socket.getInetAddress().toString() + ":" + socket.getPort()
				+ ")", protocol, writeQueue, socket.getOutputStream());

		AsynchronousConnectionBufferImpl connectionBuffer = new AsynchronousConnectionBufferImpl(
				readQueue, writeQueue);

		readThread.start();
		writeThread.start();

		readThread.setUncaughtExceptionHandler(connectionBuffer);
		writeThread.setUncaughtExceptionHandler(connectionBuffer);

		return connectionBuffer;
	}

	/**
	 * Stops a communication to a reader.
	 * 
	 * @throws IOException
	 */
	@Override
	public void stopCommunication() throws IOException {
		readThread.stop();
		writeThread.stop();

		logger.debug("Disconnecting: " + socket.getInetAddress() + ":"
				+ socket.getPort() + " ...");

		socket.close();
	}

}

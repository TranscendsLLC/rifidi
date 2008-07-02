/*
 *  ReadThread.java
 *
 *  Created:	Jun 27, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.core.communication.threads;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.protocol.Protocol;
import org.rifidi.edge.common.utilities.thread.AbstractThread;


/**
 * @author Jerry Maine - jerry@pramari.com
 *
 */
public class ReadThread extends AbstractThread {
	private static final Log logger = LogFactory.getLog(ReadThread.class);

	private Protocol protocol;
	private LinkedBlockingQueue<Object> readQueue;

	private InputStream inputStream;

	/**
	 * Instantiates a asynchronous read thread.
	 * @param threadName The Name of the thread.
	 * @param protocol The plugin provided protocol used to transform the byte stream into objects.
	 * @param readQueue The blocking queue used to push the objects created from the protocol to the adapter.
	 * @param inputStream An input stream from a socket.
	 */
	public ReadThread(String threadName, Protocol protocol,
			LinkedBlockingQueue<Object> readQueue, InputStream inputStream) {
		super(threadName);
		this.protocol = protocol;
		this.readQueue = readQueue;
		this.inputStream = inputStream;

	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		logger.debug("Starting Read thread");
		try {
			while (running) {
				byte[] input = readFromSocket(inputStream);
				// TODO Think about what to do if the byte array is empty
				List<Object> msg = protocol.toObject(input);
				for (Object o : msg) {
					logger.debug(o);
					readQueue.add(o);
				}
			}
		//TODO: Think about catching SocketTimeOutException separately.
			//TODO: where is the Interrupted exception handler?
		} catch (IOException e) {
			running = false;
			Thread.currentThread().getUncaughtExceptionHandler()
					.uncaughtException(Thread.currentThread(), e);
		}
	}

	
	/**
	 * This reads the socket until nothing is available any more.
	 * @param inputStream A stream from a socket
	 * @return A byte array from the stream
	 * @throws IOException
	 */
	private byte[] readFromSocket(InputStream inputStream) throws IOException {
		int input;
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		//TODO need to find a way so that the we can return if and only if we have a complete message.
		while ((input = inputStream.read()) != -1) {
			buffer.write(input);

			// TODO Not sure this is going to work for every reader
			// TODO I don't like this - <3 Matt
			if (inputStream.available() == 0)
				break;
		}
		return buffer.toByteArray();
	}

}

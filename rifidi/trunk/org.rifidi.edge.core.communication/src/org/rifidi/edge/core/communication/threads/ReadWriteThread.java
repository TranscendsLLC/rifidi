/*
 *  ReadWriteThread.java
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
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.common.utilities.thread.AbstractThread;
import org.rifidi.edge.core.communication.protocol.Protocol;

/**
 * This is the Read/Write Thread used for synchronous communication
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public class ReadWriteThread extends AbstractThread {

	/**
	 * Standard logging system
	 */
	private Log logger = LogFactory.getLog(ReadWriteThread.class);

	/**
	 * Read and Write Queue for Commands (reader specific)
	 */
	private BlockingQueue<Object> readQueue;
	private BlockingQueue<Object> writeQueue;

	/**
	 * Reader Protocol to translate Bytes to Messages and vice versa
	 */
	private Protocol protocol;

	/**
	 * Input and Output from the Socket to recieve or send the byte messages
	 */
	private InputStream in;
	private OutputStream out;

	/**
	 * Constructor
	 * 
	 * @param threadName
	 *            Name of the Thread
	 * @param protocol
	 *            Reader specific translation from bytes to messages and vice
	 *            versa
	 * @param in
	 *            InputStream from the physical connection to read bytes from
	 * @param out
	 *            OutputStream from the physical connection to write bytes to
	 * @param readQueue
	 *            Output to the ReaderPlugin all incomming messages in reader
	 *            specific message format
	 * @param writeQueue
	 *            Input from the ReaderPlugin for all outgoing messages in
	 *            reader specific message format
	 */
	public ReadWriteThread(String threadName, Protocol protocol,
			InputStream in, OutputStream out, BlockingQueue<Object> readQueue,
			BlockingQueue<Object> writeQueue) {
		super(threadName);
		this.readQueue = readQueue;
		this.writeQueue = writeQueue;
		this.in = in;
		this.out = out;
		this.protocol = protocol;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		logger.debug("Starting ReadAndWrite Thread");
		try {
			while (running) {
				// Send the next Message from the queue over the wire
				sendData(writeQueue.take());
				// read answer coresponding the the message send
				readQueue.add(readData());
			}
		} catch (InterruptedException e) {
			running = false;
		} catch (IOException e) {
			running = false;
			Thread.currentThread().getUncaughtExceptionHandler()
					.uncaughtException(Thread.currentThread(), e);
		}

	}

	/**
	 * send data to the socket
	 * 
	 * @param message
	 *            message to send
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private void sendData(Object message) throws InterruptedException,
			IOException {
		logger.debug("Sending Message " + message);
		byte[] bytes = protocol.fromObject(message);
		out.write(bytes);
		out.flush();
	}

	/**
	 * read data from the socket
	 * 
	 * @return message from the socket (reader specific)
	 * @throws IOException
	 */
	private Object readData() throws IOException {
		byte[] input = readFromSocket(in);

		String omg = new String(input);
		omg.replace('\0', '?');
		logger.debug("reading from socket: " + omg);

		// TODO Think about what to do if the byte array is empty
		List<Object> msg = protocol.toObject(input);
		if (msg.size() > 1) {
			throw new IOException("To many message from reader");
		}

		if (msg.isEmpty())
			throw new IOException("No repsonse recieved");
		return msg.get(0);
	}

	/**
	 * read bytes arrays from the input stream
	 * 
	 * @param inputStream
	 *            input stream to read from
	 * @return byte array
	 * @throws IOException
	 */
	private byte[] readFromSocket(InputStream inputStream) throws IOException {
		int input;
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		while ((input = inputStream.read()) != -1) {
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
			}
			buffer.write(input);

			// TODO Not sure this is going to work for every reader
			if (inputStream.available() == 0)
				break;
		}
		return buffer.toByteArray();
	}

}

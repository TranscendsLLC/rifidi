/*
 *  WriteThread.java
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

import java.io.OutputStream;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.impl.ConnectionImpl;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionExceptionListener;
import org.rifidi.edge.core.readerplugin.protocol.CommunicationProtocol;

public class WriteThread extends AbstractThread {
	private static final Log logger = LogFactory.getLog(WriteThread.class);

	private CommunicationProtocol protocol;
	private LinkedBlockingQueue<Object> writeQueue;
	private OutputStream outputStream;
	private boolean ignoreExceptions = false;
	private ConnectionExceptionListener connectionExceptionListener;

	public WriteThread(String threadName, ConnectionExceptionListener connectionExceptionListener,
			CommunicationProtocol protocol, Queue<Object> writeQueue,
			OutputStream outputStream) {
		super(threadName);

		this.protocol = protocol;
		// TODO make this more safe
		this.writeQueue = (LinkedBlockingQueue<Object>) writeQueue;
		this.outputStream = outputStream;
		this.connectionExceptionListener = connectionExceptionListener;
	}

	public void run() {
		// logger.debug("Starting Write thread");
		try {
			while (running) {

				Object object = writeQueue.take();
				logger.debug(object);
				byte[] bytes = protocol.messageToByte(object);

				outputStream.write(bytes);
				outputStream.flush();
			}
		} catch (InterruptedException e) {
			running = false;
			// e.printStackTrace();
		} catch (Exception e) {
			running = false;
			e.printStackTrace();
			if (!ignoreExceptions) {
				connectionExceptionListener.connectionExceptionEvent(e);
			}
		}
	}

}

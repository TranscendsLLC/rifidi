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

import java.io.InputStream;
import java.util.Queue;

import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionExceptionListener;
import org.rifidi.edge.core.readerplugin.protocol.CommunicationProtocol;

public class ReadThread extends AbstractThread {
	private static final org.apache.commons.logging.Log logger = LogFactory
			.getLog(ReadThread.class);

	private CommunicationProtocol protocol;
	private Queue<Object> readQueue;

	private InputStream inputStream;
	
	private ConnectionExceptionListener connectionExceptionListener;

	public ReadThread(String threadName,
			ConnectionExceptionListener connectionExceptionListener,
			CommunicationProtocol protocol, Queue<Object> readQueue,
			InputStream inputStream) {
		super(threadName);
		this.protocol = protocol;
		this.readQueue = readQueue;
		this.inputStream = inputStream;
		this.connectionExceptionListener = connectionExceptionListener;
	}

	@Override
	public void run() {
		logger.debug("Starting: " + Thread.currentThread().getName());
		try {
			Object message = null;
			int input;
			while ((input = inputStream.read()) != -1 && running) {
				message = protocol.byteToMessage((byte) input);
				if (message != null) {
					logger.debug(message);
					readQueue.add(message);
				}
			}
			if (!ignoreExceptions) {
				connectionExceptionListener.connectionExceptionEvent(new RifidiConnectionException(
						"Socket was closed by client"));
			}
		} catch (Exception e) {
			running = false;
			e.printStackTrace();
			if (!ignoreExceptions) {
				connectionExceptionListener.connectionExceptionEvent(e);
			}
		}
		logger.debug("Stopping: " + Thread.currentThread().getName());
	}

}

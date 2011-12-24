/*
 * AwidEndpoint.java
 * 
 * Created:     Oct 20th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:     The software in this package is published under the terms of the EPL License
 *                   A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.adapter.awid.awid2010.communication;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.sensors.ByteMessage;
import org.rifidi.edge.sensors.sessions.IPSessionEndpoint;

/**
 * This class subscribes to the session and receives and processes incoming Awid
 * Messages
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AwidEndpoint implements IPSessionEndpoint {

	/** Queue for incoming responses from the Alien reader */
	private final LinkedBlockingQueue<ByteMessage> messageQueue;
	/** The logger for this class */
	private static final Log logger = LogFactory.getLog(AwidEndpoint.class);
	/** True if we have seen a welcome message */
	private final AtomicBoolean isConnected = new AtomicBoolean(false);
	/** The default timeout in ms */
	private int timeout;

	/**
	 * Constructor
	 * 
	 * @param timeout
	 *            the default amount of time to wait on responses in ms.
	 */
	public AwidEndpoint(int timeout) {
		messageQueue = new LinkedBlockingQueue<ByteMessage>();
		this.timeout = timeout;
	}

	@Override
	public void handleMessage(ByteMessage message) {
		if (isWelcomMessage(message)) {
			if (isConnected.compareAndSet(false, true)) {
				logger.debug("Welcome Message received");
			} else {
				logger.debug("Welcome Message received, "
						+ "but already connected");
			}
		} else {
			messageQueue.add(message);
		}

	}

	/**
	 * A private helper method that determines if the received message is a
	 * welcome message
	 * 
	 * @param message
	 * @return
	 */
	private boolean isWelcomMessage(ByteMessage message) {
		byte[] bytes = message.message;
		if (bytes.length > 2) {
			return (bytes[0] == (byte) 'i' && bytes[1] == (byte) 'i');
		} else {
			return false;
		}
	}

	/**
	 * Receive a message. This method blocks until the message is received or
	 * until the default amount of time has expired.
	 * 
	 * @return The next message from the reader
	 * @throws IOException
	 * @throws TimeoutException
	 *             if the timeout has expired while waiting.
	 */
	public ByteMessage receiveMessage() throws IOException, TimeoutException {
		return receiveMessage(timeout);
	}

	/**
	 * Receive a message. This method blocks until the message is received or
	 * until the given amount of time has expired.
	 * 
	 * @param timeout
	 *            the time to wait for a response in milliseconds
	 * @return The next message from the reader
	 * @throws IOException
	 * @throws TimeoutException
	 *             if the timeout has expired while waiting.
	 */
	public ByteMessage receiveMessage(int timeout) throws IOException,
			TimeoutException {
		try {
			if(timeout<=0){
				logger.warn("Timeout is less than 0");
			}
			ByteMessage response = messageQueue.poll(timeout,
					TimeUnit.MILLISECONDS);
			if (response != null){
				return response;
			}
			else
				throw new TimeoutException(
						"Timed out while waiting for a response");
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IOException();
		}
	}

	/**
	 * Return true if we have seen the welcome message
	 * 
	 * @return
	 */
	public boolean isConnected() {
		return isConnected.get();
	}

	/**
	 * Reset the welcome message has been seen flag.
	 */
	public void disconnect() {
		isConnected.set(false);
	}
	
	public void clearUndeliveredMessages(){
		messageQueue.clear();
	}
	
	public boolean isEmpty(){
		return messageQueue.isEmpty();
	}

}

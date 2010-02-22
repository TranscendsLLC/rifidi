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
package org.rifidi.edge.readerplugin.awid.awid2010.communication;

import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.sensors.messages.ByteMessage;
import org.rifidi.edge.core.sensors.sessions.pubsub.IPSessionEndpoint;
import org.rifidi.edge.readerplugin.awid.awid2010.communication.commands.AbstractAwidCommand;
import org.rifidi.edge.readerplugin.awid.awid2010.communication.messages.AbstractAwidMessage;
import org.rifidi.edge.readerplugin.awid.awid2010.communication.messages.AckMessage;
import org.rifidi.edge.readerplugin.awid.awid2010.communication.messages.IncomingAwidMessageFactory;
import org.rifidi.edge.readerplugin.awid.awid2010.communication.messages.InvalidAwidMessageException;
import org.rifidi.edge.readerplugin.awid.awid2010.communication.messages.WelcomeMessage;

/**
 * This class subscribes to the session and receives and processes incoming Awid
 * Messages
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AwidEndpoint implements IPSessionEndpoint {

	/** A factory that produces Awid Messages from byte arrays */
	private final IncomingAwidMessageFactory factory;
	/** A Queue of commands awaiting Acknowledgements */
	private final Queue<AbstractAwidCommand> commandsAwaitingAck;
	/** The logger for this class */
	private static final Log logger = LogFactory.getLog(AwidEndpoint.class);
	/** True if we have seen a welcome message */
	private final AtomicBoolean isConnected = new AtomicBoolean(false);
	/** An object that processes tag messages */
	private final AwidTagHandler tagHandler;

	/**
	 * Constructor
	 * 
	 * @param tagHandler
	 *            An object that processes tag messages
	 * @param readerID
	 *            The ID of the reader that created the session.
	 */
	public AwidEndpoint(final AwidTagHandler tagHandler, final String readerID) {
		factory = new IncomingAwidMessageFactory(readerID);
		commandsAwaitingAck = new ConcurrentLinkedQueue<AbstractAwidCommand>();
		this.tagHandler = tagHandler;
	}

	@Override
	public void handleMessage(ByteMessage message) {
		try {
			// first thing is to create an AwidMessage from the bytes
			AbstractAwidMessage awidMessage = factory
					.getMessage(message.message);
			if (awidMessage instanceof AckMessage) {
				AckMessage ackMsg = (AckMessage) awidMessage;
				try {
					if (ackMsg.isSuccessful()) {
						logger.debug("Command Acknowledge: "
								+ commandsAwaitingAck.remove());
					} else {
						logger.warn("Command Error: "
								+ commandsAwaitingAck.remove());
					}
				} catch (NoSuchElementException ex) {
					logger.warn("ACK message received for unknown command");
				}
			} else if (awidMessage instanceof WelcomeMessage) {
				if (isConnected.compareAndSet(false, true)) {
					logger.debug("Welcome Message received");
				} else {
					logger.debug("Welcome Message received, "
							+ "but already connected");
				}
			} else if (awidMessage instanceof TagResponseMessage) {
				this.tagHandler
						.handleTagEvent((TagResponseMessage) awidMessage);
			}

		} catch (InvalidAwidMessageException e) {
			logger.warn("Invalid Awid Message received");
			throw new IllegalStateException(e);
		}

	}

	/**
	 * Add an awid command to the queue of commands that should be ackowledged
	 * 
	 * @param command
	 */
	public void listenForAck(AbstractAwidCommand command) {
		this.commandsAwaitingAck.add(command);
	}

	/**
	 * Remove a command from the queue of commands to be ackowledged
	 * 
	 * @param command
	 */
	public void stopListeningForAck(AbstractAwidCommand command) {
		this.commandsAwaitingAck.remove(command);
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

}

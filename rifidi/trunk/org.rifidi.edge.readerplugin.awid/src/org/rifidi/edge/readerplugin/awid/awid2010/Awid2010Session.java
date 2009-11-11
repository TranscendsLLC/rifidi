/*
 * Awid2010Session.java
 * 
 * Created:     Oct 20th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:     The software in this package is published under the terms of the EPL License
 *                   A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.readerplugin.awid.awid2010;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.sensors.commands.Command;
import org.rifidi.edge.core.sensors.messages.ByteMessage;
import org.rifidi.edge.core.sensors.sessions.MessageParsingStrategyFactory;
import org.rifidi.edge.core.sensors.sessions.pubsub.AbstractPubSubIPSensorSession;
import org.rifidi.edge.core.services.notification.NotifierService;
import org.rifidi.edge.readerplugin.awid.awid2010.communication.AwidEndpoint;
import org.rifidi.edge.readerplugin.awid.awid2010.communication.AwidTagHandler;
import org.rifidi.edge.readerplugin.awid.awid2010.communication.commands.AbstractAwidCommand;
import org.rifidi.edge.readerplugin.awid.awid2010.communication.commands.StopCommand;
import org.springframework.jms.core.JmsTemplate;

/**
 * The session that provides communication with the Awid reader. It extends the
 * AbstratPubSubIPSensorSession so that the reading and writing to and from TCP
 * sockets is handled. Incoming messages from the reader are sent to registered
 * listeners, in this case the AwidEndpoint.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class Awid2010Session extends AbstractPubSubIPSensorSession {

	/** The factory that produces the MessageParsingStrategy */
	private final Awid2010MessageParsingStrategyFactory parsingStratFac;
	/** The endpoint handles incoming messages */
	private final AwidEndpoint awidEndpoint;
	/** The logger for this class */
	private static final Log logger = LogFactory.getLog(Awid2010Session.class);
	/** Sends out JMS notifications about the state */
	private final NotifierService notifierService;

	/**
	 * Constructor
	 * 
	 * @param sensor
	 *            The sensor that produced this session
	 * @param ID
	 *            The ID of this session
	 * @param host
	 *            The IP address of the awid
	 * @param port
	 *            The port of the awid
	 * @param reconnectionInterval
	 *            The time inbetween successive reconnection attempts
	 * @param maxConAttempts
	 *            The maxiumum number of attempts to make to recoonect
	 * @param template
	 *            Helper object for sending out JMS messages
	 * @param commandConfigurations
	 *            The Commands available.
	 * @param notifierSerivce
	 *            Helper object to send out notifications about the state
	 */
	public Awid2010Session(AbstractSensor<?> sensor, String ID, String host,
			int port, int reconnectionInterval, int maxConAttempts,
			JmsTemplate template,
			Set<AbstractCommandConfiguration<?>> commandConfigurations,
			NotifierService notifierSerivce) {
		super(sensor, ID, host, port, reconnectionInterval, maxConAttempts,
				template.getDefaultDestination(), template,
				commandConfigurations);
		this.parsingStratFac = new Awid2010MessageParsingStrategyFactory();
		// create an object to handle incoming tag messages
		AwidTagHandler tagHandler = new AwidTagHandler(template,
				(Awid2010Sensor) super.getSensor());
		// create a new object that listens for incoming messages
		awidEndpoint = new AwidEndpoint(tagHandler, sensor.getID());
		// subscribe the endpoint
		this.subscribe(awidEndpoint);
		this.notifierService = notifierSerivce;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.sensors.base.AbstractIPSensorSession#
	 * getMessageParsingStrategyFactory()
	 */
	@Override
	public MessageParsingStrategyFactory getMessageParsingStrategyFactory() {
		return parsingStratFac;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.AbstractIPSensorSession#onConnect()
	 */
	@Override
	public boolean onConnect() throws IOException {
		// Wait for the welcome message to be received. If it's not recieved
		// after 10*100 ms, give up
		for (int i = 0; i < 10; i++) {
			if (awidEndpoint.isConnected()) {
				continue;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		if (awidEndpoint.isConnected()) {
			logger.debug("Awid Welcome Message Received.");
			return true;
		} else {
			logger.warn("No Awid Welcome Message Received");
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.sensors.SensorSession#getResetCommand()
	 */
	@Override
	protected Command getResetCommand() {
		return new Command("AwidResetCommand") {
			@Override
			public void run() {
				StopCommand command = new StopCommand();
				try {
					((Awid2010Session) super.sensorSession)
							.sendMessage(command);
				} catch (IOException e) {
				}

			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.sessions.AbstractSensorSession#submit(java
	 * .lang.String, long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public Integer submit(String commandID, long interval, TimeUnit unit) {
		int id = super.submit(commandID, interval, unit);
		this.notifierService.jobSubmitted(this.getSensor().getID(), this
				.getID(), id, commandID, (interval > 0L));
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.sessions.AbstractSensorSession#killComand
	 * (java.lang.Integer)
	 */
	@Override
	public void killComand(Integer id) {
		super.killComand(id);
		this.notifierService.jobDeleted(this.getSensor().getID(), this.getID(),
				id);
	}

	/**
	 * Clients of the session should use this method to send commands to the
	 * awid reader
	 * 
	 * @param command
	 *            The command to send
	 * @throws IOException
	 */
	public void sendMessage(AbstractAwidCommand command) throws IOException {
		// tell the awid endpoint to listen for an ack for this command.
		this.awidEndpoint.listenForAck(command);
		try {
			super.sendMessage(new ByteMessage(command.getCommand()));
		} catch (IOException e) {
			// if there was a problem, then there will be no ack.
			this.awidEndpoint.stopListeningForAck(command);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.sessions.AbstractSensorSession#setStatus
	 * (org.rifidi.edge.api.SessionStatus)
	 */
	@Override
	protected synchronized void setStatus(SessionStatus status) {
		// ensure communication is disconnected before receiveing welcome
		// message
		if (status == SessionStatus.LOGGINGIN) {
			this.awidEndpoint.disconnect();
		}
		super.setStatus(status);
		this.notifierService.sessionStatusChanged(getSensor().getID(), getID(),
				status);
	}

	/**
	 * Called when destroying this session
	 */
	protected void destroy() {
		this.unsubscribe(this.awidEndpoint);
	}
}

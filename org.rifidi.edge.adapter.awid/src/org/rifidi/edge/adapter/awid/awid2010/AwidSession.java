/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
package org.rifidi.edge.adapter.awid.awid2010;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.adapter.awid.awid2010.communication.AwidEndpoint;
import org.rifidi.edge.adapter.awid.awid2010.communication.commands.AbstractAwidCommand;
import org.rifidi.edge.adapter.awid.awid2010.communication.commands.ReaderStatusCommand;
import org.rifidi.edge.adapter.awid.awid2010.communication.commands.StopCommand;
import org.rifidi.edge.adapter.awid.awid2010.communication.messages.AckMessage;
import org.rifidi.edge.adapter.awid.awid2010.communication.messages.ReaderStatusMessage;
import org.rifidi.edge.adapter.awid.awid2010.gpio.AwidGPIOSession;
import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.notification.NotifierService;
import org.rifidi.edge.sensors.AbstractCommandConfiguration;
import org.rifidi.edge.sensors.AbstractSensor;
import org.rifidi.edge.sensors.ByteMessage;
import org.rifidi.edge.sensors.TimeoutCommand;
import org.rifidi.edge.sensors.sessions.AbstractPubSubIPSensorSession;
import org.rifidi.edge.sensors.sessions.MessageParsingStrategyFactory;

/**
 * The session that provides communication with the Awid reader. It extends the
 * AbstratPubSubIPSensorSession so that the reading and writing to and from TCP
 * sockets is handled. Incoming messages from the reader are sent to registered
 * listeners, in this case the AwidEndpoint.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AwidSession extends AbstractPubSubIPSensorSession {

	/** The factory that produces the MessageParsingStrategy */
	private final AwidMessageParsingStrategyFactory parsingStratFac;
	/** The endpoint handles incoming messages */
	private final AwidEndpoint awidEndpoint;
	/** The logger for this class */
	private static final Log logger = LogFactory.getLog(AwidSession.class);
	/** Sends out JMS notifications about the state */
	private final NotifierService notifierService;
	/** Session for GPIO Commands */
	private AwidGPIOSession gpioSession;

	private boolean is3014 = false;

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
	public AwidSession(AbstractSensor<?> sensor, String ID, String host,
			int port, int reconnectionInterval, int maxConAttempts,

			Set<AbstractCommandConfiguration<?>> commandConfigurations,
			NotifierService notifierSerivce, boolean is3014) {
		super(sensor, ID, host, port, reconnectionInterval, maxConAttempts,
				commandConfigurations);
		this.parsingStratFac = new AwidMessageParsingStrategyFactory();
		// create a new object that listens for incoming messages
		awidEndpoint = new AwidEndpoint(getTimeout());
		// subscribe the endpoint
		this.subscribe(awidEndpoint);
		this.notifierService = notifierSerivce;
		this.is3014 = is3014;
		this.gpioSession = new AwidGPIOSession(sensor, "1", host, 4001);
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
	 * @see org.rifidi.edge.sensors.SensorSession#connect()
	 */
	@Override
	public void connect() throws IOException {
		super.connect();
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					gpioSession.connect();
				} catch (Exception e) {
					logger.warn("Cannot connect GPIOSessoin: " + gpioSession);
				}

			}
		});
		t.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.base.AbstractIPSensorSession#onConnect()
	 */
	@Override
	public boolean onConnect() throws IOException {
		// Wait for the welcome message to be received. If it's not recieved
		// after 10*100 ms, give up
		for (int i = 0; i < 15; i++) {
			if (awidEndpoint.isConnected()) {
				break;
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		if (awidEndpoint.isConnected()) {
			logger.debug("Awid Welcome Message Received.");
			gpioSession.disconnect();
			return true;
		} else {
			logger.warn("No Awid Welcome Message Received");
			return false;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.sessions.AbstractIPSensorSession#onConnectFailed
	 * ()
	 */
	@Override
	protected void onConnectFailed() {
		logger.warn("On Connect Failed. Attempt to connect again.");
		super.onConnectFailed();

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					connect();
				} catch (IOException e) {
					disconnect();
				}

			}
		});
		t.start();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.SensorSession#getResetCommand()
	 */
	@Override
	protected TimeoutCommand getResetCommand() {
		return new TimeoutCommand("AwidResetCommand") {
			@Override
			public void execute() throws TimeoutException {
				StopCommand command = new StopCommand();
				try {
					AwidSession session = (AwidSession) super.sensorSession;
					clearUndelieverdMessages();
					session.sendMessage(command);
					ByteMessage response = session.getEndpoint()
							.receiveMessage(session.getTimeout());
					AckMessage ack = new AckMessage(response.message);
				} catch (IOException e) {
					logger.warn("IOException on stop command");
				}

			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.sensors.sessions.AbstractIPSensorSession#
	 * getKeepAliveCommand()
	 */
	@Override
	protected TimeoutCommand getKeepAliveCommand() {
		return new TimeoutCommand("AWIDKeepAliveCommand") {

			@Override
			public void execute() throws TimeoutException {
				ReaderStatusCommand command = new ReaderStatusCommand();
				try {
					AwidSession session = (AwidSession) super.sensorSession;
					clearUndelieverdMessages();
					session.sendMessage(command);
					ByteMessage response = session.getEndpoint()
							.receiveMessage(session.getTimeout());
					AckMessage ack = new AckMessage(response.message);

					response = session.getEndpoint().receiveMessage(
							session.getTimeout());
					ReaderStatusMessage status = new ReaderStatusMessage(
							response.message);

				} catch (TimeoutException ex) {
					ex.printStackTrace();
					throw ex;
				} catch (IOException e) {
					logger.warn("IOException on keepalive");
				}

			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.sessions.AbstractSensorSession#submit(java
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
	 * org.rifidi.edge.sensors.sessions.AbstractSensorSession#killComand
	 * (java.lang.Integer)
	 */
	@Override
	public void killComand(Integer id) {
		super.killComand(id);
		this.notifierService.jobDeleted(this.getSensor().getID(), this.getID(),
				id);
	}

	public AwidGPIOSession getGPIOSession() {
		return gpioSession;
	}

	public AwidEndpoint getEndpoint() {
		return this.awidEndpoint;
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
		super.sendMessage(new ByteMessage(command.getCommand()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.sessions.AbstractSensorSession#setStatus
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.sessions.AbstractIPSensorSession#disconnect
	 * ()
	 */
	@Override
	public void disconnect() {
		super.disconnect();
		gpioSession.disconnect();
	}

	/**
	 * Called when destroying this session
	 */
	protected void destroy() {
		this.unsubscribe(this.awidEndpoint);
		gpioSession.disconnect();
	}

	/**
	 * Is the reader a 3014?
	 * 
	 * @return
	 */
	public boolean is3014() {
		return is3014;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.sessions.AbstractIPSensorSession#toString()
	 */
	@Override
	public String toString() {
		return super.toString() + ", " + gpioSession.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.sessions.pubsub.AbstractPubSubIPSensorSession
	 * #clearUndelieverdMessages()
	 */
	@Override
	protected void clearUndelieverdMessages() {
		awidEndpoint.clearUndeliveredMessages();
	}
}

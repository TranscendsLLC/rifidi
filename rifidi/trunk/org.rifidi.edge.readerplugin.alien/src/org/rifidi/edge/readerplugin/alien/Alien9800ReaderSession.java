/*
 * 
 * Alien9800ReaderSession.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
/**
 * 
 */
package org.rifidi.edge.readerplugin.alien;

import java.io.IOException;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.sensors.commands.Command;
import org.rifidi.edge.core.sensors.commands.TimeoutCommand;
import org.rifidi.edge.core.sensors.exceptions.CannotExecuteException;
import org.rifidi.edge.core.sensors.messages.ByteMessage;
import org.rifidi.edge.core.sensors.sessions.MessageParsingStrategyFactory;
import org.rifidi.edge.core.sensors.sessions.poll.AbstractPollIPSensorSession;
import org.rifidi.edge.core.services.notification.NotifierService;
import org.rifidi.edge.readerplugin.alien.autonomous.AlienAutonomousSensorSession;
import org.rifidi.edge.readerplugin.alien.commandobject.AlienCommandObjectWrapper;
import org.rifidi.edge.readerplugin.alien.commandobject.AlienGetCommandObject;
import org.rifidi.edge.readerplugin.alien.gpio.AlienGPIOSession;
import org.rifidi.edge.readerplugin.alien.gpio.messages.TextIOListMessageParsingStrategy;
import org.springframework.jms.core.JmsTemplate;

/**
 * A session that connects to an Alien9800Reader
 * 
 * @author Jochen Mader - jochen@pramari.com
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class Alien9800ReaderSession extends AbstractPollIPSensorSession
		implements AlienCommandConstants {
	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(Alien9800ReaderSession.class);
	/** Username for connecting to the reader. */
	private final String username;
	/** Password for connecting to the reader. */
	private final String password;
	/** Each command needs to be terminated with a newline. */
	public static final String NEWLINE = "\n";
	/** Welcome string. */
	public static final String WELCOME = "Alien";
	/** Service used to send out notifications */
	private volatile NotifierService notifierService;
	/** The FACTORY_ID of the reader this session belongs to */
	private final String readerID;
	private AlienAutonomousSensorSession autonomousSession;
	private AlienGPIOSession gpiosession;

	/**
	 * 
	 * Constructor
	 * 
	 * @param sensor
	 * @param id
	 *            The FACTORY_ID of the session
	 * @param host
	 *            The IP to connect to
	 * @param port
	 *            The port to connect to
	 * @param notifyPort
	 *            The port to use as a autonomous notify port
	 * @param reconnectionInterval
	 *            The wait time between reconnect attempts
	 * @param maxConAttempts
	 *            The maximum number of times to try to connect
	 * @param username
	 *            The Alien username
	 * @param password
	 *            The Alien password
	 * @param destination
	 *            The JMS destination for tags
	 * @param template
	 *            The JSM template for tags
	 * @param notifierService
	 *            The service for sending client notifications
	 * @param readerID
	 *            The FACTORY_ID of the reader that created this session
	 * @param commands
	 *            A thread safe set containing all available commands
	 */
	public Alien9800ReaderSession(AbstractSensor<?> sensor, String id,
			String host, int port, int notifyPort, int ioStreamPort, int reconnectionInterval,
			int maxConAttempts, String username, String password,
			JmsTemplate template, NotifierService notifierService,
			String readerID, Set<AbstractCommandConfiguration<?>> commands) {
		super(sensor, id, host, port, reconnectionInterval, maxConAttempts,
				template.getDefaultDestination(), template, commands);
		this.username = username;
		this.password = password;
		this.notifierService = notifierService;
		this.readerID = readerID;
		this.autonomousSession = new AlienAutonomousSensorSession(sensor, "1",
				template, notifierService, notifyPort, 15,
				new HashSet<AbstractCommandConfiguration<?>>());
		this.gpiosession = new AlienGPIOSession(sensor, "2", ioStreamPort,
				new TextIOListMessageParsingStrategy());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.sensors.base.AbstractIPSensorSession#
	 * getMessageParsingStrategyFactory()
	 */
	@Override
	public MessageParsingStrategyFactory getMessageParsingStrategyFactory() {
		return new AlienMessageParsingStrategyFactory();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.sensors.SensorSession#connect()
	 */
	@Override
	public void connect() throws IOException {
		super.connect();

		Thread autoSessionThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					autonomousSession.connect();
				} catch (IOException ex) {
					logger.warn("Cannot start Autonomous Session "
							+ autonomousSession);
				}
			}
		});
		autoSessionThread.start();
		
		final Alien9800ReaderSession interactiveSession = this;
		
		Thread gpioSessionThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					gpiosession.initialize(interactiveSession);
					gpiosession.connect();
				} catch (CannotExecuteException e) {
					e.printStackTrace();
					logger.warn("Cannot connect Alien GPIO Session: " + gpiosession);
				} catch (IOException e) {
					e.printStackTrace();
					logger.warn("Cannot connect Alien GPIO Session: " + gpiosession);
				}
				
			}
		});
		
		gpioSessionThread.start();


	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readers.impl.AbstractIPReaderSession#onConnect()
	 */
	@Override
	public boolean onConnect() throws IOException {
		logger.debug("getting the welcome response");
		try {
			String welcome = new String(receiveMessage().message);
			logger.debug("welcome message: " + welcome);

			if (welcome == null
					|| !welcome.contains(Alien9800ReaderSession.WELCOME)) {
				logger.fatal("SensorSession is not an alien sensorSession: "
						+ welcome);
				return false;
			} else if (welcome.toLowerCase().contains("busy")) {
				logger.error("SensorSession is busy: " + welcome);
				return false;
			} else {
				logger.debug("SensorSession is an alien.  Hoo-ray!");
			}

			logger.debug("sending username");
			sendMessage(new ByteMessage((Alien9800ReaderSession.PROMPT_SUPPRESS
					+ username + Alien9800ReaderSession.NEWLINE).getBytes()));
			logger.debug("getting the username response");
			receiveMessage();
			logger.debug("sending the password. ");
			sendMessage(new ByteMessage((Alien9800ReaderSession.PROMPT_SUPPRESS
					+ password + Alien9800ReaderSession.NEWLINE).getBytes()));
			logger.debug("recieving the password response");
			String authMessage = new String(receiveMessage().message);
			if (authMessage.contains("Invalid")) {
				logger.warn("Incorrect Password");
				return false;
			}
		} catch (TimeoutException ex) {
			logger.warn("Timeout when logging in");
			throw new IOException(ex);
		}
		
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.sessions.AbstractIPSensorSession#disconnect
	 * ()
	 */
	@Override
	public void disconnect() {
		try {
			super.disconnect();
		} catch (Exception e) {

		}
		try {
			this.autonomousSession.disconnect();
		} catch (Exception e) {

		}
		try {
			this.gpiosession.disconnect();
		} catch (Exception e) {

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.sensors.sessions.AbstractIPSensorSession#
	 * getKeepAliveCommand()
	 */
	@Override
	protected Command getKeepAliveCommand() {

		/**
		 * The Alien uptime command sends a 'get uptime' command to the alien
		 * reader. If it gets no response within 10 seconds, it assumes the
		 * reader has gone away and restarts the session.
		 */
		return new TimeoutCommand("Alien Keep Alive") {

			@Override
			public void execute() throws TimeoutException {
				// the message to send
				String message = PROMPT_SUPPRESS + "get " + COMMAND_UPTIME
						+ NEWLINE;
				try {
					// send the message to the reader
					sendMessage(new ByteMessage(message.getBytes()));

					// wait for a response
					receiveMessage();

				} catch (IOException e) {
					// Ignore
				}
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.sessions.AbstractIPSensorSession#sendMessage
	 * (org.rifidi.edge.core.sensors.messages.ByteMessage)
	 */
	@Override
	public void sendMessage(ByteMessage message) throws IOException {
		super.sendMessage(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readers.impl.AbstractReaderSession#setStatus(org
	 * .rifidi.edge.core.api.SessionStatus)
	 */
	@Override
	protected void setStatus(SessionStatus status) {
		super.setStatus(status);

		// TODO: Remove this once we have aspectJ
		notifierService.sessionStatusChanged(this.readerID, this.getID(),
				status);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readers.impl.AbstractReaderSession#killComand(java
	 * .lang.Integer)
	 */
	@Override
	public void killComand(Integer id) {
		super.killComand(id);

		// TODO: Remove this once we have aspectJ
		notifierService.jobDeleted(this.readerID, this.getID(), id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.sensors.SensorSession#submit(java.lang.String,
	 * long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public Integer submit(String commandID, long interval, TimeUnit unit) {
		Integer retVal = super.submit(commandID, interval, unit);
		// TODO: Remove this once we have aspectJ
		try {
			notifierService.jobSubmitted(this.readerID, this.getID(), retVal,
					commandID, (interval > 0L));
		} catch (Exception e) {
			// make sure the notification doesn't cause this method to exit
			// under any circumstances
			logger.error(e);
		}
		return retVal;

	}

	/**
	 * This method sets the External Output high for the given ports
	 * 
	 * @param ports
	 *            The ports to set high
	 */
	public void setOutputPort(BitSet ports) {
		int value = 0;
		for (int i = 0; i < 8; i++) {
			int bit = 0;
			if (ports.size() > i + 1)
				bit = ports.get(i) ? 1 : 0;
			value = value | bit << i;
		}
		if (value > 255) {
			throw new IllegalArgumentException("No more than 8 ports allowed");
		}
		((Alien9800Reader) this.getSensor()).setExternalOutput(value);
		((Alien9800Reader) this.getSensor()).applyPropertyChanges();
	}

	/**
	 * This method tests if a given input port is high
	 * 
	 * @param port
	 * @return
	 * @throws CannotExecuteException
	 */
	public boolean testInputPort(int port) throws CannotExecuteException {
		int external = getExternalInput();
		// create a mask by bitshifting 1 port number of bits
		int mask = 1 << port;
		// if the mask AND the bitmap returned from the alien reader is
		// greater than 1, then the bit specified by 'port' is high
		return (external & mask) > 0;

	}

	/**
	 * helper method that sends a 'get externalInput' command to the reader. It
	 * blocks until the response returns
	 * 
	 * @return the External Input
	 * @throws CannotExecuteException
	 */
	public int getExternalInput() throws CannotExecuteException {
		LinkedBlockingQueue<AlienCommandObjectWrapper> commandObj = new LinkedBlockingQueue<AlienCommandObjectWrapper>();
		commandObj.add(new AlienCommandObjectWrapper(
				Alien9800Reader.PROP_EXTERNAL_INPUT, new AlienGetCommandObject(
						COMMAND_EXTERNAL_INPUT)));
		boolean executed = ((Alien9800Reader) this.getSensor())
				.applyPropertyChanges(commandObj, true);
		if (executed) {
			return ((Alien9800Reader) this.getSensor()).getExternalInput();

		} else {
			throw new CannotExecuteException(
					"The GPI command may not have executed");
		}

	}

	/**
	 * This method flashes the given output ports high for a given amount of
	 * time. This method executes in a separate thread, so it returns
	 * immediately.
	 * 
	 * @param ports
	 *            The ports to set to high
	 * @param finalPorts
	 *            The configuration of the ports after the flashes are done
	 * @param timeOn
	 *            The time in seconds to set the ports high
	 * @param timeOff
	 *            The time in seconds to set the port low
	 * @param repeat
	 *            The number of times to reapeat the flashes.
	 * @throws CannotExecuteException
	 */
	public void flashOutput(final BitSet ports, final BitSet finalPorts,
			final int timeOn, final int timeOff, final int repeat)
			throws CannotExecuteException {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					setOutputPort(new BitSet());
					for (int i = 0; i < repeat; i++) {
						setOutputPort(ports);
						Thread.sleep(timeOn * 1000);
						setOutputPort(new BitSet());
						Thread.sleep(timeOff * 1000);
					}
					setOutputPort(finalPorts);
				} catch (InterruptedException e) {
					logger.warn("Could not complete flash output");
					Thread.currentThread().interrupt();
				} catch (Exception e) {
					logger.warn("Could not complete flash output");
				}
			}
		});
		t.start();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.sessions.AbstractIPSensorSession#toString()
	 */
	@Override
	public String toString() {
		return super.toString() + ", " + autonomousSession + "," + gpiosession;
	}

}

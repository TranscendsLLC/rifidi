/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.adapter.llrp;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.RuntimeIOException;
import org.llrp.ltk.exceptions.InvalidLLRPMessageException;
import org.llrp.ltk.generated.enumerations.AccessReportTriggerType;
import org.llrp.ltk.generated.enumerations.GetReaderCapabilitiesRequestedData;
import org.llrp.ltk.generated.enumerations.NotificationEventType;
import org.llrp.ltk.generated.enumerations.ROReportTriggerType;
import org.llrp.ltk.generated.enumerations.StatusCode;
import org.llrp.ltk.generated.messages.GET_READER_CAPABILITIES;
import org.llrp.ltk.generated.messages.SET_READER_CONFIG;
import org.llrp.ltk.generated.messages.SET_READER_CONFIG_RESPONSE;
import org.llrp.ltk.generated.parameters.AccessReportSpec;
import org.llrp.ltk.generated.parameters.C1G2EPCMemorySelector;
import org.llrp.ltk.generated.parameters.EventNotificationState;
import org.llrp.ltk.generated.parameters.ROReportSpec;
import org.llrp.ltk.generated.parameters.ReaderEventNotificationSpec;
import org.llrp.ltk.generated.parameters.TagReportContentSelector;
import org.llrp.ltk.net.LLRPConnection;
import org.llrp.ltk.net.LLRPConnectionAttemptFailedException;
import org.llrp.ltk.net.LLRPConnector;
import org.llrp.ltk.net.LLRPEndpoint;
import org.llrp.ltk.types.Bit;
import org.llrp.ltk.types.LLRPMessage;
import org.llrp.ltk.types.UnsignedInteger;
import org.llrp.ltk.types.UnsignedShort;
import org.llrp.ltk.util.Util;
import org.rifidi.edge.adapter.llrp.commands.internal.LLRPReset;
import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.notification.NotifierService;
import org.rifidi.edge.notification.ReadCycle;
import org.rifidi.edge.sensors.AbstractCommandConfiguration;
import org.rifidi.edge.sensors.AbstractSensor;
import org.rifidi.edge.sensors.Command;
import org.rifidi.edge.sensors.TimeoutCommand;
import org.rifidi.edge.sensors.sessions.AbstractSensorSession;

/**
 * This class represents a session with an LLRP reader. It handles connecting
 * and disconnecting, as well as receiving tag data.
 * 
 * @author Matthew Dean
 */
public class LLRPReaderSession extends AbstractSensorSession implements
		LLRPEndpoint {

	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(LLRPReaderSession.class);

	private volatile LLRPConnection connection = null;
	/** Service used to send out notifications */
	private volatile NotifierService notifierService;
	/** The ID of the reader this session belongs to */
	private final String readerID;
	private final String readerConfigPath;

	/** Ok, because only accessed from synchronized block */
	int messageID = 1;
	int maxConAttempts = -1;
	int reconnectionInterval = -1;
	private AtomicBoolean timingOut = new AtomicBoolean(false);

	/** atomic boolean that is true if we are inside the connection attempt loop */
	private AtomicBoolean connectingLoop = new AtomicBoolean(false);
	/** LLRP host */
	private String host;
	/** LLRP port */
	private int port;

	/**
	 * 
	 * @param sensor
	 * @param id
	 * @param host
	 * @param port
	 * @param reconnectionInterval
	 * @param maxConAttempts
	 * @param readerConfigPath
	 * @param timeout
	 * @param notifierService
	 * @param readerID
	 * @param commands
	 */
	public LLRPReaderSession(AbstractSensor<?> sensor, String id, String host,
			int port, int reconnectionInterval, int maxConAttempts,
			String readerConfigPath, NotifierService notifierService,
			String readerID, Set<AbstractCommandConfiguration<?>> commands) {
		super(sensor, id, commands);
		this.host = host;
		this.port = port;
		this.connection = new LLRPConnector(this, host, port);
		this.maxConAttempts = maxConAttempts;
		this.reconnectionInterval = reconnectionInterval;
		this.readerConfigPath = readerConfigPath;
		this.notifierService = notifierService;
		this.readerID = readerID;
		this.setStatus(SessionStatus.CLOSED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readers.ReaderSession#connect()
	 */
	@Override
	protected synchronized void _connect() throws IOException {
		logger.info("LLRP Session " + this.getID() + " on sensor "
				+ this.getSensor().getID() + " attempting to connect to "
				+ host + ":" + port);
		this.setStatus(SessionStatus.CONNECTING);
		// Connected flag
		boolean connected = false;
		// try to connect up to MaxConAttempts number of times, unless
		// maxConAttempts is -1, in which case, try forever
		connectingLoop.set(true);
		try {
			for (int connCount = 0; connCount < maxConAttempts
					|| maxConAttempts == -1; connCount++) {

				// attempt to make the connection
				try {
					((LLRPConnector) connection).connect();
					connected = true;
					break;
				} catch (LLRPConnectionAttemptFailedException e) {
					logger.debug("Attempt to connect to LLRP reader failed: "
							+ connCount);
				} catch (RuntimeIOException e) {
					logger.debug("Attempt to connect to LLRP reader failed: "
							+ connCount);
				}

				// wait for a specified number of ms or until someone calls
				// notify on the connetingLoop object
				try {
					synchronized (connectingLoop) {
						connectingLoop.wait(reconnectionInterval);
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}

				// if someone else wants us to stop, break from the for loop
				if (!connectingLoop.get())
					break;
			}
		} finally {
			// make sure connecting loop is false!
			connectingLoop.set(false);
		}

		// if not connected, exit
		if (!connected) {
			setStatus(SessionStatus.CLOSED);
			throw new IOException("Cannot connect");
		}

		// physical connection established, set up session
		timingOut.set(false);
		onConnect();

		logger.info("LLRP Session " + this.getID() + " on sensor "
				+ this.getSensor().getID() + " connected to " + host + ":"
				+ port);
	}

	/**
	 * This logic executes as soon as a socket is established to initialize the
	 * connection. It occurs before any commands are scheduled
	 */
	private void onConnect() {
		logger.info("LLRP Session " + this.getID() + " on sensor "
				+ this.getSensor().getID() + " attempting to log in to " + host
				+ ":" + port);
		setStatus(SessionStatus.LOGGINGIN);
		executor = new ScheduledThreadPoolExecutor(1);

		try {
			SET_READER_CONFIG config = createSetReaderConfig();
			config.setMessageID(new UnsignedInteger(messageID++));

			SET_READER_CONFIG_RESPONSE config_response = (SET_READER_CONFIG_RESPONSE) connection
					.transact(config);

			StatusCode sc = config_response.getLLRPStatus().getStatusCode();
			if (sc.intValue() != StatusCode.M_Success) {
				if (config_response.getLLRPStatus().getStatusCode().toInteger() != 0) {
					try {
						logger.error("Problem with SET_READER_CONFIG: \n"
								+ config_response.toXMLString());
					} catch (InvalidLLRPMessageException e) {
						logger.warn("Cannot print XML for "
								+ "SET_READER_CONFIG_RESPONSE");
					}
				}
			}

			// BytesToEnd_HEX data = new BytesToEnd_HEX();
			// CUSTOM_MESSAGE msg = new CUSTOM_MESSAGE();
			// msg.setVendorIdentifier(new UnsignedInteger(25882));
			// msg.setMessageSubtype(new UnsignedByte(21));
			// data.add(new SignedByte(0));
			// data.add(new SignedByte(0));
			// data.add(new SignedByte(0));
			// data.add(new SignedByte(0));
			// msg.setData(data);
			// connection.transact(msg);

			if (!processing.compareAndSet(false, true)) {
				logger.warn("Executor was already active! ");
			}
			submit(getTimeoutCommand(), 10, TimeUnit.SECONDS);
			setStatus(SessionStatus.PROCESSING);

		} catch (TimeoutException e) {
			logger.error(e.getMessage());
			disconnect();
		} catch (ClassCastException ex) {
			logger.error(ex.getMessage());
			disconnect();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.SensorSession#getResetCommand()
	 */
	@Override
	protected Command getResetCommand() {
		return new LLRPReset("LLRPResetCommand");
	}

	/**
	 * This method returns a command used to ping the reader. It is used to
	 * determine if the connection is still alive
	 * 
	 * @return
	 */
	private Command getTimeoutCommand() {
		return new TimeoutCommand("LLRP Timeout") {

			@Override
			protected void execute() throws TimeoutException {
				GET_READER_CAPABILITIES grc = new GET_READER_CAPABILITIES();
				GetReaderCapabilitiesRequestedData data = new GetReaderCapabilitiesRequestedData();
				data.set(GetReaderCapabilitiesRequestedData.LLRP_Capabilities);
				grc.setRequestedData(data);
				transact(grc);

			}
		};
	}

	/**
	 * 
	 */
	@Override
	public void disconnect() {
		resetCommands();
		this.submitAndBlock(getResetCommand(), getTimeout(),
				TimeUnit.MILLISECONDS);
		try {
			// if in the connecting loop, set atomic boolean to false and call
			// notify on the connectingLoop monitor
			if (connectingLoop.getAndSet(false)) {
				synchronized (connectingLoop) {
					connectingLoop.notify();
				}
			}
			// if already connected, disconnect
			if (processing.get()) {
				if (processing.compareAndSet(true, false)) {
					logger.debug("Disconnecting");
					((LLRPConnector) connection).disconnect();
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {

			// make sure executor is shutdown!
			if (executor != null) {
				executor.shutdownNow();
				executor = null;
			}
			// notify anyone who cares that session is now closed
			setStatus(SessionStatus.CLOSED);
		}

	}

	/**
	 * This method sends a message and waits for an LLRP response message.
	 * 
	 * @param message
	 *            The LLRP message to send to the reader
	 * @return The response message
	 * @throws TimeoutException
	 *             If there was a timeout when processing this command.
	 */
	public LLRPMessage transact(LLRPMessage message) throws TimeoutException {
		synchronized (connection) {
			try {
				if (timingOut.get()) {
					throw new IllegalStateException(
							"Cannot execute while timing out: "
									+ message.getName());
				}
				LLRPMessage response = this.connection.transact(message, 20000);
				if (response != null) {
					return response;
				} else {
					logger.error("Response for message: " + message.getName()
							+ " was null");
					throw new TimeoutException();
				}
			} catch (TimeoutException e) {
				timingOut.set(true);
				logger.error("Timeout when sending an LLRP Message: "
						+ message.getName());
				throw e;
			}
		}
	}

	/**
	 * This command sends a message and does not wait for a response. Transact
	 * should normally be preferred to this method.
	 * 
	 * @param message
	 */
	public void send(LLRPMessage message) {
		synchronized (connection) {
			connection.send(message);
		}
	}

	/**
	 * 
	 */
	@Override
	public void errorOccured(String arg0) {
		logger.error("LLRP Error Occurred: " + arg0);
		// TODO: should we disconnect?
	}

	/**
	 * This method creates a SET_READER_CONFIG method.
	 * 
	 * @return The SET_READER_CONFIG object.
	 */
	public SET_READER_CONFIG createSetReaderConfig() {
		try {
			String directory = System.getProperty("org.rifidi.home");
			String file = directory + File.separator + readerConfigPath;
			LLRPMessage message = Util.loadXMLLLRPMessage(new File(file));
			return (SET_READER_CONFIG) message;
		} catch (Exception e) {
			logger.warn("No SET_READER_CONFIG message found at "
					+ readerConfigPath + " Using default SET_READER_CONFIG");
		}

		return createDefaultConfig();

	}

	/**
	 * A default SET_READER_CONFIG message to use in case the one from the file
	 * cannot be loaded.
	 * 
	 * @return
	 */
	private SET_READER_CONFIG createDefaultConfig() {
		SET_READER_CONFIG setReaderConfig = new SET_READER_CONFIG();

		// Create a default RoReportSpec so that reports are sent at the end of
		// ROSpecs
		ROReportSpec roReportSpec = new ROReportSpec();
		roReportSpec.setN(new UnsignedShort(0));
		roReportSpec.setROReportTrigger(new ROReportTriggerType(
				ROReportTriggerType.None));
		TagReportContentSelector tagReportContentSelector = new TagReportContentSelector();
		tagReportContentSelector.setEnableAccessSpecID(new Bit(0));
		tagReportContentSelector.setEnableAntennaID(new Bit(1));
		tagReportContentSelector.setEnableChannelIndex(new Bit(0));
		tagReportContentSelector.setEnableFirstSeenTimestamp(new Bit(0));
		tagReportContentSelector.setEnableInventoryParameterSpecID(new Bit(0));
		tagReportContentSelector.setEnableLastSeenTimestamp(new Bit(0));
		tagReportContentSelector.setEnablePeakRSSI(new Bit(0));
		tagReportContentSelector.setEnableROSpecID(new Bit(1));
		tagReportContentSelector.setEnableSpecIndex(new Bit(0));
		tagReportContentSelector.setEnableTagSeenCount(new Bit(0));
		C1G2EPCMemorySelector epcMemSel = new C1G2EPCMemorySelector();
		epcMemSel.setEnableCRC(new Bit(0));
		epcMemSel.setEnablePCBits(new Bit(0));
		tagReportContentSelector
				.addToAirProtocolEPCMemorySelectorList(epcMemSel);
		roReportSpec.setTagReportContentSelector(tagReportContentSelector);
		setReaderConfig.setROReportSpec(roReportSpec);

		// Set default AccessReportSpec

		AccessReportSpec accessReportSpec = new AccessReportSpec();
		accessReportSpec.setAccessReportTrigger(new AccessReportTriggerType(
				AccessReportTriggerType.Whenever_ROReport_Is_Generated));
		setReaderConfig.setAccessReportSpec(accessReportSpec);

		// Set up reporting for AISpec events, ROSpec events, and GPI Events

		ReaderEventNotificationSpec eventNoteSpec = new ReaderEventNotificationSpec();
		EventNotificationState noteState = new EventNotificationState();
		noteState.setEventType(new NotificationEventType(
				NotificationEventType.AISpec_Event));
		noteState.setNotificationState(new Bit(0));
		eventNoteSpec.addToEventNotificationStateList(noteState);
		noteState = new EventNotificationState();
		noteState.setEventType(new NotificationEventType(
				NotificationEventType.ROSpec_Event));
		noteState.setNotificationState(new Bit(0));
		eventNoteSpec.addToEventNotificationStateList(noteState);
		noteState = new EventNotificationState();
		noteState.setEventType(new NotificationEventType(
				NotificationEventType.GPI_Event));
		noteState.setNotificationState(new Bit(0));
		eventNoteSpec.addToEventNotificationStateList(noteState);
		setReaderConfig.setReaderEventNotificationSpec(eventNoteSpec);

		setReaderConfig.setResetToFactoryDefault(new Bit(0));

		return setReaderConfig;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readers.impl.AbstractReaderSession#setStatus(org
	 * .rifidi.edge.core.api.SessionStatus)
	 */
	@Override
	protected synchronized void setStatus(SessionStatus status) {
		super.setStatus(status);
		// TODO: Remove this once we have aspectJ
		NotifierService service = notifierService;
		if (service != null) {
			service.sessionStatusChanged(this.readerID, this.getID(), status);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.base.AbstractSensorSession#submit(java.lang
	 * .String, long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public Integer submit(String commandID, long interval, TimeUnit unit) {
		Integer retVal = super.submit(commandID, interval, unit);
		// TODO: Remove this once we have aspectJ
		try {
			NotifierService service = notifierService;
			if (service != null) {
				service.jobSubmitted(this.readerID, this.getID(), retVal,
						commandID, (interval > 0));
			}
		} catch (Exception e) {
			// make sure the notification doesn't cause this method to exit
			// under any circumstances
			logger.error(e);
		}
		return retVal;
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
		NotifierService service = notifierService;
		if (service != null) {
			service.jobDeleted(this.readerID, this.getID(), id);
		}
	}

	/**
	 * This method receives asynchronous messages back from the LLRP reader. We
	 * add relavent events to the esper runtime
	 */
	@Override
	public void messageReceived(LLRPMessage arg0) {
		if (!processing.get()) {
			return;
		}
		try {
			Object event = LLRPEventFactory.createEvent(arg0, readerID);
			if (event != null) {
				if (event instanceof ReadCycle) {
					ReadCycle cycle = (ReadCycle) event;
					sensor.send(cycle);

					// TODO: get rid of this for performance reasons. Need to
					// have a better way to figure out if we need to send tag
					// read to JMS
					// this.getTemplate().send(this.getDestination(),
					// new ObjectMessageCreator(cycle));
				} else {
					sensor.sendEvent(event);
				}
			}

		} catch (Exception e) {
			logger.error("Exception while parsing message: " + e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LLRPSession: " + host + ":" + port + " (" + getStatus() + ")";
	}

}

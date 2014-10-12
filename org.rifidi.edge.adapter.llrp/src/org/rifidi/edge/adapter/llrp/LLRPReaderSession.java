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
package org.rifidi.edge.adapter.llrp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.RuntimeIOException;
import org.jdom.Document;
import org.llrp.ltk.exceptions.InvalidLLRPMessageException;
import org.llrp.ltk.generated.LLRPMessageFactory;
import org.llrp.ltk.generated.enumerations.AccessReportTriggerType;
import org.llrp.ltk.generated.enumerations.AccessSpecState;
import org.llrp.ltk.generated.enumerations.AccessSpecStopTriggerType;
import org.llrp.ltk.generated.enumerations.AirProtocols;
import org.llrp.ltk.generated.enumerations.GetReaderCapabilitiesRequestedData;
import org.llrp.ltk.generated.enumerations.NotificationEventType;
import org.llrp.ltk.generated.enumerations.ROReportTriggerType;
import org.llrp.ltk.generated.enumerations.StatusCode;
import org.llrp.ltk.generated.interfaces.AccessCommandOpSpec;
import org.llrp.ltk.generated.interfaces.AccessCommandOpSpecResult;
import org.llrp.ltk.generated.messages.ADD_ACCESSSPEC;
import org.llrp.ltk.generated.messages.ADD_ACCESSSPEC_RESPONSE;
import org.llrp.ltk.generated.messages.DELETE_ACCESSSPEC;
import org.llrp.ltk.generated.messages.DELETE_ACCESSSPEC_RESPONSE;
import org.llrp.ltk.generated.messages.ENABLE_ACCESSSPEC;
import org.llrp.ltk.generated.messages.ENABLE_ACCESSSPEC_RESPONSE;
import org.llrp.ltk.generated.messages.GET_READER_CAPABILITIES;
import org.llrp.ltk.generated.messages.RO_ACCESS_REPORT;
import org.llrp.ltk.generated.messages.SET_READER_CONFIG;
import org.llrp.ltk.generated.messages.SET_READER_CONFIG_RESPONSE;
import org.llrp.ltk.generated.parameters.AccessCommand;
import org.llrp.ltk.generated.parameters.AccessReportSpec;
import org.llrp.ltk.generated.parameters.AccessSpec;
import org.llrp.ltk.generated.parameters.AccessSpecStopTrigger;
import org.llrp.ltk.generated.parameters.C1G2BlockWrite;
import org.llrp.ltk.generated.parameters.C1G2EPCMemorySelector;
import org.llrp.ltk.generated.parameters.C1G2Read;
import org.llrp.ltk.generated.parameters.C1G2TagSpec;
import org.llrp.ltk.generated.parameters.C1G2TargetTag;
import org.llrp.ltk.generated.parameters.C1G2Write;
import org.llrp.ltk.generated.parameters.EventNotificationState;
import org.llrp.ltk.generated.parameters.ROReportSpec;
import org.llrp.ltk.generated.parameters.ReaderEventNotificationSpec;
import org.llrp.ltk.generated.parameters.TagReportContentSelector;
import org.llrp.ltk.generated.parameters.TagReportData;
import org.llrp.ltk.net.LLRPConnection;
import org.llrp.ltk.net.LLRPConnectionAttemptFailedException;
import org.llrp.ltk.net.LLRPConnector;
import org.llrp.ltk.net.LLRPEndpoint;
import org.llrp.ltk.types.Bit;
import org.llrp.ltk.types.BitArray_HEX;
import org.llrp.ltk.types.LLRPMessage;
import org.llrp.ltk.types.TwoBitField;
import org.llrp.ltk.types.UnsignedInteger;
import org.llrp.ltk.types.UnsignedShort;
import org.llrp.ltk.types.UnsignedShortArray_HEX;
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
					// fix for abandoned connection
					try {
						((LLRPConnector) connection).disconnect();
					} catch (Exception ex) {
					}
					logger.debug("Attempt to connect to LLRP reader failed: "
							+ connCount);
				} catch (RuntimeIOException e) {
					// fix for abandoned connection
					try {
						((LLRPConnector) connection).disconnect();
					} catch (Exception ex) {
					}
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

	public String addAccessSpec(String writeAccessPassword, String writeData) {
		// TODO: Throw exception if writeData does not divide evenly by 4
		try {
			System.out.println("Add accessspec! ");
			
			int accessSpecId = 2;

			AccessSpec spec = buildAccessSpec(accessSpecId, writeData);
			
			ADD_ACCESSSPEC aas = new ADD_ACCESSSPEC();

			aas.setAccessSpec(spec);

			System.out.println(aas.toXMLString());

			ADD_ACCESSSPEC_RESPONSE response = null;
			try {

				response = (ADD_ACCESSSPEC_RESPONSE) this.transact(aas);
				System.out.println("Response: " + response.toXMLString());
				
				ENABLE_ACCESSSPEC enablespec = new ENABLE_ACCESSSPEC();
				enablespec.setAccessSpecID(new UnsignedInteger(accessSpecId));
				
				this.send(enablespec);
				//this.transact(enablespec);

				
				// Thread.sleep(3000);
				Thread.sleep(10000);

				DELETE_ACCESSSPEC deleteAccessSpec = new DELETE_ACCESSSPEC();
				deleteAccessSpec.setAccessSpecID(new UnsignedInteger(accessSpecId));
				this.send(deleteAccessSpec);

				return response.toXMLString();
			} catch (TimeoutException e) {
				return e.getMessage();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	public String addAccessSpec_matts(String writeAccessPassword,
			String writeData) {
		// TODO: Throw exception if writeData does not divide evenly by 4
		try {
			System.out.println("Add accessspec! ");

			AccessSpec spec = new AccessSpec();
			spec.setAccessSpecID(new UnsignedInteger(1));
			spec.setAntennaID(new UnsignedShort(0));
			spec.setProtocolID(new AirProtocols(
					AirProtocols.EPCGlobalClass1Gen2));
			spec.setROSpecID(new UnsignedInteger(1));
			AccessCommand command = new AccessCommand();
			C1G2TagSpec cgSpec = new C1G2TagSpec();
			C1G2TargetTag tag = new C1G2TargetTag();
			// tag.setMatch(new Bit(targetMatch));
			TwoBitField tbf = new TwoBitField();
			tbf.clear(0);
			tbf.set(1);
			tag.setMB(tbf);
			tag.setPointer(new UnsignedShort(0));
			BitArray_HEX bitHex = new BitArray_HEX("0000");
			tag.setTagData(bitHex);
			tag.setMatch(new Bit(0));
			tag.setTagMask(new BitArray_HEX(0000));
			tag.setTagData(new BitArray_HEX(0000));

			List<C1G2TargetTag> tagList = new LinkedList<C1G2TargetTag>();
			tagList.add(tag);
			cgSpec.setC1G2TargetTagList(tagList);

			command.setAirProtocolTagSpec(cgSpec);

			List<AccessCommandOpSpec> opSpecList = new ArrayList<AccessCommandOpSpec>();

			C1G2BlockWrite write = new C1G2BlockWrite();
			write.setAccessPassword(new UnsignedInteger(writeAccessPassword));
			UnsignedShortArray_HEX writeArray = new UnsignedShortArray_HEX();
			for (String word : writeData.split(":")) {
				System.out.println("Adding short: " + word);
				writeArray.add(new UnsignedShort(word, 16));
			}
			write.setWriteData(writeArray);
			write.setOpSpecID(new UnsignedShort(1));
			write.setMB(tbf);
			// write.setWordPointer(new UnsignedShort(0x02));
			write.setWordPointer(new UnsignedShort(0x00));

			opSpecList.add(write);
			command.setAccessCommandOpSpecList(opSpecList);
			spec.setAccessCommand(command);
			spec.setCurrentState(new AccessSpecState(AccessSpecState.Active));

			AccessSpecStopTrigger stopTrigger = new AccessSpecStopTrigger();
			stopTrigger.setAccessSpecStopTrigger(new AccessSpecStopTriggerType(
					AccessSpecStopTriggerType.Operation_Count));
			stopTrigger.setOperationCountValue(new UnsignedShort(10));

			spec.setAccessSpecStopTrigger(stopTrigger);

			ADD_ACCESSSPEC aas = new ADD_ACCESSSPEC();

			aas.setAccessSpec(spec);

			System.out.println(aas.toXMLString());

			ADD_ACCESSSPEC_RESPONSE response = null;
			try {

				response = (ADD_ACCESSSPEC_RESPONSE) this.transact(aas);
				System.out.println("Response: " + response.toXMLString());

				ENABLE_ACCESSSPEC enablespec = new ENABLE_ACCESSSPEC();
				enablespec.setAccessSpecID(new UnsignedInteger(1));

				this.send(enablespec);

				// Thread.sleep(3000);
				Thread.sleep(10000);

				DELETE_ACCESSSPEC deleteAccessSpec = new DELETE_ACCESSSPEC();
				deleteAccessSpec.setAccessSpecID(new UnsignedInteger(1));
				this.send(deleteAccessSpec);

				return response.toXMLString();
			} catch (TimeoutException e) {
				return e.getMessage();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	public String buildWriteOpSpec(String writeAccessPassword, String writeData) {

		try {
			System.out.println("Add accessspec! ");

			AccessSpec spec = new AccessSpec();
			spec.setAccessSpecID(new UnsignedInteger(1));
			spec.setAntennaID(new UnsignedShort(0));
			spec.setProtocolID(new AirProtocols(
					AirProtocols.EPCGlobalClass1Gen2));
			spec.setROSpecID(new UnsignedInteger(1));

			AccessCommand command = new AccessCommand();

			// Create a new OpSpec.

			// This specifies what operation we want to perform on the

			// tags that match the specifications above.

			// In this case, we want to write to the tag.

			C1G2Write opSpec = new C1G2Write();

			// Set the OpSpecID to a unique number.

			opSpec.setOpSpecID(new UnsignedShort(1));

			opSpec.setAccessPassword(new UnsignedInteger(0));

			// For this demo, we'll write to user memory (bank 3).

			TwoBitField opMemBank = new TwoBitField();

			// Set bits 0 and 1 (bank 3 in binary).

			opMemBank.set(1);

			opMemBank.clear(0);

			opSpec.setMB(opMemBank);

			// We'll write to the base of this memory bank (0x00).

			opSpec.setWordPointer(new UnsignedShort(0x02));

			// UnsignedShortArray_HEX writeData = new UnsignedShortArray_HEX();

			// We'll write 8 bytes or two words.
			UnsignedShortArray_HEX writeArray = new UnsignedShortArray_HEX();
			for (String word : writeData.split(":")) {
				System.out.println("Adding short: " + word);
				writeArray.add(new UnsignedShort(word, 16));
			}
			opSpec.setWriteData(writeArray);

			// return opSpec;
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}

	}

	public String[] splitHexArray(String arg) {
		ArrayList<String> retVal = new ArrayList<String>();
		for (int i = 0; i < arg.length(); i += 4) {
			retVal.add(arg.substring(i, i + 4));
		}
		return retVal.toArray(new String[arg.length()]);
	}

	public String sendLLRPMessage(Document xmlMessage) {
		try {
			LLRPMessage message = LLRPMessageFactory
					.createLLRPMessage(xmlMessage);
			LLRPMessage response = null;
			try {
				response = this.transact(message);
				return response.toString();
			} catch (TimeoutException e) {
				return e.getMessage();
			}
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	/**
	 * This method receives asynchronous messages back from the LLRP reader. We
	 * add relavent events to the esper runtime
	 */
	@Override
	public void messageReceived(LLRPMessage arg0) {
		
		if (arg0.getTypeNum() == RO_ACCESS_REPORT.TYPENUM) {
			// The message received is an Access Report.
			RO_ACCESS_REPORT report = (RO_ACCESS_REPORT) arg0;
			// Get a list of the tags read.
			List<TagReportData> tags = report.getTagReportDataList();
			// Loop through the list and get the EPC of each tag.
			for (TagReportData tag : tags) {
				//System.out.println(tag.getEPCParameter());
				//System.out.println(tag.getLastSeenTimestampUTC());
				List<AccessCommandOpSpecResult> ops = tag
						.getAccessCommandOpSpecResultList();
				// See if any operations were performed on
				// this tag (read, write, kill).
				// If so, print out the details.
				for (AccessCommandOpSpecResult op : ops) {
					System.out.println(op.toString());
				}
			}
		}
		
		if ((arg0 instanceof RO_ACCESS_REPORT)) {
			// try {
			// // RO_ACCESS_REPORT report = (RO_ACCESS_REPORT)arg0;
			// // if(report.getRFSurveyReportDataList()) {
			// // System.out.println(arg0.toXMLString());
			// // }
			// } catch (InvalidLLRPMessageException e) {
			// }
		}
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

	private static final String TARGET_EPC = "300833b2ddd9014035050000";
	private static final String TAG_MASK = "ffffffffffffffffffffffff";
	private static final int WRITE_ACCESSSPEC_ID = 2;
	private static final int READ_ACCESSSPEC_ID = 444;
	private static final int WRITE_OPSPEC_ID = 2121;
	private static final int READ_OPSPEC_ID = 1212;

	// The TAG_MASK and TARGET_EPC constants are used to define which tags we
	// want to write to. The reader will take each tag EPC and bitwise AND it
	// with the TAG_MASK. If the result matches the value specified in the
	// TARGET_EPC constant, the reader will write to that tag. The other
	// constants defined above are used for message IDs. They can be any unique
	// integer value. Next, we'll add the functions required to read and write
	// user memory.

	// Enable the AccessSpec
	public void enableAccessSpec(int accessSpecID) {
		ENABLE_ACCESSSPEC_RESPONSE response;

		System.out.println("Enabling the AccessSpec.");
		ENABLE_ACCESSSPEC enable = new ENABLE_ACCESSSPEC();
		enable.setAccessSpecID(new UnsignedInteger(accessSpecID));
		try {
			// response = (ENABLE_ACCESSSPEC_RESPONSE) this.transact(enable,
			// TIMEOUT_MS);
			// System.out.println(response.toXMLString());
		} catch (Exception e) {
			System.out.println("Error enabling AccessSpec.");
			e.printStackTrace();
		}
	}

	// Delete all AccessSpecs from the reader
	public void deleteAccessSpecs() {
		DELETE_ACCESSSPEC_RESPONSE response;

		System.out.println("Deleting all AccessSpecs.");
		DELETE_ACCESSSPEC del = new DELETE_ACCESSSPEC();
		// Use zero as the ROSpec ID.
		// This means delete all AccessSpecs.
		del.setAccessSpecID(new UnsignedInteger(0));
		try {
			// response = (DELETE_ACCESSSPEC_RESPONSE) reader.transact(del,
			// TIMEOUT_MS);
			// System.out.println(response.toXMLString());
		} catch (Exception e) {
			System.out.println("Error deleting AccessSpec.");
			e.printStackTrace();
		}
	}

	// Create a OpSpec that reads from user memory
	public C1G2Read buildReadOpSpec() {
		// Create a new OpSpec.
		// This specifies what operation we want to perform on the
		// tags that match the specifications above.
		// In this case, we want to read from the tag.
		C1G2Read opSpec = new C1G2Read();
		// Set the OpSpecID to a unique number.
		opSpec.setOpSpecID(new UnsignedShort(READ_OPSPEC_ID));
		opSpec.setAccessPassword(new UnsignedInteger(0));
		// For this demo, we'll read from user memory (bank 3).
		TwoBitField opMemBank = new TwoBitField();
		// Set bits 0 and 1 (bank 3 in binary).
		opMemBank.set(0);
		opMemBank.set(1);
		opSpec.setMB(opMemBank);
		// We'll read from the base of this memory bank (0x00).
		opSpec.setWordPointer(new UnsignedShort(0x00));
		// Read two words.
		opSpec.setWordCount(new UnsignedShort(2));

		return opSpec;
	}

	// Create a OpSpec that writes to user memory
	public C1G2Write buildWriteOpSpec() {
		// Create a new OpSpec.
		// This specifies what operation we want to perform on the
		// tags that match the specifications above.
		// In this case, we want to write to the tag.
		C1G2Write opSpec = new C1G2Write();
		// Set the OpSpecID to a unique number.
		opSpec.setOpSpecID(new UnsignedShort(WRITE_OPSPEC_ID));
		opSpec.setAccessPassword(new UnsignedInteger(0));
		// For this demo, we'll write to user memory (bank 3).
		TwoBitField opMemBank = new TwoBitField();
		// Set bits 0 and 1 (bank 3 in binary).
		opMemBank.set(0);
		opMemBank.set(1);
		opSpec.setMB(opMemBank);
		// We'll write to the base of this memory bank (0x00).
		opSpec.setWordPointer(new UnsignedShort(0x00));
		UnsignedShortArray_HEX writeData = new UnsignedShortArray_HEX();
		// We'll write 8 bytes or two words.
		writeData.add(new UnsignedShort(0xAABB));
		writeData.add(new UnsignedShort(0xCCDD));
		opSpec.setWriteData(writeData);

		return opSpec;
	}

	// Create an AccessSpec.
	// It will contain our two OpSpecs (read and write).
	public AccessSpec buildAccessSpec(int accessSpecID, String writeData) {
		System.out.println("Building the AccessSpec.");

		AccessSpec accessSpec = new AccessSpec();

		accessSpec.setAccessSpecID(new UnsignedInteger(accessSpecID));

		// Set ROSpec ID to zero.
		// This means that the AccessSpec will apply to all ROSpecs.
		accessSpec.setROSpecID(new UnsignedInteger(0));
		// Antenna ID of zero means all antennas.
		accessSpec.setAntennaID(new UnsignedShort(0));
		accessSpec.setProtocolID(new AirProtocols(
				AirProtocols.EPCGlobalClass1Gen2));
		// AccessSpecs must be disabled when you add them.
		accessSpec
				.setCurrentState(new AccessSpecState(AccessSpecState.Disabled));
		AccessSpecStopTrigger stopTrigger = new AccessSpecStopTrigger();
		// Stop after the operating has been performed a certain number of
		// times.
		// That number is specified by the Operation_Count parameter.
		stopTrigger.setAccessSpecStopTrigger(new AccessSpecStopTriggerType(
				AccessSpecStopTriggerType.Operation_Count));
		// OperationCountValue indicate the number of times this Spec is
		// executed before it is deleted. If set to 0, this is equivalent
		// to no stop trigger defined.
		stopTrigger.setOperationCountValue(new UnsignedShort(1));
		accessSpec.setAccessSpecStopTrigger(stopTrigger);

		// Create a new AccessCommand.
		// We use this to specify which tags we want to operate on.
		AccessCommand accessCommand = new AccessCommand();

		// Create a new tag spec.
		C1G2TagSpec tagSpec = new C1G2TagSpec();
		C1G2TargetTag targetTag = new C1G2TargetTag();
		targetTag.setMatch(new Bit(1));
		// We want to check memory bank 1 (the EPC memory bank).
		TwoBitField memBank = new TwoBitField();
		// Clear bit 0 and set bit 1 (bank 1 in binary).
		memBank.clear(0);
		memBank.set(1);
		targetTag.setMB(memBank);
		// The EPC data starts at offset 0x20.
		// Start reading or writing from there.
		targetTag.setPointer(new UnsignedShort(0x20));
		// This is the mask we'll use to compare the EPC.
		// We want to match all bits of the EPC, so all mask bits are set.
		BitArray_HEX tagMask = new BitArray_HEX(TAG_MASK);
		targetTag.setTagMask(tagMask);
		// We only only to operate on tags with this EPC.
		BitArray_HEX tagData = new BitArray_HEX(TARGET_EPC);
		targetTag.setTagData(tagData);

		// Add a list of target tags to the tag spec.
		List targetTagList = new ArrayList();
		targetTagList.add(targetTag);
		tagSpec.setC1G2TargetTagList(targetTagList);

		// Add the tag spec to the access command.
		accessCommand.setAirProtocolTagSpec(tagSpec);

		// A list to hold the op specs for this access command.
		List opSpecList = new ArrayList();

		// Are we reading or writing to the tag?
		// Add the appropriate op spec to the op spec list.
		if (accessSpecID == WRITE_ACCESSSPEC_ID) {
			opSpecList.add(buildEpcWriteOpSpec(writeData));
		} else {
			opSpecList.add(buildReadOpSpec());
		}

		accessCommand.setAccessCommandOpSpecList(opSpecList);

		// Add access command to access spec.
		accessSpec.setAccessCommand(accessCommand);

		// Add an AccessReportSpec.
		// We want to get notification when the operation occurs.
		// Tell the reader to sent it to us with the ROSpec.
		AccessReportSpec reportSpec = new AccessReportSpec();
		reportSpec.setAccessReportTrigger(new AccessReportTriggerType(
				AccessReportTriggerType.Whenever_ROReport_Is_Generated));

		return accessSpec;
	}

	// Add the AccessSpec to the reader.
	/*
	public void addAccessSpec(int accessSpecID) {
		ADD_ACCESSSPEC_RESPONSE response;

		AccessSpec accessSpec = buildAccessSpec(accessSpecID);
		System.out.println("Adding the AccessSpec.");
		try {
			ADD_ACCESSSPEC accessSpecMsg = new ADD_ACCESSSPEC();
			accessSpecMsg.setAccessSpec(accessSpec);
			 response = (ADD_ACCESSSPEC_RESPONSE) this.transact(accessSpecMsg, TIMEOUT_MS);
			// System.out.println(response.toXMLString());

			// Check if the we successfully added the AccessSpec.
			// StatusCode status = response.getLLRPStatus().getStatusCode();

			/*
			 * if (status.equals(new StatusCode("M_Success"))) {
			 * System.out.println("Successfully added AccessSpec."); } else {
			 * System.out.println("Error adding AccessSpec."); System.exit(1); }
			 */

	/*
		} catch (Exception e) {
			System.out.println("Error adding AccessSpec.");
			e.printStackTrace();
		}
	}
	*/

	// Finally, modify the messageReceived function so that it prints OpSpec
	// results to the console. The changes required have been highlighted below.

	public void messageReceivedAS(LLRPMessage message) {
		if (message.getTypeNum() == RO_ACCESS_REPORT.TYPENUM) {
			// The message received is an Access Report.
			RO_ACCESS_REPORT report = (RO_ACCESS_REPORT) message;
			// Get a list of the tags read.
			List<TagReportData> tags = report.getTagReportDataList();
			// Loop through the list and get the EPC of each tag.
			for (TagReportData tag : tags) {
				System.out.println(tag.getEPCParameter());
				System.out.println(tag.getLastSeenTimestampUTC());
				List<AccessCommandOpSpecResult> ops = tag
						.getAccessCommandOpSpecResultList();
				// See if any operations were performed on
				// this tag (read, write, kill).
				// If so, print out the details.
				for (AccessCommandOpSpecResult op : ops) {
					System.out.println(op.toString());
				}
			}
		}
	}
	
	
	
	
	
	
	
	// Create a OpSpec that writes to user memory

    public C1G2Write buildEpcWriteOpSpec(String writeData) {

        // Create a new OpSpec.

        // This specifies what operation we want to perform on the

        // tags that match the specifications above.

        // In this case, we want to write to the tag.

        C1G2Write opSpec = new C1G2Write();

        // Set the OpSpecID to a unique number.

        opSpec.setOpSpecID(new UnsignedShort(WRITE_OPSPEC_ID));

        opSpec.setAccessPassword(new UnsignedInteger(0));

        // For this demo, we'll write to user memory (bank 3).

        TwoBitField opMemBank = new TwoBitField();

        // Set bit 1 (bank 1 in binary).

        opMemBank.set(1);

        opMemBank.clear(0);

        opSpec.setMB(opMemBank);

        // We'll write to the base of this memory bank (0x00).

        opSpec.setWordPointer(new UnsignedShort(0x02));

        UnsignedShortArray_HEX writeArray = new UnsignedShortArray_HEX();

        // We'll write 12 bytes or three words.
        
        for (String word : writeData.split(":")) {
			System.out.println("Adding short: " + word);
			writeArray.add(new UnsignedShort(word, 16));
		}

        /*
        writeData.add(new UnsignedShort(0x0000));

        writeData.add(new UnsignedShort(0x0000));

        writeData.add(new UnsignedShort(0x0000));

        writeData.add(new UnsignedShort(0x0000));

        writeData.add(new UnsignedShort(0x0000));

        writeData.add(new UnsignedShort(0x5555));
        */

        opSpec.setWriteData(writeArray); 

       return opSpec;

    }



}

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
import org.llrp.ltk.generated.enumerations.C1G2LockDataField;
import org.llrp.ltk.generated.enumerations.C1G2LockPrivilege;
import org.llrp.ltk.generated.enumerations.GetReaderCapabilitiesRequestedData;
import org.llrp.ltk.generated.enumerations.NotificationEventType;
import org.llrp.ltk.generated.enumerations.ROReportTriggerType;
import org.llrp.ltk.generated.enumerations.StatusCode;
import org.llrp.ltk.generated.interfaces.AccessCommandOpSpec;
import org.llrp.ltk.generated.interfaces.AccessCommandOpSpecResult;
import org.llrp.ltk.generated.messages.ADD_ACCESSSPEC;
import org.llrp.ltk.generated.messages.ADD_ACCESSSPEC_RESPONSE;
import org.llrp.ltk.generated.messages.DELETE_ACCESSSPEC;
import org.llrp.ltk.generated.messages.ENABLE_ACCESSSPEC;
import org.llrp.ltk.generated.messages.GET_READER_CAPABILITIES;
import org.llrp.ltk.generated.messages.RO_ACCESS_REPORT;
import org.llrp.ltk.generated.messages.SET_READER_CONFIG;
import org.llrp.ltk.generated.messages.SET_READER_CONFIG_RESPONSE;
import org.llrp.ltk.generated.parameters.AccessCommand;
import org.llrp.ltk.generated.parameters.AccessReportSpec;
import org.llrp.ltk.generated.parameters.AccessSpec;
import org.llrp.ltk.generated.parameters.AccessSpecStopTrigger;
import org.llrp.ltk.generated.parameters.C1G2BlockEraseOpSpecResult;
import org.llrp.ltk.generated.parameters.C1G2BlockWrite;
import org.llrp.ltk.generated.parameters.C1G2BlockWriteOpSpecResult;
import org.llrp.ltk.generated.parameters.C1G2EPCMemorySelector;
import org.llrp.ltk.generated.parameters.C1G2KillOpSpecResult;
import org.llrp.ltk.generated.parameters.C1G2Lock;
import org.llrp.ltk.generated.parameters.C1G2LockOpSpecResult;
import org.llrp.ltk.generated.parameters.C1G2LockPayload;
import org.llrp.ltk.generated.parameters.C1G2Read;
import org.llrp.ltk.generated.parameters.C1G2ReadOpSpecResult;
import org.llrp.ltk.generated.parameters.C1G2TagSpec;
import org.llrp.ltk.generated.parameters.C1G2TargetTag;
import org.llrp.ltk.generated.parameters.C1G2WriteOpSpecResult;
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

	public Long lastTagTimestamp;

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

	/** Indicates if this session is in the middle of LLRP encode operations **/
	private boolean isRunningLLRPEncoding = false;

	/** the EPC Tag Data to perform operation on **/
	private String targetEpc;

	/**
	 * the Tag Mask to perform operation on (0 is wildcard match, F is Exact
	 * Match)
	 **/
	private String tagMask;

	/**
	 * the duration of time to attempt operation (O is infinite until operation
	 * occurs, set in milliseconds)
	 **/
	private Integer operationsTimeout;

	/** Access Password (Hex Value) **/
	private String accessPwd;

	/** Kill Password (Hex Value) **/
	private String killPwd;

	/**
	 * value to perform a Kill Password Lock Operation (values can be Read_Write
	 * (which Locks), Perma_Lock, Unlock)
	 **/
	private int killPwdLockPrivilege;

	/**
	 * value to perform an Access Password Lock Operation (values can be
	 * Read_Write (which Locks), Perma_Lock, Unlock)
	 **/
	private int accessPwdLockPrivilege;

	/**
	 * value to perform an EPC Lock Operation (values can be Read_Write (which
	 * Locks), Perma_Lock, Unlock)
	 **/
	private int epcLockPrivilege;

	/**
	 * Indicates the count of LLRP operations that should be executed on this
	 * session
	 **/
	private int operationNumber;

	/** Default target Epc value if there is no property read from jvm **/
	public static final String DEFAULT_TARGET_EPC = "000000000000000000000000";

	/**
	 * Default tag mask value if there is no property read from jvm. (0 is
	 * wildcard match, F is Exact Match)
	 **/
	public static final String DEFAULT_TAG_MASK = "000000000000000000000000";

	/**
	 * Default operations timeout value if there is no property read from jvm. O
	 * is infinite until operation occurs, set in milliseconds
	 **/
	public static final int DEFAULT_OPERATIONS_TIMEOUT = 0;

	/**
	 * Default access password value if there is no property read from jvm. (Hex
	 * Value)
	 **/
	public static final String DEFAULT_ACCESS_PASSWORD = "0";

	/**
	 * Default kill password value if there is no property read from jvm. (Hex
	 * Value)
	 **/
	public static final String DEFAULT_KILL_PASSWORD = "0";

	/**
	 * Default kill password lock privilege if there is no property read from
	 * jvm. (values can be Read_Write (which Locks), Perma_Lock, Unlock)
	 **/
	public static final int DEFAULT_KILL_PASSWORD_LOCK_PRIVILEGE = C1G2LockPrivilege.Read_Write;

	/**
	 * Default access password lock privilege if there is no property read from
	 * jvm. (values can be Read_Write (which Locks), Perma_Lock, Unlock)
	 **/
	public static final int DEFAULT_ACCESS_PASSWORD_LOCK_PRIVILEGE = C1G2LockPrivilege.Read_Write;

	/**
	 * Default EPC lock privilege if there is no property read from jvm. (values
	 * can be Read_Write (which Locks), Perma_Lock, Unlock)
	 **/
	public static final int DEFAULT_EPC_LOCK_PRIVILEGE = C1G2LockPrivilege.Read_Write;

	/** Expected value for Read_Write lock privilege taken from jvm **/
	public final static String READ_WRITE_LOCK_PRIVILEGE = "Read_Write";

	/** Expected value for Perma_Lock lock privilege taken from jvm **/
	public final static String PERMA_LOCK_PRIVILEGE = "Perma_Lock";

	/** Expected value for Unlock lock privilege taken from jvm **/
	public final static String UNLOCK_PRIVILEGE = "Unlock";

	private static final int WRITE_EPC_ACCESSSPEC_ID = 1;
	private static final int WRITE_EPC_OPSPEC_ID = 2;

	private static final int WRITE_KILLPASS_ACCESSSPEC_ID = 3;
	private static final int WRITE_KILLPASS_OPSPEC_ID = 4;

	private static final int WRITE_ACCESSPASS_ACCESSSPEC_ID = 5;
	private static final int WRITE_ACCESSPASS_OPSPEC_ID = 6;

	private static final int LOCK_EPC_ACCESSSPEC_ID = 7;
	private static final int LOCK_EPC_OPSPEC_ID = 8;

	private static final int LOCK_KILLPASS_ACCESSSPEC_ID = 9;
	private static final int LOCK_KILLPASS_OPSPEC_ID = 10;

	private static final int LOC_ACCESSPASS_ACCESSSPEC_ID = 11;
	private static final int LOC_ACCESSPASS_OPSPEC_ID = 12;

	/**
	 * @return the isRunningLLRPEncoding
	 */
	public boolean isRunningLLRPEncoding() {
		return isRunningLLRPEncoding;
	}

	/**
	 * @param isRunningLLRPEncoding
	 *            the isRunningLLRPEncoding to set
	 */
	public void setRunningLLRPEncoding(boolean isRunningLLRPEncoding) {
		this.isRunningLLRPEncoding = isRunningLLRPEncoding;
	}

	/**
	 * @return the targetEpc
	 */
	public String getTargetEpc() {
		return targetEpc;
	}

	/**
	 * @param targetEpc
	 *            the targetEpc to set
	 */
	public void setTargetEpc(String targetEpc) {
		this.targetEpc = targetEpc;
	}

	/**
	 * @return the tagMask
	 */
	public String getTagMask() {
		return tagMask;
	}

	/**
	 * @param tagMask
	 *            the tagMask to set
	 */
	public void setTagMask(String tagMask) {
		this.tagMask = tagMask;
	}

	/**
	 * @return the operationsTimeout
	 */
	public Integer getOperationsTimeout() {
		return operationsTimeout;
	}

	/**
	 * @param operationsTimeout
	 *            the operationsTimeout to set
	 */
	public void setOperationsTimeout(Integer operationsTimeout) {
		this.operationsTimeout = operationsTimeout;
	}

	/**
	 * @return the accessPwd
	 */
	public String getAccessPwd() {
		return accessPwd;
	}

	/**
	 * @param accessPwd
	 *            the accessPwd to set
	 */
	public void setAccessPwd(String accessPwd) {
		this.accessPwd = accessPwd;
	}

	/**
	 * @return the killPwd
	 */
	public String getKillPwd() {
		return killPwd;
	}

	/**
	 * @param killPwd
	 *            the killPwd to set
	 */
	public void setKillPwd(String killPwd) {
		this.killPwd = killPwd;
	}

	/**
	 * @return the killPwdLockPrivilege
	 */
	public int getKillPwdLockPrivilege() {
		return killPwdLockPrivilege;
	}

	/**
	 * @param killPwdLockPrivilege
	 *            the killPwdLockPrivilege to set
	 */
	public void setKillPwdLockPrivilege(int killPwdLockPrivilege) {
		this.killPwdLockPrivilege = killPwdLockPrivilege;
	}

	/**
	 * @return the accessPwdLockPrivilege
	 */
	public int getAccessPwdLockPrivilege() {
		return accessPwdLockPrivilege;
	}

	/**
	 * @param accessPwdLockPrivilege
	 *            the accessPwdLock to set
	 */
	public void setAccessPwdLockPrivilege(int accessPwdLockPrivilege) {
		this.accessPwdLockPrivilege = accessPwdLockPrivilege;
	}

	/**
	 * @return the epcLockPrivilege
	 */
	public int getEpcLockPrivilege() {
		return epcLockPrivilege;
	}

	/**
	 * @param epcLockPrivilege
	 *            the epcLockPrivilege to set
	 */
	public void setEpcLockPrivilege(int epcLockPrivilege) {
		this.epcLockPrivilege = epcLockPrivilege;
	}

	/**
	 * @return the operationNumber
	 */
	public int getOperationNumber() {
		return operationNumber;
	}

	/**
	 * @param operationNumber
	 *            the operationNumber to set
	 */
	public void setOperationNumber(int operationNumber) {
		this.operationNumber = operationNumber;
	}

	/**
	 * Decrements operation number
	 */
	public void decrementOperationNumber() {
		operationNumber--;
	}

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
			this.lastTagTimestamp = System.currentTimeMillis();
			submit(getTimeoutCommand(this), 10, TimeUnit.SECONDS);
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
	private Command getTimeoutCommand(final LLRPReaderSession session) {
		return new TimeoutCommand("LLRP Timeout") {

			@Override
			protected void execute() throws TimeoutException {
				// If the last tag was seen less than 10 seconds ago, do
				// nothing.
				Long tenSecondsAgo = System.currentTimeMillis() - 10000;
				if (tenSecondsAgo > session.lastTagTimestamp) {
					GET_READER_CAPABILITIES grc = new GET_READER_CAPABILITIES();
					GetReaderCapabilitiesRequestedData data = new GetReaderCapabilitiesRequestedData();
					data.set(GetReaderCapabilitiesRequestedData.LLRP_Capabilities);
					grc.setRequestedData(data);
					transact(grc);
				}
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

	private ADD_ACCESSSPEC_RESPONSE executeOperation(int accessSpecId, String epcId)
			throws InvalidLLRPMessageException, TimeoutException {

		// try {
		System.out.println("Add accessspec! " + accessSpecId);
		// //// int accessSpecId = 2;
		AccessSpec spec = buildAccessSpec(accessSpecId, epcId);
		ADD_ACCESSSPEC aas = new ADD_ACCESSSPEC();
		aas.setAccessSpec(spec);
		System.out.println(aas.toXMLString());
		ADD_ACCESSSPEC_RESPONSE response = null;

		response = (ADD_ACCESSSPEC_RESPONSE) this.transact(aas);
		System.out.println("Response: " + response.toXMLString());

		ENABLE_ACCESSSPEC enablespec = new ENABLE_ACCESSSPEC();
		enablespec.setAccessSpecID(new UnsignedInteger(accessSpecId));

		this.send(enablespec);

		return response;
		/*
		 * } catch (Exception e) { e.printStackTrace(); return e.getMessage(); }
		 */
	}

	// TODO to exceute only this operation:
	public void llrpEpcWriteOperation() {

	}

	// TODO to exceute only this operation:
	public void llrpWriteAccessPasswordOperation() {

	}

	// public String addAccessSpec(String writeAccessPassword, String writeData
	// ) {
	public void llrpEncode(String strTag) throws TimeoutException,
			InvalidLLRPMessageException {

		// TODO: Throw exception if writeData does not divide evenly by 4

		// the counter should begin in fixed value 6, and decrement on every
		// operation call, until it reaches zero
		// on the first called operation, start the session and the timer
		setRunningLLRPEncoding(true);
		setOperationNumber(6);

		// 1. Call write EPC id
		executeOperation(WRITE_EPC_ACCESSSPEC_ID, strTag);

		// 2. Call write kill pwd
		executeOperation(WRITE_KILLPASS_ACCESSSPEC_ID, strTag);

		// 3. Call write access pwd
		executeOperation(WRITE_ACCESSPASS_ACCESSSPEC_ID, strTag);

		// 4. Call lock the EPC
		executeOperation(LOCK_EPC_ACCESSSPEC_ID, strTag);

		// 5. lock kill pwd
		executeOperation(LOCK_KILLPASS_ACCESSSPEC_ID, strTag);

		// 6. lock access pwd
		executeOperation(LOC_ACCESSPASS_ACCESSSPEC_ID, strTag);

		/*
		 * try { System.out.println("Add accessspec! "); int accessSpecId = 2;
		 * AccessSpec spec = buildAccessSpec(accessSpecId, strTag,
		 * LLRPWriteOperationEnum.WRITE_EPC); ADD_ACCESSSPEC aas = new
		 * ADD_ACCESSSPEC(); aas.setAccessSpec(spec);
		 * System.out.println(aas.toXMLString()); ADD_ACCESSSPEC_RESPONSE
		 * response = null; try { response = (ADD_ACCESSSPEC_RESPONSE)
		 * this.transact(aas); System.out.println("Response: " +
		 * response.toXMLString());
		 * 
		 * ENABLE_ACCESSSPEC enablespec = new ENABLE_ACCESSSPEC();
		 * enablespec.setAccessSpecID(new UnsignedInteger(accessSpecId));
		 * 
		 * this.send(enablespec);
		 * 
		 * return response.toXMLString(); } catch (TimeoutException e) { return
		 * e.getMessage(); } } catch (Exception e) { e.printStackTrace(); return
		 * e.getMessage(); }
		 */
	}

	/**
	 * Split the hex array into groups of 4 by splitting on a colon charater.
	 * 
	 * @param arg
	 * @return
	 */
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
			this.lastTagTimestamp = System.currentTimeMillis();
			// The message received is an Access Report.
			RO_ACCESS_REPORT report = (RO_ACCESS_REPORT) arg0;
			// Get a list of the tags read.
			List<TagReportData> tags = report.getTagReportDataList();
			// Loop through the list and get the EPC of each tag.
			for (TagReportData tag : tags) {
				// System.out.println(tag.getEPCParameter());
				// System.out.println(tag.getLastSeenTimestampUTC());
				List<AccessCommandOpSpecResult> ops = tag
						.getAccessCommandOpSpecResultList();
				// See if any operations were performed on
				// this tag (read, write, kill).
				// If so, print out the details.

				for (AccessCommandOpSpecResult op : ops) {

					if (op instanceof C1G2BlockEraseOpSpecResult) {

						C1G2BlockEraseOpSpecResult res = (C1G2BlockEraseOpSpecResult) op;
						System.out.println("res.getResult(): "
								+ res.getResult());

					} else if (op instanceof C1G2BlockWriteOpSpecResult) {

						C1G2BlockWriteOpSpecResult res = (C1G2BlockWriteOpSpecResult) op;
						System.out.println("res.getResult(): "
								+ res.getResult());

					} else if (op instanceof C1G2KillOpSpecResult) {

						C1G2KillOpSpecResult res = (C1G2KillOpSpecResult) op;
						System.out.println("res.getResult(): "
								+ res.getResult());

					} else if (op instanceof C1G2LockOpSpecResult) {

						C1G2LockOpSpecResult res = (C1G2LockOpSpecResult) op;
						System.out.println("res.getResult(): "
								+ res.getResult());

					} else if (op instanceof C1G2ReadOpSpecResult) {

						C1G2ReadOpSpecResult res = (C1G2ReadOpSpecResult) op;
						System.out.println("res.getResult(): "
								+ res.getResult());

					} else if (op instanceof C1G2WriteOpSpecResult) {

						C1G2WriteOpSpecResult res = (C1G2WriteOpSpecResult) op;
						System.out.println("res.getResult(): "
								+ res.getResult());

					}

					// Delete access spec if there is a CommandOpSpecResult
					// coming back.
					this.deleteAccessSpecs();
					System.out.println(op.toString());

					// Decrement the operation number counter
					decrementOperationNumber();

					// TODO test if counter == 0. If it is 0, check if there is
					// any error in the list of tracked errors

					// TODO
					// substract one unit from the number of operations, once it
					// reaches to zero, reset session, write to topic, or , when
					// the timeout
					// comes
					// set the timeout with the first operation

					// TODO keep track of every error message on every operation
					// : for fail

					// TODO return a single success message if all operations
					// success

				}
			}
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

	/*
	public enum LLRPWriteOperationEnum {
		WRITE_EPC, WRITE_ACCESSPASS, WRITE_KILLPASS, LOCK_EPC, LOCK_KILL_PASSWORD, LOCK_ACCESS_PASSWORD
	}
	*/

	// The TAG_MASK and TARGET_EPC constants are used to define which tags we
	// want to write to. The reader will take each tag EPC and bitwise AND it
	// with the TAG_MASK. If the result matches the value specified in the
	// TARGET_EPC constant, the reader will write to that tag. The other
	// constants defined above are used for message IDs. They can be any unique
	// integer value. Next, we'll add the functions required to read and write
	// user memory.

	// Enable the AccessSpec
	public void enableAccessSpec(int accessSpecID) {
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

	/*
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
	*/

	// Create an AccessSpec.
	// It will contain our two OpSpecs (read and write).
	// receive the type of operation
	// public AccessSpec buildAccessSpec(int accessSpecID, String writeData,
	public AccessSpec buildAccessSpec(int accessSpecID, String epcId) {
		// LLRPWriteOperationEnum writeOperation) {

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
		BitArray_HEX hexTagMask = new BitArray_HEX(getTagMask());
		targetTag.setTagMask(hexTagMask);
		// We only only to operate on tags with this EPC.
		BitArray_HEX hexTagData = new BitArray_HEX(getTargetEpc());
		targetTag.setTagData(hexTagData);

		// Add a list of target tags to the tag spec.
		List<C1G2TargetTag> targetTagList = new ArrayList<C1G2TargetTag>();
		targetTagList.add(targetTag);
		tagSpec.setC1G2TargetTagList(targetTagList);

		// Add the tag spec to the access command.
		accessCommand.setAirProtocolTagSpec(tagSpec);

		// A list to hold the op specs for this access command.
		List<AccessCommandOpSpec> opSpecList = new ArrayList<AccessCommandOpSpec>();

		// Are we reading or writing to the tag?
		// Add the appropriate op spec to the op spec list.

		// TODO have here specific if statements, without else
		// TODO build six different methods, like buildEpcWriteOpSpec, and
		// pass the appropiate parameter as needed
		// if (operation == )

		if (accessSpecID == WRITE_EPC_ACCESSSPEC_ID) {

			opSpecList.add(buildEpcWriteOpSpec(epcId));

		} else if (accessSpecID == WRITE_KILLPASS_ACCESSSPEC_ID) {

			opSpecList.add(buildEpcWriteKillPass());

		} else if (accessSpecID == WRITE_ACCESSPASS_ACCESSSPEC_ID) {

			// TODO instead of hard code these passwords should be taken from
			// parameters
			// access password: comes from jvm
			// old password: set it at the beginning of operation (the one that
			// i'll rename)
			opSpecList.add(buildEpcWriteAccessPass(new Integer(0)));

		} else if (accessSpecID == LOC_ACCESSPASS_ACCESSSPEC_ID) {

			opSpecList.add(buildLockOpSpec(LOC_ACCESSPASS_OPSPEC_ID,
					getAccessPwdLockPrivilege(),
					C1G2LockDataField.Access_Password));

		} else if (accessSpecID == LOCK_KILLPASS_ACCESSSPEC_ID) {

			opSpecList
					.add(buildLockOpSpec(LOCK_KILLPASS_OPSPEC_ID,
							getKillPwdLockPrivilege(),
							C1G2LockDataField.Kill_Password));

		} else if (accessSpecID == LOCK_EPC_ACCESSSPEC_ID) {

			opSpecList.add(buildLockOpSpec(LOCK_EPC_OPSPEC_ID, getEpcLockPrivilege(),
					C1G2LockDataField.EPC_Memory));

		}

		/*
		 * else { // TODO remove this else statement, and add the other three
		 * opSpecList.add(buildReadOpSpec()); }
		 */

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

	// Create a OpSpec that writes to epc memory
	public C1G2BlockWrite buildEpcWriteOpSpec(String epcId) {
		// Create a new OpSpec.
		// This specifies what operation we want to perform on the
		// tags that match the specifications above.
		// In this case, we want to write to the tag.
		C1G2BlockWrite opSpec = new C1G2BlockWrite();

		// Set the OpSpecID to a unique number.

		// opSpec.setOpSpecID(new UnsignedShort(WRITE_OPSPEC_ID));
		opSpec.setOpSpecID(new UnsignedShort(WRITE_EPC_OPSPEC_ID));

		opSpec.setAccessPassword(new UnsignedInteger(getAccessPwd()));

		// For this demo, we'll write to user memory (bank 3).
		TwoBitField opMemBank = new TwoBitField();

		// Set bit 1 (bank 2 in binary).
		opMemBank.set(1);
		opMemBank.clear(0);

		opSpec.setMB(opMemBank);

		opSpec.setWordPointer(new UnsignedShort(0x02));

		UnsignedShortArray_HEX writeArray = new UnsignedShortArray_HEX();

		// We'll write 12 bytes or three words.

		for (String word : epcId.split(":")) {
			System.out.println("Adding short: " + word);
			writeArray.add(new UnsignedShort(word, 16));
		}

		opSpec.setWriteData(writeArray);

		return opSpec;
	}

	// Create a OpSpec that writes the access password
	// TODO for a new tag, an old password is always zero
	public C1G2BlockWrite buildEpcWriteAccessPass(Integer oldPass) {
		// Create a new OpSpec.
		// This specifies what operation we want to perform on the
		// tags that match the specifications above.
		// In this case, we want to write to the tag.
		C1G2BlockWrite opSpec = new C1G2BlockWrite();
		// Set the OpSpecID to a unique number.
		opSpec.setOpSpecID(new UnsignedShort(WRITE_ACCESSPASS_OPSPEC_ID));
		opSpec.setAccessPassword(new UnsignedInteger(oldPass));

		// For this demo, we'll write to user memory (bank 3).
		TwoBitField opMemBank = new TwoBitField();
		// Clear twobits (bank 0 in binary).
		opMemBank.clear(1);
		opMemBank.clear(0);

		opSpec.setMB(opMemBank);
		opSpec.setWordPointer(new UnsignedShort(2));

		// new pass is 8 characters long
		// cut by 4 character blocks -> for access password and kill password
		// change to this same logic, the loop for epc tag
		UnsignedShortArray_HEX writeArray = new UnsignedShortArray_HEX();
		for (String word : getAccessPwd().split(":")) {
			System.out.println("Adding short: " + word);
			writeArray.add(new UnsignedShort(word, 16));
		}

		opSpec.setWriteData(writeArray);

		return opSpec;
	}

	/*
	 * private UnsignedShortArray_HEX getWriteData(String value){
	 * 
	 * //The value comes with no ':' symbols //always split it into 4 characters
	 * 
	 * 
	 * }
	 */

	// Create a OpSpec that writes the access password
	public C1G2BlockWrite buildEpcWriteKillPass() {
		// Create a new OpSpec.
		// This specifies what operation we want to perform on the
		// tags that match the specifications above.
		// In this case, we want to write to the tag.
		C1G2BlockWrite opSpec = new C1G2BlockWrite();
		// Set the OpSpecID to a unique number.
		opSpec.setOpSpecID(new UnsignedShort(WRITE_KILLPASS_OPSPEC_ID));
		opSpec.setAccessPassword(new UnsignedInteger(getAccessPwd()));

		// For this demo, we'll write to user memory (bank 3).
		TwoBitField opMemBank = new TwoBitField();
		// Clear twobits (bank 0 in binary).
		opMemBank.clear(1);
		opMemBank.clear(0);

		opSpec.setMB(opMemBank);
		opSpec.setWordPointer(new UnsignedShort(0x02));

		UnsignedShortArray_HEX writeArray = new UnsignedShortArray_HEX();
		for (String word : getKillPwd().split(":")) {
			System.out.println("Adding short: " + word);
			writeArray.add(new UnsignedShort(word, 16));
		}

		opSpec.setWriteData(writeArray);

		return opSpec;
	}

	/**
	 * Converts string representation of lock privilege into int representation
	 * 
	 * @param lockPrivilege
	 *            the string lock value to be parsed into int representation
	 * @return int representation of this string lock value
	 * @throws Exception
	 *             if lockPrivilege is invalid, that is, is not any of
	 *             Read_Write, Perma_Lock or Unlock string values
	 */
	public static int getLockPrivilege(String lockPrivilege) throws Exception {

		switch (lockPrivilege) {

		case READ_WRITE_LOCK_PRIVILEGE:
			return C1G2LockPrivilege.Read_Write;

		case PERMA_LOCK_PRIVILEGE:
			return C1G2LockPrivilege.Perma_Lock;

		case UNLOCK_PRIVILEGE:
			return C1G2LockPrivilege.Unlock;

		default:
			throw new Exception("Invalid lock privilege value: "
					+ lockPrivilege + ". Valid ones are: "
					+ LLRPReaderSession.READ_WRITE_LOCK_PRIVILEGE + ", "
					+ LLRPReaderSession.PERMA_LOCK_PRIVILEGE + ", "
					+ LLRPReaderSession.UNLOCK_PRIVILEGE);

		}

	}

	/**
	 * 
	 * @param opSpecId
	 * @param lockPrivilege
	 * @param lockDataField
	 * @return
	 */
	public C1G2Lock buildLockOpSpec(int opSpecId, int lockPrivilege,
			int lockDataField) {
		// Create a new OpSpec.
		// This specifies what operation we want to perform on the
		// tags that match the specifications above.
		// In this case, we want to write to the tag.
		C1G2Lock opSpec = new C1G2Lock();

		// Set the OpSpecID to a unique number.

		opSpec.setOpSpecID(new UnsignedShort(opSpecId));

		// opSpec.setWordPointer(new UnsignedShort(0x02));->only for writing

		opSpec.setAccessPassword(new UnsignedInteger(getAccessPwd()));
		C1G2LockPayload payload = new C1G2LockPayload();
		C1G2LockDataField dataField = new C1G2LockDataField(lockDataField);
		payload.setDataField(dataField);

		C1G2LockPrivilege privilege = new C1G2LockPrivilege(lockPrivilege);
		payload.setPrivilege(privilege);

		List<C1G2LockPayload> c1G2LockPayloadList = new ArrayList<>();
		c1G2LockPayloadList.add(payload);

		opSpec.setC1G2LockPayloadList(c1G2LockPayloadList);

		return opSpec;
	}

	/*
	 * public C1G2Lock buildLockAccessPWDOpSpec(String accesspassword) { //
	 * Create a new OpSpec. // This specifies what operation we want to perform
	 * on the // tags that match the specifications above. // In this case, we
	 * want to write to the tag. C1G2Lock opSpec = new C1G2Lock();
	 * 
	 * // Set the OpSpecID to a unique number.
	 * 
	 * opSpec.setOpSpecID(new UnsignedShort(AccessPWDLock_OPSPEC_ID)); ///comes
	 * from private member constant
	 * 
	 * 
	 * opSpec.setWordPointer(new UnsignedShort(0x02)); opSpec.AccessPassword =
	 * accesspassword; C1G2LockPayload payload = new C1G2LockPayload(); opspec.
	 * payload.DataField = C1G2LockDataField.Access_Password;; opspec.
	 * payload.Privilege = C1G2LockPrivilege.Read_Write; }
	 * 
	 * 
	 * return opSpec; } public C1G2Lock buildLockKillWDOpSpec(String
	 * accesspassword) { // Create a new OpSpec. // This specifies what
	 * operation we want to perform on the // tags that match the specifications
	 * above. // In this case, we want to write to the tag. C1G2Lock opSpec =
	 * new C1G2Lock();
	 * 
	 * // Set the OpSpecID to a unique number.
	 * 
	 * opSpec.setOpSpecID(new UnsignedShort(KillPWDLock_OPSPEC_ID)); ///comes
	 * from private member constant
	 * 
	 * 
	 * opSpec.setWordPointer(new UnsignedShort(0x02)); opSpec.AccessPassword =
	 * accesspassword; C1G2LockPayload payload = new C1G2LockPayload(); opspec.
	 * payload.DataField = C1G2LockDataField.Kill_Password; opspec.
	 * payload.Privilege = C1G2LockPrivilege.Read_Write; }
	 * 
	 * 
	 * return opSpec; } public C1G2Lock buildUnLockEPCOpSpec(String
	 * accesspassword) { // Create a new OpSpec. // This specifies what
	 * operation we want to perform on the // tags that match the specifications
	 * above. // In this case, we want to write to the tag. C1G2Lock opSpec =
	 * new C1G2Lock();
	 * 
	 * // Set the OpSpecID to a unique number.
	 * 
	 * opSpec.setOpSpecID(new UnsignedShort(EPCLock_OPSPEC_ID)); ///comes from
	 * private member constant
	 * 
	 * 
	 * opSpec.setWordPointer(new UnsignedShort(0x02));
	 * 
	 * opSpec.AccessPassword = accesspassword; C1G2LockPayload payload = new
	 * C1G2LockPayload(); opspec.payload.DataField
	 * =C1G2LockDataField.EPC_Memory; opspec.payload.Privilege =
	 * C1G2LockPrivilege.Unlock; }
	 * 
	 * 
	 * return opSpec; } public C1G2Lock buildUnLockAccessPWDOpSpec(String
	 * accesspassword) { // Create a new OpSpec. // This specifies what
	 * operation we want to perform on the // tags that match the specifications
	 * above. // In this case, we want to write to the tag. C1G2Lock opSpec =
	 * new C1G2Lock();
	 * 
	 * // Set the OpSpecID to a unique number.
	 * 
	 * opSpec.setOpSpecID(new UnsignedShort(AccessPWDLock_OPSPEC_ID)); ///comes
	 * from private member constant
	 * 
	 * 
	 * opSpec.setWordPointer(new UnsignedShort(0x02)); opSpec.AccessPassword =
	 * accesspassword; C1G2LockPayload payload = new C1G2LockPayload(); opspec.
	 * payload.DataField = C1G2LockDataField.Access_Password;; opspec.
	 * payload.Privilege = C1G2LockPrivilege.Unlock; }
	 * 
	 * 
	 * return opSpec; } public C1G2Lock buildUnLockKillWDOpSpec(String
	 * accesspassword) { // Create a new OpSpec. // This specifies what
	 * operation we want to perform on the // tags that match the specifications
	 * above. // In this case, we want to write to the tag. C1G2Lock opSpec =
	 * new C1G2Lock();
	 * 
	 * // Set the OpSpecID to a unique number.
	 * 
	 * opSpec.setOpSpecID(new UnsignedShort(KillPWDLock_OPSPEC_ID)); ///comes
	 * from private member constant
	 * 
	 * 
	 * opSpec.setWordPointer(new UnsignedShort(0x02)); opSpec.AccessPassword =
	 * accesspassword; C1G2LockPayload payload = new C1G2LockPayload(); opspec.
	 * payload.DataField = C1G2LockDataField.Kill_Password; opspec.
	 * payload.Privilege = C1G2LockPrivilege.Unlock; }
	 * 
	 * 
	 * return opSpec; }
	 */

	/*
	 * public C1G2Lock buildLockEPCOpSpec(String accesspassword) { // Create a
	 * new OpSpec. // This specifies what operation we want to perform on the //
	 * tags that match the specifications above. // In this case, we want to
	 * write to the tag. C1G2Lock opSpec = new C1G2Lock();
	 * 
	 * // Set the OpSpecID to a unique number.
	 * 
	 * opSpec.setOpSpecID(new UnsignedShort(EPCLock_OPSPEC_ID)); ///comes from
	 * private member constant
	 * 
	 * 
	 * //opSpec.setWordPointer(new UnsignedShort(0x02));->only for writing
	 * 
	 * opSpec.AccessPassword = accesspassword; C1G2LockPayload payload = new
	 * C1G2LockPayload(); opSpec.payload.DataField
	 * =C1G2LockDataField.EPC_Memory; opSpec.payload.Privilege =
	 * C1G2LockPrivilege.Read_Write; }
	 * 
	 * 
	 * return opSpec; } public C1G2Lock buildLockAccessPWDOpSpec(String
	 * accesspassword) { // Create a new OpSpec. // This specifies what
	 * operation we want to perform on the // tags that match the specifications
	 * above. // In this case, we want to write to the tag. C1G2Lock opSpec =
	 * new C1G2Lock();
	 * 
	 * // Set the OpSpecID to a unique number.
	 * 
	 * opSpec.setOpSpecID(new UnsignedShort(AccessPWDLock_OPSPEC_ID)); ///comes
	 * from private member constant
	 * 
	 * 
	 * opSpec.setWordPointer(new UnsignedShort(0x02)); opSpec.AccessPassword =
	 * accesspassword; C1G2LockPayload payload = new C1G2LockPayload(); opspec.
	 * payload.DataField = C1G2LockDataField.Access_Password;; opspec.
	 * payload.Privilege = C1G2LockPrivilege.Read_Write; }
	 * 
	 * 
	 * return opSpec; } public C1G2Lock buildLockKillWDOpSpec(String
	 * accesspassword) { // Create a new OpSpec. // This specifies what
	 * operation we want to perform on the // tags that match the specifications
	 * above. // In this case, we want to write to the tag. C1G2Lock opSpec =
	 * new C1G2Lock();
	 * 
	 * // Set the OpSpecID to a unique number.
	 * 
	 * opSpec.setOpSpecID(new UnsignedShort(KillPWDLock_OPSPEC_ID)); ///comes
	 * from private member constant
	 * 
	 * 
	 * opSpec.setWordPointer(new UnsignedShort(0x02)); opSpec.AccessPassword =
	 * accesspassword; C1G2LockPayload payload = new C1G2LockPayload(); opspec.
	 * payload.DataField = C1G2LockDataField.Kill_Password; opspec.
	 * payload.Privilege = C1G2LockPrivilege.Read_Write; }
	 * 
	 * 
	 * return opSpec; } public C1G2Lock buildUnLockEPCOpSpec(String
	 * accesspassword) { // Create a new OpSpec. // This specifies what
	 * operation we want to perform on the // tags that match the specifications
	 * above. // In this case, we want to write to the tag. C1G2Lock opSpec =
	 * new C1G2Lock();
	 * 
	 * // Set the OpSpecID to a unique number.
	 * 
	 * opSpec.setOpSpecID(new UnsignedShort(EPCLock_OPSPEC_ID)); ///comes from
	 * private member constant
	 * 
	 * 
	 * opSpec.setWordPointer(new UnsignedShort(0x02));
	 * 
	 * opSpec.AccessPassword = accesspassword; C1G2LockPayload payload = new
	 * C1G2LockPayload(); opspec.payload.DataField
	 * =C1G2LockDataField.EPC_Memory; opspec.payload.Privilege =
	 * C1G2LockPrivilege.Unlock; }
	 * 
	 * 
	 * return opSpec; } public C1G2Lock buildUnLockAccessPWDOpSpec(String
	 * accesspassword) { // Create a new OpSpec. // This specifies what
	 * operation we want to perform on the // tags that match the specifications
	 * above. // In this case, we want to write to the tag. C1G2Lock opSpec =
	 * new C1G2Lock();
	 * 
	 * // Set the OpSpecID to a unique number.
	 * 
	 * opSpec.setOpSpecID(new UnsignedShort(AccessPWDLock_OPSPEC_ID)); ///comes
	 * from private member constant
	 * 
	 * 
	 * opSpec.setWordPointer(new UnsignedShort(0x02)); opSpec.AccessPassword =
	 * accesspassword; C1G2LockPayload payload = new C1G2LockPayload(); opspec.
	 * payload.DataField = C1G2LockDataField.Access_Password;; opspec.
	 * payload.Privilege = C1G2LockPrivilege.Unlock; }
	 * 
	 * 
	 * return opSpec; } public C1G2Lock buildUnLockKillWDOpSpec(String
	 * accesspassword) { // Create a new OpSpec. // This specifies what
	 * operation we want to perform on the // tags that match the specifications
	 * above. // In this case, we want to write to the tag. C1G2Lock opSpec =
	 * new C1G2Lock();
	 * 
	 * // Set the OpSpecID to a unique number.
	 * 
	 * opSpec.setOpSpecID(new UnsignedShort(KillPWDLock_OPSPEC_ID)); ///comes
	 * from private member constant
	 * 
	 * 
	 * opSpec.setWordPointer(new UnsignedShort(0x02)); opSpec.AccessPassword =
	 * accesspassword; C1G2LockPayload payload = new C1G2LockPayload(); opspec.
	 * payload.DataField = C1G2LockDataField.Kill_Password; opspec.
	 * payload.Privilege = C1G2LockPrivilege.Unlock; }
	 * 
	 * 
	 * return opSpec; }
	 */

	/*
	 * 
	 * public C1G2Lock buildLockEPCOpSpec(String accesspassword) { // Create a
	 * new OpSpec. // This specifies what operation we want to perform on the //
	 * tags that match the specifications above. // In this case, we want to
	 * write to the tag. C1G2Lock opSpec = new C1G2Lock();
	 * 
	 * // Set the OpSpecID to a unique number.
	 * 
	 * opSpec.setOpSpecID(new UnsignedShort(EPCLock_OPSPEC_ID)); ///comes from
	 * private member constant
	 * 
	 * 
	 * //opSpec.setWordPointer(new UnsignedShort(0x02));->only for writing
	 * 
	 * opSpec.AccessPassword = accesspassword; C1G2LockPayload payload = new
	 * C1G2LockPayload(); opSpec.payload.DataField
	 * =C1G2LockDataField.EPC_Memory; opSpec.payload.Privilege =
	 * C1G2LockPrivilege.Read_Write; }
	 * 
	 * 
	 * return opSpec; } public C1G2Lock buildLockAccessPWDOpSpec(String
	 * accesspassword) { // Create a new OpSpec. // This specifies what
	 * operation we want to perform on the // tags that match the specifications
	 * above. // In this case, we want to write to the tag. C1G2Lock opSpec =
	 * new C1G2Lock();
	 * 
	 * // Set the OpSpecID to a unique number.
	 * 
	 * opSpec.setOpSpecID(new UnsignedShort(AccessPWDLock_OPSPEC_ID)); ///comes
	 * from private member constant
	 * 
	 * 
	 * opSpec.setWordPointer(new UnsignedShort(0x02)); opSpec.AccessPassword =
	 * accesspassword; C1G2LockPayload payload = new C1G2LockPayload(); opspec.
	 * payload.DataField = C1G2LockDataField.Access_Password;; opspec.
	 * payload.Privilege = C1G2LockPrivilege.Read_Write; }
	 * 
	 * 
	 * return opSpec; } public C1G2Lock buildLockKillWDOpSpec(String
	 * accesspassword) { // Create a new OpSpec. // This specifies what
	 * operation we want to perform on the // tags that match the specifications
	 * above. // In this case, we want to write to the tag. C1G2Lock opSpec =
	 * new C1G2Lock();
	 * 
	 * // Set the OpSpecID to a unique number.
	 * 
	 * opSpec.setOpSpecID(new UnsignedShort(KillPWDLock_OPSPEC_ID)); ///comes
	 * from private member constant
	 * 
	 * 
	 * opSpec.setWordPointer(new UnsignedShort(0x02)); opSpec.AccessPassword =
	 * accesspassword; C1G2LockPayload payload = new C1G2LockPayload(); opspec.
	 * payload.DataField = C1G2LockDataField.Kill_Password; opspec.
	 * payload.Privilege = C1G2LockPrivilege.Read_Write; }
	 * 
	 * 
	 * return opSpec; } public C1G2Lock buildUnLockEPCOpSpec(String
	 * accesspassword) { // Create a new OpSpec. // This specifies what
	 * operation we want to perform on the // tags that match the specifications
	 * above. // In this case, we want to write to the tag. C1G2Lock opSpec =
	 * new C1G2Lock();
	 * 
	 * // Set the OpSpecID to a unique number.
	 * 
	 * opSpec.setOpSpecID(new UnsignedShort(EPCLock_OPSPEC_ID)); ///comes from
	 * private member constant
	 * 
	 * 
	 * opSpec.setWordPointer(new UnsignedShort(0x02));
	 * 
	 * opSpec.AccessPassword = accesspassword; C1G2LockPayload payload = new
	 * C1G2LockPayload(); opspec.payload.DataField
	 * =C1G2LockDataField.EPC_Memory; opspec.payload.Privilege =
	 * C1G2LockPrivilege.Unlock; }
	 * 
	 * 
	 * return opSpec; } public C1G2Lock buildUnLockAccessPWDOpSpec(String
	 * accesspassword) { // Create a new OpSpec. // This specifies what
	 * operation we want to perform on the // tags that match the specifications
	 * above. // In this case, we want to write to the tag. C1G2Lock opSpec =
	 * new C1G2Lock();
	 * 
	 * // Set the OpSpecID to a unique number.
	 * 
	 * opSpec.setOpSpecID(new UnsignedShort(AccessPWDLock_OPSPEC_ID)); ///comes
	 * from private member constant
	 * 
	 * 
	 * opSpec.setWordPointer(new UnsignedShort(0x02)); opSpec.AccessPassword =
	 * accesspassword; C1G2LockPayload payload = new C1G2LockPayload(); opspec.
	 * payload.DataField = C1G2LockDataField.Access_Password;; opspec.
	 * payload.Privilege = C1G2LockPrivilege.Unlock; }
	 * 
	 * 
	 * return opSpec; } public C1G2Lock buildUnLockKillWDOpSpec(String
	 * accesspassword) { // Create a new OpSpec. // This specifies what
	 * operation we want to perform on the // tags that match the specifications
	 * above. // In this case, we want to write to the tag. C1G2Lock opSpec =
	 * new C1G2Lock();
	 * 
	 * // Set the OpSpecID to a unique number.
	 * 
	 * opSpec.setOpSpecID(new UnsignedShort(KillPWDLock_OPSPEC_ID)); ///comes
	 * from private member constant
	 * 
	 * 
	 * opSpec.setWordPointer(new UnsignedShort(0x02)); opSpec.AccessPassword =
	 * accesspassword; C1G2LockPayload payload = new C1G2LockPayload(); opspec.
	 * payload.DataField = C1G2LockDataField.Kill_Password; opspec.
	 * payload.Privilege = C1G2LockPrivilege.Unlock; }
	 * 
	 * 
	 * return opSpec; }
	 */

}

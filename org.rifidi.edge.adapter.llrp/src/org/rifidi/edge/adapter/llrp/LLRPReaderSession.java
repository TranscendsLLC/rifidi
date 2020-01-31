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
import java.nio.channels.UnresolvedAddressException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
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
import org.llrp.ltk.generated.enumerations.AISpecStopTriggerType;
import org.llrp.ltk.generated.enumerations.AccessReportTriggerType;
import org.llrp.ltk.generated.enumerations.AccessSpecState;
import org.llrp.ltk.generated.enumerations.AccessSpecStopTriggerType;
import org.llrp.ltk.generated.enumerations.AirProtocols;
import org.llrp.ltk.generated.enumerations.C1G2LockDataField;
import org.llrp.ltk.generated.enumerations.C1G2LockPrivilege;
import org.llrp.ltk.generated.enumerations.GetReaderCapabilitiesRequestedData;
import org.llrp.ltk.generated.enumerations.GetReaderConfigRequestedData;
import org.llrp.ltk.generated.enumerations.StatusCode;
import org.llrp.ltk.generated.interfaces.AccessCommandOpSpec;
import org.llrp.ltk.generated.interfaces.AccessCommandOpSpecResult;
import org.llrp.ltk.generated.interfaces.SpecParameter;
import org.llrp.ltk.generated.messages.ADD_ACCESSSPEC;
import org.llrp.ltk.generated.messages.ADD_ACCESSSPEC_RESPONSE;
import org.llrp.ltk.generated.messages.ADD_ROSPEC;
import org.llrp.ltk.generated.messages.DELETE_ACCESSSPEC;
import org.llrp.ltk.generated.messages.ENABLE_ACCESSSPEC;
import org.llrp.ltk.generated.messages.ENABLE_EVENTS_AND_REPORTS;
import org.llrp.ltk.generated.messages.GET_READER_CAPABILITIES;
import org.llrp.ltk.generated.messages.GET_READER_CONFIG;
import org.llrp.ltk.generated.messages.GET_ROSPECS;
import org.llrp.ltk.generated.messages.RO_ACCESS_REPORT;
import org.llrp.ltk.generated.messages.SET_READER_CONFIG;
import org.llrp.ltk.generated.messages.SET_READER_CONFIG_RESPONSE;
import org.llrp.ltk.generated.parameters.AISpec;
import org.llrp.ltk.generated.parameters.AISpecStopTrigger;
import org.llrp.ltk.generated.parameters.AccessCommand;
import org.llrp.ltk.generated.parameters.AccessReportSpec;
import org.llrp.ltk.generated.parameters.AccessSpec;
import org.llrp.ltk.generated.parameters.AccessSpecStopTrigger;
import org.llrp.ltk.generated.parameters.AntennaConfiguration;
import org.llrp.ltk.generated.parameters.C1G2BlockWrite;
import org.llrp.ltk.generated.parameters.C1G2Lock;
import org.llrp.ltk.generated.parameters.C1G2LockPayload;
import org.llrp.ltk.generated.parameters.C1G2Read;
import org.llrp.ltk.generated.parameters.C1G2TagSpec;
import org.llrp.ltk.generated.parameters.C1G2TargetTag;
import org.llrp.ltk.generated.parameters.C1G2Write;
import org.llrp.ltk.generated.parameters.EPCData;
import org.llrp.ltk.generated.parameters.EPC_96;
import org.llrp.ltk.generated.parameters.InventoryParameterSpec;
import org.llrp.ltk.generated.parameters.RFTransmitter;
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

	/** Old access Password (Hex Value) **/
	private String oldAccessPwd;

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
	 * value to perform an user memory lock Operation (values can be Read_Write (which
	 * Locks), Perma_Lock, Unlock)
	 **/
	private int userMemoryLockPrivilege;

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
	 * Default old access password value if there is no property read from jvm.
	 * (Hex Value)
	 **/
	public static final String DEFAULT_OLD_ACCESS_PASSWORD = "0";

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
	
	/**
	 * Default user memory lock privilege if there is no property read from jvm. (values
	 * can be Read_Write (which Locks), Perma_Lock, Unlock)
	 **/
	public static final int DEFAULT_USER_MEMORY_LOCK_PRIVILEGE = C1G2LockPrivilege.Read_Write;
	

	/** Expected value for Read_Write lock privilege taken from jvm **/
	public final static String READ_WRITE_LOCK_PRIVILEGE = "Read_Write";

	/** Expected value for Perma_Lock lock privilege taken from jvm **/
	public final static String PERMA_LOCK_PRIVILEGE = "Perma_Lock";

	/** Expected value for Unlock lock privilege taken from jvm **/
	public final static String UNLOCK_PRIVILEGE = "Unlock";

	private static final int WRITE_EPC_ACCESSSPEC_ID = 101;
	private static final UnsignedShort WRITE_EPC_OPSPEC_ID = new UnsignedShort(102);

	private static final int WRITE_KILLPASS_ACCESSSPEC_ID = 103;
	private static final UnsignedShort WRITE_KILLPASS_OPSPEC_ID = new UnsignedShort(104);

	private static final int WRITE_ACCESSPASS_ACCESSSPEC_ID = 105;
	private static final UnsignedShort WRITE_ACCESSPASS_OPSPEC_ID = new UnsignedShort(106);

	private static final int LOCK_EPC_ACCESSSPEC_ID = 107;
	private static final UnsignedShort LOCK_EPC_OPSPEC_ID = new UnsignedShort(108);

	private static final int LOCK_KILLPASS_ACCESSSPEC_ID = 109;
	private static final UnsignedShort LOCK_KILLPASS_OPSPEC_ID = new UnsignedShort(110);

	private static final int LOCK_ACCESSPASS_ACCESSSPEC_ID = 111;
	private static final UnsignedShort LOCK_ACCESSPASS_OPSPEC_ID = new UnsignedShort(112);
	
	private static final int READ_EPC_ACCESSSPEC_ID = 113;
	private static final UnsignedShort READ_EPC_OPSPEC_ID = new UnsignedShort(114);
	
	private static final int READ_ACCESSPASS_ACCESSSPEC_ID = 115;
	private static final UnsignedShort READ_ACCESSPASS_OPSPEC_ID = new UnsignedShort(116);
	
	private static final int READ_KILLPASS_ACCESSSPEC_ID = 117;
	private static final UnsignedShort READ_KILLPASS_OPSPEC_ID = new UnsignedShort(118);
	
	private static final int READ_USER_MEMORY_ACCESSSPEC_ID = 119;
	private static final UnsignedShort READ_USER_MEMORY_OPSPEC_ID = new UnsignedShort(120);
	
	private static final int WRITE_USER_MEMORY_ACCESSSPEC_ID = 121;
	private static final UnsignedShort WRITE_USER_MEMORY_OPSPEC_ID = new UnsignedShort(122);
	
	private static final int LOCK_USER_MEMORY_ACCESSSPEC_ID = 123;
	private static final UnsignedShort LOCK_USER_MEMORY_OPSPEC_ID = new UnsignedShort(124);
	
	private static final int READ_MEMORY_BANK_ACCESSSPEC_ID = 125;
	private static final UnsignedShort READ_MEMORY_BANK_OPSEC_ID = new UnsignedShort(126);

	/** Operation names **/
	public enum LLRP_OPERATION_CODE {
		LLRPEPCWrite, LLRPEPCRead, LLRPAccessPasswordWrite, LLRPAccessPasswordValidate, 
		LLRPKillPasswordRead, LLRPKillPasswordWrite, LLRPEPCLock, LLRPKillPasswordLock, 
		LLRPAccessPasswordLock, LLRPUserMemoryLock, LLRPUserMemoryRead, LLRPUserMemoryWrite, 
		LLRPTidRead, LLRPMemoryBankRead 
	};

	/** Object to keep track of the operations and responses on each one **/
	private LLRPOperationTracker llrpOperationTracker;

	/**
	 * The block length the tag on this session must satisfy. For example for
	 * EPC it needs to split in blocks of 4: /0A50708041A1B1D1A1B1E238 so we
	 * have blocks like this 0A50 7080 41A1 B1D1 A1B1 E238 to set the data on
	 * tag Default is 4
	 **/
	private int writeDataBlockLength = 4;

	/** Quality of service level for mqtt, example: 2 **/
	private int mqttQos;

	/** mqtt broker url, example: tcp://localhost:1883 **/
	private String mqttBroker;

	/** mqtt client id to publish messages on queue **/
	private String mqttClientId;

	private boolean executeOperationsInAsynchronousMode;
	
	/** default number of blocks for EPC to be read **/
	public static final int DEFAULT_EPC_WORD_COUNT = 6;
	
	/** default number of blocks for user memory to be read **/
	public static final int DEFAULT_USER_MEMORY_WORD_COUNT = 2;
	
	/** number of blocks to be read, for EPC or user memory **/
	private int wordCount;
	/** memblock to read **/
	private int membank1;
	private int membank2;
	
	/** data to be set in user memory **/
	private String userMemoryData;
	
	private Boolean rssioffset;
	
	private Map<Integer, Integer> rssiFilterMap = null;
	private void setRSSIFilterMap(String rssiFilter) {
		try {
			rssiFilterMap = new HashMap<Integer, Integer>();
			String[] pairs = rssiFilter.split("\\|");
			for (String pair : pairs) {
				String[] s = pair.split(":");
				Integer ant = Integer.parseInt(s[0]);
				Integer rssi = Integer.parseInt(s[1]);
				rssiFilterMap.put(ant, rssi);
			}
			if (rssiFilterMap.size() == 0) {
				rssiFilterMap = null;
			} else if (rssiFilterMap.size() == 1) {
				if(rssiFilterMap.containsKey(0) && rssiFilterMap.get(0).equals(0)) {
					rssiFilterMap = null;
				}
			}
		} catch (Exception e) {
			// Any problems and we disable the filter
			rssiFilterMap = null;   
		} 
	}
	
	private Map<Integer, UnsignedShort> transmitPowerMap = null;
	private void setTransmitPowerMap(String transmitPower) {
		if(transmitPower.equals("0:0")) {
			return;
		}
		try {
			transmitPowerMap = new HashMap<Integer, UnsignedShort>();
			String[] pairs = transmitPower.split("\\|");
			for (String pair : pairs) {
				String[] s = pair.split(":");
				Integer ant = Integer.parseInt(s[0]);
				Integer power = Integer.parseInt(s[1]);
				transmitPowerMap.put(ant, new UnsignedShort(power));
			}
			if (transmitPowerMap.size() == 0) {
				transmitPowerMap = null;
			} else if (transmitPowerMap.size() == 1) {
				if(transmitPowerMap.containsKey(0) && transmitPowerMap.get(0).equals(0.0F)) {
					transmitPowerMap = null;
				}
			}
		} catch(Exception e) {
			//Any problems and we disable the power map
			this.transmitPowerMap = null;
		}
	}
	

	/**
	 * @return the userMemoryData
	 */
	public String getUserMemoryData() {
		return userMemoryData;
	}

	/**
	 * @param userMemoryData the userMemoryData to set
	 */
	public void setUserMemoryData(String userMemoryData) {
		this.userMemoryData = userMemoryData;
	}

	/**
	 * @return the wordCount
	 */
	public int getWordCount() {
		return wordCount;
	}

	/**
	 * @param wordCount the wordCount to set
	 */
	public void setWordCount(int wordCount) {
		this.wordCount = wordCount;
	}

	public void setMemBank1(int membank1) {
		this.membank1 = membank1;
	}
	public void setMemBank2(int membank2) {
		this.membank2 = membank2;
	}


	/**
	 * @return the executeOperationsInAsynchronousMode
	 */
	public boolean isExecuteOperationsInAsynchronousMode() {
		return executeOperationsInAsynchronousMode;
	}

	/**
	 * @param executeOperationsInAsynchronousMode
	 *            the executeOperationsInAsynchronousMode to set
	 */
	public void setExecuteOperationsInAsynchronousMode(
			boolean executeOperationsInAsynchronousMode) {
		this.executeOperationsInAsynchronousMode = executeOperationsInAsynchronousMode;
	}

	/**
	 * @return the mqttQos
	 */
	public int getMqttQos() {
		return mqttQos;
	}

	/**
	 * @param mqttQos
	 *            the mqttQos to set
	 */
	public void setMqttQos(int mqttQos) {
		this.mqttQos = mqttQos;
	}

	/**
	 * @return the mqttBroker
	 */
	public String getMqttBroker() {
		return mqttBroker;
	}

	/**
	 * @param mqttBroker
	 *            the mqttBroker to set
	 */
	public void setMqttBroker(String mqttBroker) {
		this.mqttBroker = mqttBroker;
	}

	/**
	 * @return the mqttClientId
	 */
	public String getMqttClientId() {
		return mqttClientId;
	}

	/**
	 * @param mqttClientId
	 *            the mqttClientId to set
	 */
	public void setMqttClientId(String mqttClientId) {
		this.mqttClientId = mqttClientId;
	}

	/**
	 * @return the writeDataBlockLength
	 */
	public int getWriteDataBlockLength() {
		return writeDataBlockLength;
	}

	/**
	 * @param writeDataBlockLength
	 *            the writeDataBlockLength to set
	 */
	public void setWriteDataBlockLength(int writeDataBlockLength) {
		this.writeDataBlockLength = writeDataBlockLength;
	}

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
	 * @return the oldAccessPwd
	 */
	public String getOldAccessPwd() {
		return oldAccessPwd;
	}

	/**
	 * @param oldAccessPwd
	 *            the oldAccessPwd to set
	 */
	public void setOldAccessPwd(String oldAccessPwd) {
		this.oldAccessPwd = oldAccessPwd;
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
	 * @return the userMemoryLockPrivilege
	 */
	public int getUserMemoryLockPrivilege() {
		return userMemoryLockPrivilege;
	}

	/**
	 * @param userMemoryLockPrivilege the userMemoryLockPrivilege to set
	 */
	public void setUserMemoryLockPrivilege(int userMemoryLockPrivilege) {
		this.userMemoryLockPrivilege = userMemoryLockPrivilege;
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
			String readerConfigPath, String rssiFilter, String transmitPower, NotifierService notifierService,
			String readerID, Boolean rssioffset, Set<AbstractCommandConfiguration<?>> commands) {
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
		this.setRSSIFilterMap(rssiFilter);
		this.rssioffset = rssioffset;
		this.setTransmitPowerMap(transmitPower);
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
					logger.warn("Attempt to connect to LLRP reader failed: " + connCount);
				} catch (RuntimeIOException e) {
					// fix for abandoned connection
					try {
						((LLRPConnector) connection).disconnect();
					} catch (Exception ex) {
					}
					logger.warn("Attempt to connect to LLRP reader failed: " + connCount);
				} catch(UnresolvedAddressException e) {
					try {
						((LLRPConnector) connection).disconnect();
					} catch (Exception ex) {
					}
					logger.warn("Attempt to connect to reader " + this.readerID + " failed: " + e.getMessage() + ", count " + connCount);
				} catch(Exception e) {
					
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

			SET_READER_CONFIG_RESPONSE config_response = (SET_READER_CONFIG_RESPONSE) connection.transact(config);
			
			ENABLE_EVENTS_AND_REPORTS report_enable = new ENABLE_EVENTS_AND_REPORTS();
			connection.send(report_enable);

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
		if (processing.get()) {
			try {
				resetCommands();
				this.submitAndBlock(getResetCommand(), getTimeout(), TimeUnit.MILLISECONDS);
				// if already connected, disconnect
				if (processing.compareAndSet(true, false)) {
					logger.debug("Disconnecting");
					((LLRPConnector) connection).disconnect();
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			} finally {
				this.timingOut.set(false);
				// make sure executor is shutdown!
				if (executor != null) {
					executor.shutdownNow();
					executor = null;
				}
				// notify anyone who cares that session is now closed
				setStatus(SessionStatus.CLOSED);
			}
		} else {
			try {
				// Try to stop connecting
				if (connectingLoop.getAndSet(false)) {
					synchronized (connectingLoop) {
						connectingLoop.notify();
					}
				}
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
		message = this.transmitPowerIntecept(message);
		synchronized (connection) {
			try {
				if (timingOut.get()) {
					throw new IllegalStateException("Cannot execute while timing out: " + message.getName());
				}
				LLRPMessage response = this.connection.transact(message, 20000);
				LLRPExceptionHandler handler = new LLRPExceptionHandler(this.readerID, this.sensor);
				handler.checkForErrors(response);
				return response;
			} catch (TimeoutException e) {
				timingOut.set(true);
				logger.error("Timeout when sending an LLRP Message: " + message.getName());
				// Disconnect and possibly reconnect
				logger.info("Attempting to reconnect to reader " + this.readerID + " after a timeout");
				this.disconnect();
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
			message = transmitPowerIntecept(message);
			connection.send(message);
		}
	}
	
	public LLRPMessage transmitPowerIntecept(LLRPMessage message) {		
		//If the message insn't an ADD_ROSPEC or there isn't a transmit power to set
		if(!(message instanceof ADD_ROSPEC) || this.transmitPowerMap==null || this.transmitPowerMap.isEmpty()) {
			return message;
		}
		
		//Otherwise, replace whatever was in the ADD_Rospec antenna section with what is in the transmit power map
		ADD_ROSPEC add_rospec = (ADD_ROSPEC) message;
		//Is there an existing AISPec
		AISpec aispec = null;
		for(SpecParameter spec:add_rospec.getROSpec().getSpecParameterList()) {
			if(spec instanceof AISpec) {
				aispec = (AISpec)spec;
			}
		}
		//If there isn't, create one and add it to the spec paramter list
		if(aispec == null) {
			aispec = new AISpec();
			AISpecStopTrigger trigger = new AISpecStopTrigger();
			trigger.setAISpecStopTriggerType(new AISpecStopTriggerType(AISpecStopTriggerType.Null));
			aispec.setAISpecStopTrigger(trigger);
			add_rospec.getROSpec().addToSpecParameterList(aispec);
		}
		
		//Check and see if there are any InventoryParamterSpec
		if(aispec.getInventoryParameterSpecList().isEmpty()) {
			InventoryParameterSpec inv = new InventoryParameterSpec();
			inv.setInventoryParameterSpecID(new UnsignedShort(1));
			inv.setProtocolID(new AirProtocols(AirProtocols.EPCGlobalClass1Gen2));
		}
		
		//Get the first spec in the list -- this should be safe as we created one if it didn't already exist
		InventoryParameterSpec inv = aispec.getInventoryParameterSpecList().get(0);
		
		HashSet<Integer> exists = new HashSet<Integer>();
		//Loop through any existing antennas and set the TransmitPower
		for(AntennaConfiguration ant:inv.getAntennaConfigurationList()) {
			if(transmitPowerMap.containsKey(ant.getAntennaID().intValue())) {
				ant.getRFTransmitter().setTransmitPower(transmitPowerMap.get(ant.getAntennaID().intValue()));
			}
		}
		
		//Loop through any antennas that don't exist yet and create and add AntennaConfigurations for them
		for(Integer key:this.transmitPowerMap.keySet()) {
			//If the key doesn't exist
			if(!exists.contains(key)) {
				AntennaConfiguration ant = new AntennaConfiguration();
				ant.setAntennaID(new UnsignedShort(key));
				RFTransmitter transmitter = new RFTransmitter();
				transmitter.setTransmitPower(transmitPowerMap.get(key));
				transmitter.setChannelIndex(new UnsignedShort(1));
				transmitter.setHopTableID(new UnsignedShort(1));
				ant.setRFTransmitter(transmitter);
				inv.addToAntennaConfigurationList(ant);
			}
		}

		try {
			System.out.println(add_rospec.toXMLString());
		} catch (InvalidLLRPMessageException e) {
			e.printStackTrace();
		}
		
		return add_rospec;
	}

	/**
	 * 
	 */
	@Override
	public void errorOccured(String arg0) {
		if(!arg0.contains("IllegalStateException")) {
			logger.error("LLRP Error Occurred: " + arg0);
		}
		//TODO: Should we disconnect
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
			logger.warn("No SET_READER_CONFIG message found at " + readerConfigPath + " Using default SET_READER_CONFIG");
		}

		return LLRPConstants.createDefaultConfig();
	}

	
	/**
	 * Gets the reader config and returns the xml representation.  
	 * 
	 * @return
	 * @throws TimeoutException 
	 * @throws InvalidLLRPMessageException 
	 */
	public String getReaderConfig() throws InvalidLLRPMessageException, TimeoutException {
		GET_READER_CONFIG grc = new GET_READER_CONFIG();
		grc.setAntennaID(new UnsignedShort(0));
		GetReaderConfigRequestedData data = new GetReaderConfigRequestedData();
		data.set(0);
		grc.setRequestedData(data);
		grc.setGPIPortNum(new UnsignedShort(0));
		grc.setGPOPortNum(new UnsignedShort(0));		
		String retVal = this.transact(grc).toXMLString();
		return retVal;
	}
	
	/**
	 * Gets the reader config and returns the xml representation.  
	 * 
	 * @return
	 * @throws TimeoutException 
	 * @throws InvalidLLRPMessageException 
	 */
	public String getReaderCapabilities() throws InvalidLLRPMessageException, TimeoutException {
		GET_READER_CAPABILITIES grc = new GET_READER_CAPABILITIES();
		GetReaderCapabilitiesRequestedData data = new GetReaderCapabilitiesRequestedData();
		data.set(0);
		grc.setRequestedData(data);
		String retVal = this.transact(grc).toXMLString();
		return retVal;
	}
	
	/**
	 * 
	 * 
	 * @param rospecID
	 * @return
	 * @throws TimeoutException 
	 * @throws InvalidLLRPMessageException 
	 */
	public String getRospecs() throws InvalidLLRPMessageException, TimeoutException {
		GET_ROSPECS gr = new GET_ROSPECS();
		String retVal = this.transact(gr).toXMLString();
		return retVal;
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

	private ADD_ACCESSSPEC_RESPONSE executeLLRPOperation(int accessSpecId,
			String epcId) throws InvalidLLRPMessageException, TimeoutException {

		// try {
		logger.info("Add accessspec! " + accessSpecId);
		// //// int accessSpecId = 2;
		AccessSpec spec = buildAccessSpec(accessSpecId, epcId);
		ADD_ACCESSSPEC aas = new ADD_ACCESSSPEC();
		aas.setAccessSpec(spec);
		logger.info(aas.toXMLString());
		ADD_ACCESSSPEC_RESPONSE response = null;

		response = (ADD_ACCESSSPEC_RESPONSE) this.transact(aas);
		logger.info("Response__: " + response.toXMLString());

		ENABLE_ACCESSSPEC enablespec = new ENABLE_ACCESSSPEC();
		enablespec.setAccessSpecID(new UnsignedInteger(accessSpecId));

		this.send(enablespec);

		return response;
		/*
		 * } catch (Exception e) { e.printStackTrace(); return e.getMessage(); }
		 */
	}

	// Execute only this operation:
	public LLRPEncodeMessageDto llrpWriteEpcOperation(String epc)
			throws InvalidLLRPMessageException, TimeoutException, Exception {

		// Initialize operation tracker, to be able to receive asynchronous
		// messages
		llrpOperationTracker = new LLRPOperationTracker(this);

		// Add the operations in the same order we want to be executed by this
		// tracker

		LLRPOperationDto epcWriteOpDto = new LLRPOperationDto(
				LLRP_OPERATION_CODE.LLRPEPCWrite.name(),
				WRITE_EPC_ACCESSSPEC_ID, WRITE_EPC_OPSPEC_ID, epc);
		llrpOperationTracker.addOperationDto(epcWriteOpDto);

		return startRequestedOperations(llrpOperationTracker);

	}
	
	// Execute only this operation EPC Read:
	//public LLRPEncodeMessageDto llrpReadEpcOperation(String epc)
	public LLRPEncodeMessageDto llrpReadEpcOperation()
			throws InvalidLLRPMessageException, TimeoutException, Exception {

		// Initialize operation tracker, to be able to receive asynchronous
		// messages
		llrpOperationTracker = new LLRPOperationTracker(this);

		// Add the operations in the same order we want to be executed by this
		// tracker

		LLRPOperationDto epcReadOpDto = new LLRPOperationDto(
				LLRP_OPERATION_CODE.LLRPEPCRead.name(),
				READ_EPC_ACCESSSPEC_ID, READ_EPC_OPSPEC_ID, null);
		llrpOperationTracker.addOperationDto(epcReadOpDto);

		return startRequestedOperations(llrpOperationTracker);

	}

	// TODO to execute only this operation:
	public LLRPEncodeMessageDto llrpWriteAccessPasswordOperation()
			throws InvalidLLRPMessageException, TimeoutException, Exception {

		// Initialize operation tracker, to be able to receive asynchronous
		// messages
		llrpOperationTracker = new LLRPOperationTracker(this);

		// Add the operations in the same order we want to be executed by this
		// tracker

		LLRPOperationDto epcWriteOpDto = new LLRPOperationDto(
				LLRP_OPERATION_CODE.LLRPAccessPasswordWrite.name(),
				WRITE_ACCESSPASS_ACCESSSPEC_ID, WRITE_ACCESSPASS_OPSPEC_ID,
				null);
		llrpOperationTracker.addOperationDto(epcWriteOpDto);

		return startRequestedOperations(llrpOperationTracker);

	}
	
	public LLRPEncodeMessageDto llrpReadAccessPasswordOperation()
			throws InvalidLLRPMessageException, TimeoutException, Exception {

		// Initialize operation tracker, to be able to receive asynchronous
		// messages
		llrpOperationTracker = new LLRPOperationTracker(this);

		// Add the operations in the same order we want to be executed by this
		// tracker

		LLRPOperationDto accessPwdReadDto = new LLRPOperationDto(
				LLRP_OPERATION_CODE.LLRPAccessPasswordValidate.name(),
				READ_ACCESSPASS_ACCESSSPEC_ID, READ_ACCESSPASS_OPSPEC_ID,
				null);
		llrpOperationTracker.addOperationDto(accessPwdReadDto);

		return startRequestedOperations(llrpOperationTracker);

	}
	
	public LLRPEncodeMessageDto llrpReadKillPasswordOperation()
			throws InvalidLLRPMessageException, TimeoutException, Exception {

		// Initialize operation tracker, to be able to receive asynchronous
		// messages
		llrpOperationTracker = new LLRPOperationTracker(this);

		// Add the operations in the same order we want to be executed by this
		// tracker

		LLRPOperationDto killPwdReadDto = new LLRPOperationDto(
				LLRP_OPERATION_CODE.LLRPKillPasswordRead.name(),
				READ_KILLPASS_ACCESSSPEC_ID, READ_KILLPASS_OPSPEC_ID,
				null);
		llrpOperationTracker.addOperationDto(killPwdReadDto);

		return startRequestedOperations(llrpOperationTracker);

	}
	
	public LLRPEncodeMessageDto llrpWriteKillPasswordOperation()
			throws InvalidLLRPMessageException, TimeoutException, Exception {

		// Initialize operation tracker, to be able to receive asynchronous
		// messages
		llrpOperationTracker = new LLRPOperationTracker(this);

		// Add the operations in the same order we want to be executed by this
		// tracker

		LLRPOperationDto epcWriteOpDto = new LLRPOperationDto(
				LLRP_OPERATION_CODE.LLRPKillPasswordWrite.name(),
				WRITE_KILLPASS_ACCESSSPEC_ID, WRITE_KILLPASS_OPSPEC_ID,
				null);
		llrpOperationTracker.addOperationDto(epcWriteOpDto);

		return startRequestedOperations(llrpOperationTracker);

	}
	
	public LLRPEncodeMessageDto llrpReadUserMemoryOperation()
			throws InvalidLLRPMessageException, TimeoutException, Exception {

		// Initialize operation tracker, to be able to receive asynchronous
		// messages
		llrpOperationTracker = new LLRPOperationTracker(this);

		// Add the operations in the same order we want to be executed by this
		// tracker

		LLRPOperationDto epcReadOpDto = new LLRPOperationDto(
				LLRP_OPERATION_CODE.LLRPUserMemoryRead.name(),
				READ_USER_MEMORY_ACCESSSPEC_ID, READ_USER_MEMORY_OPSPEC_ID,
				null);
		llrpOperationTracker.addOperationDto(epcReadOpDto);

		return startRequestedOperations(llrpOperationTracker);

	}
	
	public LLRPEncodeMessageDto llrpReadMemoryBankOperation()
			throws InvalidLLRPMessageException, TimeoutException, Exception {

		// Initialize operation tracker, to be able to receive asynchronous
		// messages
		llrpOperationTracker = new LLRPOperationTracker(this);

		// Add the operations in the same order we want to be executed by this
		// tracker

		LLRPOperationDto epcReadOpDto = new LLRPOperationDto(
				LLRP_OPERATION_CODE.LLRPMemoryBankRead.name(),
				READ_MEMORY_BANK_ACCESSSPEC_ID, READ_MEMORY_BANK_OPSEC_ID,
				null);
		llrpOperationTracker.addOperationDto(epcReadOpDto);

		return startRequestedOperations(llrpOperationTracker);

	}
	
	public LLRPEncodeMessageDto llrpWriteUserMemoryOperation()
			throws InvalidLLRPMessageException, TimeoutException, Exception {

		// Initialize operation tracker, to be able to receive asynchronous
		// messages
		llrpOperationTracker = new LLRPOperationTracker(this);

		// Add the operations in the same order we want to be executed by this
		// tracker

		LLRPOperationDto epcWriteOpDto = new LLRPOperationDto(
				LLRP_OPERATION_CODE.LLRPUserMemoryWrite.name(),
				WRITE_USER_MEMORY_ACCESSSPEC_ID, WRITE_USER_MEMORY_OPSPEC_ID,
				null);
		llrpOperationTracker.addOperationDto(epcWriteOpDto);

		return startRequestedOperations(llrpOperationTracker);

	}
	
	public LLRPEncodeMessageDto llrpLockEpcOperation()
			throws InvalidLLRPMessageException, TimeoutException, Exception {

		// Initialize operation tracker, to be able to receive asynchronous
		// messages
		llrpOperationTracker = new LLRPOperationTracker(this);

		// Add the operations in the same order we want to be executed by this
		// tracker

		LLRPOperationDto epcLockOpDto = new LLRPOperationDto(
				LLRP_OPERATION_CODE.LLRPEPCLock.name(),
				LOCK_EPC_ACCESSSPEC_ID, LOCK_EPC_OPSPEC_ID,
				null);
		llrpOperationTracker.addOperationDto(epcLockOpDto);

		return startRequestedOperations(llrpOperationTracker);

	}
	
	public LLRPEncodeMessageDto llrpLockAccessPasswordOperation()
			throws InvalidLLRPMessageException, TimeoutException, Exception {

		// Initialize operation tracker, to be able to receive asynchronous
		// messages
		llrpOperationTracker = new LLRPOperationTracker(this);

		// Add the operations in the same order we want to be executed by this
		// tracker

		LLRPOperationDto epcLockOpDto = new LLRPOperationDto(
				LLRP_OPERATION_CODE.LLRPAccessPasswordLock.name(),
				LOCK_ACCESSPASS_ACCESSSPEC_ID, LOCK_ACCESSPASS_OPSPEC_ID,
				null);
		llrpOperationTracker.addOperationDto(epcLockOpDto);

		return startRequestedOperations(llrpOperationTracker);

	}
	
	public LLRPEncodeMessageDto llrpLockKillPasswordOperation()
			throws InvalidLLRPMessageException, TimeoutException, Exception {

		// Initialize operation tracker, to be able to receive asynchronous
		// messages
		llrpOperationTracker = new LLRPOperationTracker(this);

		// Add the operations in the same order we want to be executed by this
		// tracker

		LLRPOperationDto epcLockOpDto = new LLRPOperationDto(
				LLRP_OPERATION_CODE.LLRPKillPasswordLock.name(),
				LOCK_KILLPASS_ACCESSSPEC_ID, LOCK_KILLPASS_OPSPEC_ID,
				null);
		llrpOperationTracker.addOperationDto(epcLockOpDto);

		return startRequestedOperations(llrpOperationTracker);

	}
	
	public LLRPEncodeMessageDto llrpLockUserMemoryOperation()
			throws InvalidLLRPMessageException, TimeoutException, Exception {

		// Initialize operation tracker, to be able to receive asynchronous
		// messages
		llrpOperationTracker = new LLRPOperationTracker(this);

		// Add the operations in the same order we want to be executed by this
		// tracker

		LLRPOperationDto userMemoryLockOpDto = new LLRPOperationDto(
				LLRP_OPERATION_CODE.LLRPUserMemoryLock.name(),
				LOCK_USER_MEMORY_ACCESSSPEC_ID, LOCK_USER_MEMORY_OPSPEC_ID,
				null);
		llrpOperationTracker.addOperationDto(userMemoryLockOpDto);

		return startRequestedOperations(llrpOperationTracker);

	}

	// public String addAccessSpec(String writeAccessPassword, String writeData
	// ) {
	public LLRPEncodeMessageDto llrpEncode(String strTag)
			throws TimeoutException, InvalidLLRPMessageException, Exception {

		// Initialize operation tracker, to be able to receive asynchronous
		// messages
		llrpOperationTracker = new LLRPOperationTracker(this);

		// Add the operations in the same order we want to be executed by this
		// tracker

		LLRPOperationDto accessPwdWriteOpDto = new LLRPOperationDto(
				LLRP_OPERATION_CODE.LLRPAccessPasswordWrite.name(),
				WRITE_ACCESSPASS_ACCESSSPEC_ID, WRITE_ACCESSPASS_OPSPEC_ID,
				strTag);
		llrpOperationTracker.addOperationDto(accessPwdWriteOpDto);

		LLRPOperationDto epcWriteOpDto = new LLRPOperationDto(
				LLRP_OPERATION_CODE.LLRPEPCWrite.name(),
				WRITE_EPC_ACCESSSPEC_ID, WRITE_EPC_OPSPEC_ID, strTag);
		llrpOperationTracker.addOperationDto(epcWriteOpDto);
		
		LLRPOperationDto killPwdWriteOpDto = new LLRPOperationDto(
				LLRP_OPERATION_CODE.LLRPKillPasswordWrite.name(),
				WRITE_KILLPASS_ACCESSSPEC_ID, WRITE_KILLPASS_OPSPEC_ID, strTag);
		llrpOperationTracker.addOperationDto(killPwdWriteOpDto);
	
		LLRPOperationDto epcLockOpDto = new LLRPOperationDto(
				LLRP_OPERATION_CODE.LLRPEPCLock.name(), LOCK_EPC_ACCESSSPEC_ID,
				LOCK_EPC_OPSPEC_ID, strTag);
		llrpOperationTracker.addOperationDto(epcLockOpDto);

		LLRPOperationDto killPwdLockOpDto = new LLRPOperationDto(
				LLRP_OPERATION_CODE.LLRPKillPasswordLock.name(),
				LOCK_KILLPASS_ACCESSSPEC_ID, LOCK_KILLPASS_OPSPEC_ID, strTag);
		llrpOperationTracker.addOperationDto(killPwdLockOpDto);

		LLRPOperationDto accessPwdLockOpDto = new LLRPOperationDto(
				LLRP_OPERATION_CODE.LLRPAccessPasswordLock.name(),
				LOCK_ACCESSPASS_ACCESSSPEC_ID, LOCK_ACCESSPASS_OPSPEC_ID,
				strTag);
		llrpOperationTracker.addOperationDto(accessPwdLockOpDto);

		return startRequestedOperations(llrpOperationTracker);

	}

	/**
	 * 
	 * @param llrpOperationTracker
	 * @param isSynchronous
	 *            if operations are going to be executed in synchronous mode,
	 *            otherwise they execute in asynchronous mode
	 * @throws TimeoutException
	 * @throws InvalidLLRPMessageException
	 */
	private LLRPEncodeMessageDto startRequestedOperations(
			LLRPOperationTracker llrpOperationTracker) throws TimeoutException,
			InvalidLLRPMessageException, Exception {

		// Start the requested operations
		setRunningLLRPEncoding(true);

		// Variable to return asynchronous response
		LLRPEncodeMessageDto llrpEncodeMessageDto = new LLRPEncodeMessageDto();

		// Call the operations in tracker
		for (LLRPOperationDto llrpOperationDto : llrpOperationTracker
				.getOperationList()) {

			ADD_ACCESSSPEC_RESPONSE response = executeLLRPOperation(
					llrpOperationDto.getAccessSpecId(),
					llrpOperationDto.getEpc());

			llrpOperationDto.setAccessSpecResponse(response);

			// Check if response is failure, and return synchronous message
			if (response.getLLRPStatus().getStatusCode().intValue() != StatusCode.M_Success) {
				throw new Exception(response.toXMLString());
			}

		}

		if (isExecuteOperationsInAsynchronousMode()) {
			
			llrpOperationTracker.initializeMqttParameters();

			// Check the operation status in asynchronous mode
			// Add a timer to control
			Timer timer = new Timer(true);
			timer.schedule(llrpOperationTracker, new Date());
			logger.info("TimerTask begins! :" + new Date());
			
			//TODO change Success hard coded value
			llrpEncodeMessageDto.setStatus("Success");
			
		} else {

			// Check the operation status in synchronous mode
			llrpEncodeMessageDto = llrpOperationTracker.checkOperationStatus();

		}

		return llrpEncodeMessageDto;
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

	public String sendLLRPMessage(Document xmlMessage, boolean sendonly) {
		try {
			LLRPMessage message = LLRPMessageFactory.createLLRPMessage(xmlMessage);
			LLRPMessage response = null;
			try {
				if(!sendonly) {
					response = this.transact(message);
					return response.toXMLString();
				} else {
					this.send(message);
					return null;
				}
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
		LLRPExceptionHandler handler = new LLRPExceptionHandler(this.readerID, this.sensor);
		try {
			handler.checkForErrors(arg0);
			if(logger.isDebugEnabled()) {
				logger.debug(arg0.getResponseType());
				logger.debug(arg0.toXMLString());
			}
		} catch (InvalidLLRPMessageException e) {
			e.printStackTrace();
		}
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
				List<AccessCommandOpSpecResult> ops = tag.getAccessCommandOpSpecResultList();
				// See if any operations were performed on
				// this tag (read, write, kill).
				// If so, print out the details.

				if (this.numTagsChecker) {
					if (this.numTagsSet != null) {
						if (tag.getEPCParameter() instanceof EPC_96) {
							EPC_96 id = (EPC_96) tag.getEPCParameter();
							this.numTagsSet.add(id.getEPC().toString());
						} else {
							EPCData id = (EPCData) tag.getEPCParameter();
							this.numTagsSet.add(id.getEPC().toString());
						}
					}
				}

				for (AccessCommandOpSpecResult op : ops) {

					// Assign the result to appropriate operation in tracker
					llrpOperationTracker.setResult(op);
				
					logger.info(op.toString());

				}
				
				// Delete access spec if there is a CommandOpSpecResult
				// coming back.
				//this.deleteAccessSpecs();
			}
		}

		if (!processing.get()) {
			return;
		}
		try {
			Object event = LLRPEventFactory.createEvent(arg0, readerID, rssiFilterMap, rssioffset);
			if (event != null) {
				if (event instanceof ReadCycle) {
					ReadCycle cycle = (ReadCycle) event;
					sensor.send(cycle);
				} else {
					sensor.sendEvent(event);
				}
			}

		} catch (Exception e) {
			logger.error("Exception while parsing message: " + e.getMessage());
		}

		/*
		 * // Timeout check: long millisToSleep = 500; int numberOfLoops = (int)
		 * (getTimeout() / millisToSleep);
		 * 
		 * for (int i = 0; i < numberOfLoops; i++) {
		 * 
		 * // Check if all messages received, if so and all succeed return //
		 * success if (llrpOperationTracker.areAllResultsReceived()) {
		 * 
		 * break;
		 * 
		 * } else {
		 * 
		 * try {
		 * 
		 * // Sleep the time out Thread.sleep(millisToSleep);
		 * 
		 * } catch (InterruptedException e) {
		 * 
		 * // No matters } }
		 * 
		 * }
		 * 
		 * if (llrpOperationTracker.areAllResultsSuccessful()) {
		 * 
		 * // TODO post to queue a single success message
		 * System.out.println("allResultsSuccessful()");
		 * 
		 * } else {
		 * 
		 * System.out.println("NOT allResultsSuccessful()"); // TODO post to
		 * queue a fail message }
		 * 
		 * //Free session
		 */
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

	private boolean numTagsChecker = false;
	private HashSet<String> numTagsSet;

	public Integer numTagsOnLLRP() {
		this.numTagsChecker = true;
		numTagsSet = new HashSet<String>();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.numTagsChecker = false;
		return numTagsSet.size();
	}

	/*
	 * public enum LLRPWriteOperationEnum { WRITE_EPC, WRITE_ACCESSPASS,
	 * WRITE_KILLPASS, LOCK_EPC, LOCK_KILL_PASSWORD, LOCK_ACCESS_PASSWORD }
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
		logger.info("Enabling the AccessSpec.");
		ENABLE_ACCESSSPEC enable = new ENABLE_ACCESSSPEC();
		enable.setAccessSpecID(new UnsignedInteger(accessSpecID));
		try {
			// response = (ENABLE_ACCESSSPEC_RESPONSE) this.transact(enable,
			// TIMEOUT_MS);
			// System.out.println(response.toXMLString());
		} catch (Exception e) {
			logger.info("Error enabling AccessSpec.");
			e.printStackTrace();
		}
	}

	// Delete all AccessSpecs from the reader
	public void deleteAccessSpecs() {

		logger.info("Deleting all AccessSpecs.");
		DELETE_ACCESSSPEC del = new DELETE_ACCESSSPEC();
		// Use zero as the ROSpec ID.
		// This means delete all AccessSpecs.
		del.setAccessSpecID(new UnsignedInteger(0));
		try {
			// response = (DELETE_ACCESSSPEC_RESPONSE) reader.transact(del,
			// TIMEOUT_MS);
			// System.out.println(response.toXMLString());
		} catch (Exception e) {
			logger.info("Error deleting AccessSpec.");
			e.printStackTrace();
		}
	}

	/*
	 * // Create a OpSpec that reads from user memory public C1G2Read
	 * buildReadOpSpec() { // Create a new OpSpec. // This specifies what
	 * operation we want to perform on the // tags that match the specifications
	 * above. // In this case, we want to read from the tag. C1G2Read opSpec =
	 * new C1G2Read(); // Set the OpSpecID to a unique number.
	 * opSpec.setOpSpecID(new UnsignedShort(READ_OPSPEC_ID));
	 * opSpec.setAccessPassword(new UnsignedInteger(0)); // For this demo, we'll
	 * read from user memory (bank 3). TwoBitField opMemBank = new
	 * TwoBitField(); // Set bits 0 and 1 (bank 3 in binary). opMemBank.set(0);
	 * opMemBank.set(1); opSpec.setMB(opMemBank); // We'll read from the base of
	 * this memory bank (0x00). opSpec.setWordPointer(new UnsignedShort(0x00));
	 * // Read two words. opSpec.setWordCount(new UnsignedShort(2));
	 * 
	 * return opSpec; }
	 */

	// Create an AccessSpec.
	// It will contain our two OpSpecs (read and write).
	public AccessSpec buildAccessSpec(int accessSpecID, String epcId) {
		// LLRPWriteOperationEnum writeOperation) {

		logger.info("Building the AccessSpec.");

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

		if (accessSpecID == WRITE_EPC_ACCESSSPEC_ID) {

			opSpecList.add(buildEpcWriteOpSpec(epcId));

		} else if (accessSpecID == WRITE_KILLPASS_ACCESSSPEC_ID) {

			opSpecList.add(buildEpcWriteKillPass());

		} else if (accessSpecID == WRITE_ACCESSPASS_ACCESSSPEC_ID) {

			opSpecList.add(buildEpcWriteAccessPass());

		} else if (accessSpecID == LOCK_ACCESSPASS_ACCESSSPEC_ID) {

			opSpecList.add(buildLockOpSpec(LOCK_ACCESSPASS_OPSPEC_ID,
					getAccessPwdLockPrivilege(),
					C1G2LockDataField.Access_Password));

		} else if (accessSpecID == LOCK_KILLPASS_ACCESSSPEC_ID) {

			opSpecList
					.add(buildLockOpSpec(LOCK_KILLPASS_OPSPEC_ID,
							getKillPwdLockPrivilege(),
							C1G2LockDataField.Kill_Password));

		} else if (accessSpecID == LOCK_EPC_ACCESSSPEC_ID) {

			opSpecList.add(buildLockOpSpec(LOCK_EPC_OPSPEC_ID,
					getEpcLockPrivilege(), C1G2LockDataField.EPC_Memory));

		} else if (accessSpecID == READ_EPC_ACCESSSPEC_ID) {

			opSpecList.add(buildEpcReadOpSpec());

		} else if (accessSpecID == READ_KILLPASS_ACCESSSPEC_ID) {

			opSpecList.add(buildKillPassRead());

		} else if (accessSpecID == READ_ACCESSPASS_ACCESSSPEC_ID) {

			opSpecList.add(buildAccessPassRead());

		} else if (accessSpecID == READ_USER_MEMORY_ACCESSSPEC_ID) {

			opSpecList.add(buildUserMemoryReadOpSpec());

		} else if (accessSpecID == READ_MEMORY_BANK_ACCESSSPEC_ID) {
			opSpecList.add(buildMemBankReadOpSpec());
		} else if (accessSpecID == WRITE_USER_MEMORY_ACCESSSPEC_ID) {

			opSpecList.add(buildUserMemoryWriteOpSpec());

		}  else if (accessSpecID == LOCK_USER_MEMORY_ACCESSSPEC_ID) {

			opSpecList.add(buildLockOpSpec(LOCK_USER_MEMORY_OPSPEC_ID,
					getUserMemoryLockPrivilege(),
					C1G2LockDataField.User_Memory));

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
		opSpec.setOpSpecID(WRITE_EPC_OPSPEC_ID);

		// opSpec.setAccessPassword(new UnsignedInteger(getAccessPwd()));
		UnsignedInteger unsignedIntAccesPass = new UnsignedInteger(
				getAccessPwd(), 16);
		
		opSpec.setAccessPassword(unsignedIntAccesPass);

		// For this demo, we'll write to user memory (bank 3).
		TwoBitField opMemBank = new TwoBitField();

		// Set bit 1 (bank 2 in binary).
		opMemBank.set(1);
		opMemBank.clear(0);

		opSpec.setMB(opMemBank);

		opSpec.setWordPointer(new UnsignedShort(0x02));

		UnsignedShortArray_HEX writeArray = getFormatedWriteArrayData(epcId);

		opSpec.setWriteData(writeArray);

		return opSpec;
	}

	// Create a OpSpec that writes the access password
	// For a new tag, an old password is always zero
	public C1G2BlockWrite buildEpcWriteAccessPass() {
		// Create a new OpSpec.
		// This specifies what operation we want to perform on the
		// tags that match the specifications above.
		// In this case, we want to write to the tag.
		C1G2BlockWrite opSpec = new C1G2BlockWrite();
		// Set the OpSpecID to a unique number.
		opSpec.setOpSpecID(WRITE_ACCESSPASS_OPSPEC_ID);
		
		UnsignedInteger unsignedIntAccesPass = new UnsignedInteger(
				getOldAccessPwd(), 16);
		
		opSpec.setAccessPassword(unsignedIntAccesPass);
		
		// For this demo, we'll write to user memory (bank 3).
		TwoBitField opMemBank = new TwoBitField();
		// Clear twobits (bank 0 in binary).
		opMemBank.clear(1);
		opMemBank.clear(0);

		opSpec.setMB(opMemBank);
		opSpec.setWordPointer(new UnsignedShort(2));

		UnsignedShortArray_HEX writeArray = getFormatedWriteArrayData(getAccessPwd());

		opSpec.setWriteData(writeArray);

		return opSpec;
	}

	// Create a OpSpec that writes the kill password
	public C1G2BlockWrite buildEpcWriteKillPass() {
		// Create a new OpSpec.
		// This specifies what operation we want to perform on the
		// tags that match the specifications above.
		// In this case, we want to write to the tag.
		C1G2BlockWrite opSpec = new C1G2BlockWrite();
		// Set the OpSpecID to a unique number.
		opSpec.setOpSpecID(WRITE_KILLPASS_OPSPEC_ID);
		
		UnsignedInteger unsignedIntAccesPass = new UnsignedInteger(
				getAccessPwd(), 16);
		
		opSpec.setAccessPassword(unsignedIntAccesPass);

		// For this demo, we'll write to user memory (bank 3).
		TwoBitField opMemBank = new TwoBitField();
		// Clear twobits (bank 0 in binary).
		opMemBank.clear(1);
		opMemBank.clear(0);

		opSpec.setMB(opMemBank);
		opSpec.setWordPointer(new UnsignedShort(0));

		UnsignedShortArray_HEX writeArray = getFormatedWriteArrayData(getKillPwd());

		opSpec.setWriteData(writeArray);

		return opSpec;
	}

	private UnsignedShortArray_HEX getFormatedWriteArrayData(String data) {

		UnsignedShortArray_HEX writeArray = new UnsignedShortArray_HEX();

		// It is required that the remainder of data.length() /
		// getWriteDataBlockLength() is zero
		if (data.length() % getWriteDataBlockLength() == 0) {

			int numberOfBlocks = data.length() / getWriteDataBlockLength();

			for (int i = 0; i < numberOfBlocks; i++) {

				int currentIndex = i * getWriteDataBlockLength();
				String word = data.substring(currentIndex, currentIndex
						+ getWriteDataBlockLength());
				logger.info("Adding short: " + word);
				writeArray.add(new UnsignedShort(word, 16));

			}
		} else if (data.length() == 1) {

			// The data could be a value of zero
			writeArray.add(new UnsignedShort(data, 16));

		} else {

			throw new RuntimeException(
					"The value "
							+ data
							+ " does not satisfy data.length() % getWriteDataBlockLength() == 0");

		}

		return writeArray;
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
		
		Exception e = new Exception("Invalid lock privilege value: "
				+ lockPrivilege + ". Valid ones are: "
				+ LLRPReaderSession.READ_WRITE_LOCK_PRIVILEGE + ", "
				+ LLRPReaderSession.PERMA_LOCK_PRIVILEGE + ", "
				+ LLRPReaderSession.UNLOCK_PRIVILEGE);
		
		if (lockPrivilege == null || lockPrivilege.isEmpty()){
			throw e;
		}
		
		if (lockPrivilege.equals(READ_WRITE_LOCK_PRIVILEGE)){
			
			return C1G2LockPrivilege.Read_Write;
			
		} else if (lockPrivilege.equals(PERMA_LOCK_PRIVILEGE)){
			
			return C1G2LockPrivilege.Perma_Lock;
			
		} else if (lockPrivilege.equals(UNLOCK_PRIVILEGE)){
			
			return C1G2LockPrivilege.Unlock;
			
		} else {
			throw e;
		}

	}

	/**
	 * 
	 * @param opSpecId
	 * @param lockPrivilege
	 * @param lockDataField
	 * @return
	 */
	public C1G2Lock buildLockOpSpec(UnsignedShort opSpecId, int lockPrivilege,
			int lockDataField) {
		// Create a new OpSpec.
		// This specifies what operation we want to perform on the
		// tags that match the specifications above.
		// In this case, we want to write to the tag.
		C1G2Lock opSpec = new C1G2Lock();

		// Set the OpSpecID to a unique number.

		opSpec.setOpSpecID(opSpecId);

		// opSpec.setWordPointer(new UnsignedShort(0x02));->only for writing

		UnsignedInteger unsignedIntAccesPass = new UnsignedInteger(
				getAccessPwd(), 16);
		
		opSpec.setAccessPassword(unsignedIntAccesPass);
		C1G2LockPayload payload = new C1G2LockPayload();
		C1G2LockDataField dataField = new C1G2LockDataField(lockDataField);
		payload.setDataField(dataField);

		C1G2LockPrivilege privilege = new C1G2LockPrivilege(lockPrivilege);
		payload.setPrivilege(privilege);

		List<C1G2LockPayload> c1G2LockPayloadList = new ArrayList<C1G2LockPayload>();
		c1G2LockPayloadList.add(payload);

		opSpec.setC1G2LockPayloadList(c1G2LockPayloadList);

		return opSpec;
	}

	/**
	 * Cleans up the session and access specs
	 */
	public void cleanupSession() {

		deleteAccessSpecs();
		setRunningLLRPEncoding(false);

	}
	
		
	// Create a OpSpec that reads the EPC
	public C1G2Read buildEpcReadOpSpec() {
		// Create a new OpSpec.
		// This specifies what operation we want to perform on the
		// tags that match the specifications above.
		C1G2Read opSpec = new C1G2Read();

		// Set the OpSpecID to a unique number.

		opSpec.setOpSpecID(READ_EPC_OPSPEC_ID);

		// opSpec.setAccessPassword(new UnsignedInteger(getAccessPwd()));
		UnsignedInteger unsignedIntAccesPass = new UnsignedInteger(
				getAccessPwd(), 16);
		
		opSpec.setAccessPassword(unsignedIntAccesPass);

		// For this demo, we'll write to user memory (bank 3).
		TwoBitField opMemBank = new TwoBitField();

		// Set bit 1 (bank 2 in binary).
		opMemBank.set(1);
		opMemBank.clear(0);

		opSpec.setMB(opMemBank);

		opSpec.setWordPointer(new UnsignedShort(0x02));

		// Read n words. 
		opSpec.setWordCount(new UnsignedShort(getWordCount()));

		return opSpec;
	}
	
	// Create a OpSpec that reads the access password
	public C1G2Read buildAccessPassRead() {
		// Create a new OpSpec.
		// This specifies what operation we want to perform on the
		// tags that match the specifications above.
		C1G2Read opSpec = new C1G2Read();
		// Set the OpSpecID to a unique number.
		opSpec.setOpSpecID(READ_ACCESSPASS_OPSPEC_ID);
		
		UnsignedInteger unsignedIntAccesPass = new UnsignedInteger(
				getOldAccessPwd(), 16);
		
		opSpec.setAccessPassword(unsignedIntAccesPass);
		
		// For this demo, we'll write to user memory (bank 3).
		TwoBitField opMemBank = new TwoBitField();
		// Clear twobits (bank 0 in binary).
		opMemBank.clear(1);
		opMemBank.clear(0);

		opSpec.setMB(opMemBank);
		opSpec.setWordPointer(new UnsignedShort(2));

		// Read two words. 
		opSpec.setWordCount(new UnsignedShort(2));

		return opSpec;
	}
	
	// Create a OpSpec that reads the kill password
	public C1G2Read buildKillPassRead() {
		// Create a new OpSpec.
		// This specifies what operation we want to perform on the
		// tags that match the specifications above.
		// In this case, we want to write to the tag.
		C1G2Read opSpec = new C1G2Read();
		// Set the OpSpecID to a unique number.
		opSpec.setOpSpecID(READ_KILLPASS_OPSPEC_ID);
		
		UnsignedInteger unsignedIntAccesPass = new UnsignedInteger(
				getOldAccessPwd(), 16);
		
		opSpec.setAccessPassword(unsignedIntAccesPass);
		
		// For this demo, we'll write to user memory (bank 3).
		TwoBitField opMemBank = new TwoBitField();
		// Clear twobits (bank 0 in binary).
		opMemBank.clear(1);
		opMemBank.clear(0);

		opSpec.setMB(opMemBank);
		opSpec.setWordPointer(new UnsignedShort(0));

		//Read two words. 
		opSpec.setWordCount(new UnsignedShort(2));

		return opSpec;
	}
	
	// Create a OpSpec that reads from user memory public C1G2Read
	//public C1G2Read buildEpcReadOpSpec(String epcId) {
	public C1G2Read buildUserMemoryReadOpSpec() {
		// Create a new OpSpec.
		// This specifies what operation we want to perform on the
		// tags that match the specifications above.
		C1G2Read opSpec = new C1G2Read();

		// Set the OpSpecID to a unique number.

		opSpec.setOpSpecID(READ_USER_MEMORY_OPSPEC_ID);

		UnsignedInteger unsignedIntAccesPass = new UnsignedInteger(
				getAccessPwd(), 16);
		
		opSpec.setAccessPassword(unsignedIntAccesPass);

		// For this demo, we'll write to user memory (bank 3).
		TwoBitField opMemBank = new TwoBitField();
		
		opMemBank.set(0);
		opMemBank.set(1);

		opSpec.setMB(opMemBank);

		opSpec.setWordPointer(new UnsignedShort(0x00));

		// Read 2 words. 
		opSpec.setWordCount(new UnsignedShort(getWordCount()));
		
		return opSpec;
	}
	
	// Create a OpSpec that reads from user memory public C1G2Read
	//public C1G2Read buildEpcReadOpSpec(String epcId) {
	public C1G2Read buildMemBankReadOpSpec() {
		// Create a new OpSpec.
		// This specifies what operation we want to perform on the
		// tags that match the specifications above.
		C1G2Read opSpec = new C1G2Read();

		// Set the OpSpecID to a unique number.

		opSpec.setOpSpecID(READ_MEMORY_BANK_OPSEC_ID);

		UnsignedInteger unsignedIntAccesPass = new UnsignedInteger(getAccessPwd(), 16);
		
		opSpec.setAccessPassword(unsignedIntAccesPass);

		// For this demo, we'll write to user memory (bank 3).
		TwoBitField opMemBank = new TwoBitField();
		
		//opMemBank.set(1);
		//opMemBank.set(0);
		if(this.membank1==1) {
			opMemBank.set(0);
		} else {
			opMemBank.clear(0);
		}
		if(this.membank2==1) {
			opMemBank.set(1);
		} else {
			opMemBank.clear(1);
		}
		
		opSpec.setMB(opMemBank);

		opSpec.setWordPointer(new UnsignedShort(0x00));

		// Read 2 words. 
		opSpec.setWordCount(new UnsignedShort(getWordCount()));
		
		return opSpec;
	}
	
	// Create a OpSpec that writes to user memory
	public C1G2Write buildUserMemoryWriteOpSpec() {
		// Create a new OpSpec.
		// This specifies what operation we want to perform on the
		// tags that match the specifications above.
		C1G2Write opSpec = new C1G2Write();

		// Set the OpSpecID to a unique number.

		opSpec.setOpSpecID(WRITE_USER_MEMORY_OPSPEC_ID);

		UnsignedInteger unsignedIntAccesPass = new UnsignedInteger(
				getAccessPwd(), 16);
		
		opSpec.setAccessPassword(unsignedIntAccesPass);

		// For this demo, we'll write to user memory (bank 3).
		TwoBitField opMemBank = new TwoBitField();

		// Set bit 1 (bank 2 in binary).
		opMemBank.set(0);
		opMemBank.set(1);

		opSpec.setMB(opMemBank);

		// We'll write to the base of this memory bank (0x00).
		opSpec.setWordPointer(new UnsignedShort(0x00));

		UnsignedShortArray_HEX writeData = getFormatedWriteArrayData(getUserMemoryData());
		
		// We'll write 8 bytes or two words.
		//writeData.add(new UnsignedShort (0xAABB));
		//writeData.add(new UnsignedShort (0xCCDD));
		opSpec.setWriteData(writeData);

		return opSpec;
	}
	

}

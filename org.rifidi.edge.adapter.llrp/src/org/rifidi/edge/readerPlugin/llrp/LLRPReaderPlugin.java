/*
 *  LLRPReaderAdapter.java
 *
 *  Created:	Jun 20, 2006
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.readerPlugin.llrp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.llrp.ltk.exceptions.InvalidLLRPMessageException;
import org.llrp.ltk.generated.enumerations.AISpecStopTriggerType;
import org.llrp.ltk.generated.enumerations.AccessReportTriggerType;
import org.llrp.ltk.generated.enumerations.AirProtocols;
import org.llrp.ltk.generated.enumerations.NotificationEventType;
import org.llrp.ltk.generated.enumerations.ROReportTriggerType;
import org.llrp.ltk.generated.enumerations.ROSpecStartTriggerType;
import org.llrp.ltk.generated.enumerations.ROSpecState;
import org.llrp.ltk.generated.enumerations.ROSpecStopTriggerType;
import org.llrp.ltk.generated.messages.ADD_ROSPEC;
import org.llrp.ltk.generated.messages.CLOSE_CONNECTION;
import org.llrp.ltk.generated.messages.DELETE_ROSPEC;
import org.llrp.ltk.generated.messages.DISABLE_ROSPEC;
import org.llrp.ltk.generated.messages.ENABLE_ROSPEC;
import org.llrp.ltk.generated.messages.LLRPMessageFactory;
import org.llrp.ltk.generated.messages.RO_ACCESS_REPORT;
import org.llrp.ltk.generated.messages.SET_READER_CONFIG;
import org.llrp.ltk.generated.messages.START_ROSPEC;
import org.llrp.ltk.generated.messages.STOP_ROSPEC;
import org.llrp.ltk.generated.parameters.AISpec;
import org.llrp.ltk.generated.parameters.AISpecStopTrigger;
import org.llrp.ltk.generated.parameters.AccessReportSpec;
import org.llrp.ltk.generated.parameters.C1G2EPCMemorySelector;
import org.llrp.ltk.generated.parameters.EPC_96;
import org.llrp.ltk.generated.parameters.EventNotificationState;
import org.llrp.ltk.generated.parameters.InventoryParameterSpec;
import org.llrp.ltk.generated.parameters.ROBoundarySpec;
import org.llrp.ltk.generated.parameters.ROReportSpec;
import org.llrp.ltk.generated.parameters.ROSpec;
import org.llrp.ltk.generated.parameters.ROSpecStartTrigger;
import org.llrp.ltk.generated.parameters.ROSpecStopTrigger;
import org.llrp.ltk.generated.parameters.ReaderEventNotificationSpec;
import org.llrp.ltk.generated.parameters.TagReportContentSelector;
import org.llrp.ltk.generated.parameters.TagReportData;
import org.llrp.ltk.types.Bit;
import org.llrp.ltk.types.LLRPInteger;
import org.llrp.ltk.types.LLRPMessage;
import org.llrp.ltk.types.UnsignedInteger;
import org.llrp.ltk.types.UnsignedShort;
import org.llrp.ltk.types.UnsignedShortArray;
import org.rifidi.edge.common.utilities.converter.ByteAndHexConvertingUtility;
import org.rifidi.edge.core.exception.readerPlugin.RifidiConnectionException;
import org.rifidi.edge.core.readerPlugin.IReaderPlugin;
import org.rifidi.edge.core.readerPlugin.commands.ICustomCommand;
import org.rifidi.edge.core.readerPlugin.commands.ICustomCommandResult;
import org.rifidi.edge.core.tag.TagRead;

/**
 * LLRP Reader.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class LLRPReaderPlugin implements IReaderPlugin {

	/**
	 * The connection info for this reader
	 */
	private LLRPReaderInfo aci;

	/**
	 * The log4j logger
	 */
	private static Log logger = LogFactory.getLog(LLRPReaderPlugin.class);

	/**
	 * 
	 */
	private Socket connection = null;

	/**
	 * The incoming data stream from the LLRP reader connection
	 */
	private DataInputStream in = null;
	private DataOutputStream out = null;

	/**
	 * The ID of the ROSpec that is used
	 */
	private static int ROSPEC_ID = 1;

	/**
	 * Adapter for the Alien reader.
	 * 
	 * @param aci
	 *            The connection info for the Alien Reader.
	 */
	public LLRPReaderPlugin(LLRPReaderInfo aci) {
		this.aci = aci;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerAdapter.IReaderAdapter#connect()
	 */
	@Override
	public void connect() throws RifidiConnectionException {
		// Connect to the LLRP Reader
		try {
			connection = new Socket(aci.getIPAddress(), aci.getPort());
			out = new DataOutputStream(connection.getOutputStream());
			in = new DataInputStream(connection.getInputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			throw new RifidiConnectionException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RifidiConnectionException(e);
		}

		try {
			LLRPMessage m = read();
			logger.debug(m.toXMLString());

			SET_READER_CONFIG config = createSetReaderConfig();
			write(config, "Set Reader Config");
			LLRPMessage setconf = readInCommand();
			logger.debug("SET CONF TO XML");
			try {
				logger.debug(setconf.toXMLString());
			} catch (InvalidLLRPMessageException e2) {
				e2.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RifidiConnectionException(e);
		} catch (InvalidLLRPMessageException e) {
			e.printStackTrace();
			throw new RifidiConnectionException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerAdapter.IReaderAdapter#disconnect()
	 */
	@Override
	public void disconnect() throws RifidiConnectionException {
		// wait for one second before closing the connection

		// Create a CLOSE_CONNECTION message and send it to the reader
		CLOSE_CONNECTION cc = new CLOSE_CONNECTION();
		write(cc, "CloseConnection");
		try {
			LLRPMessage m = read();
			logger.debug(m.toXMLString());
		} catch (IOException e) {
			e.printStackTrace();
			throw new RifidiConnectionException(e);
		} catch (InvalidLLRPMessageException e) {
			e.printStackTrace();
			throw new RifidiConnectionException(e);
		}

		this.pause(1000);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerAdapter.IReaderAdapter#sendCommand(byte[])
	 */
	@Override
	public ICustomCommandResult sendCustomCommand(ICustomCommand customCommand) {

		return null;
	}

	/**
	 * Gets the next tag
	 * 
	 */
	@Override
	public List<TagRead> getNextTags() {
		logger.debug("STARTING THE GETNEXTTAGS");

		List<TagRead> retVal = null;

		// CREATE an ADD_ROSPEC Message and send it to the reader
		ADD_ROSPEC addROSpec = new ADD_ROSPEC();
		addROSpec.setROSpec(createROSpec());
		write(addROSpec, "ADD_ROSPEC");
		pause(250);
		LLRPMessage adros = readInCommand();
		logger.debug("ADD ROSPEC TO XML");
		try {
			logger.debug(adros.toXMLString());
		} catch (InvalidLLRPMessageException e2) {
			e2.printStackTrace();
		}

		// Create an ENABLE_ROSPEC message and send it to the reader
		ENABLE_ROSPEC enableROSpec = new ENABLE_ROSPEC();
		enableROSpec.setROSpecID(new UnsignedInteger(ROSPEC_ID));
		write(enableROSpec, "ENABLE_ROSPEC");
		pause(250);

		LLRPMessage enros = readInCommand();
		logger.debug("ENABLE ROSPEC TO XML");
		try {
			logger.debug(enros.toXMLString());
		} catch (InvalidLLRPMessageException e1) {
			e1.printStackTrace();
		}

		// Create a START_ROSPEC message and send it to the reader
		START_ROSPEC startROSpec = new START_ROSPEC();
		startROSpec.setROSpecID(new UnsignedInteger(ROSPEC_ID));
		write(startROSpec, "START_ROSPEC");
		LLRPMessage startros = readInCommand();
		logger.debug("START ROSPEC TO XML");
		try {
			logger.debug(startros.toXMLString());
		} catch (InvalidLLRPMessageException e1) {
			e1.printStackTrace();
		}

		LLRPMessage startros2 = readInCommand();
		logger.debug("START ROSPEC2 TO XML");
		try {
			logger.debug(startros2.toXMLString());
		} catch (InvalidLLRPMessageException e1) {
			e1.printStackTrace();
		}

		pause(1000);

		// Create a STOP_ROSPEC message and send it to the reader
		STOP_ROSPEC stopROSpec = new STOP_ROSPEC();
		stopROSpec.setROSpecID(new UnsignedInteger(ROSPEC_ID));
		write(stopROSpec, "STOP_ROSPEC");
		pause(250);
		LLRPMessage stopros = readInCommand();
		logger.debug("STOP ROSPEC TO XML");
		try {
			logger.debug(stopros.toXMLString());
		} catch (InvalidLLRPMessageException e1) {
			e1.printStackTrace();
		}

		LLRPMessage stopros2 = readInCommand();
		logger.debug("STOP ROSPEC2 TO XML");
		try {
			logger.debug(stopros2.toXMLString());
		} catch (InvalidLLRPMessageException e1) {
			e1.printStackTrace();
		}

		LLRPMessage tags = readInCommand();
		retVal = parseTags(tags);

		try {
			logger.debug("TAGS TO XML STRING");
			logger.debug(tags.toXMLString());
		} catch (InvalidLLRPMessageException e) {
			e.printStackTrace();
		}

		// Create a DISABLE_ROSPEC message and send it to the reader
		DISABLE_ROSPEC disableROSpec = new DISABLE_ROSPEC();
		disableROSpec.setROSpecID(new UnsignedInteger(ROSPEC_ID));
		write(disableROSpec, "DISABLE_ROSPEC");
		pause(250);

		LLRPMessage disros = readInCommand();
		logger.debug(disros);

		// Create a DELTE_ROSPEC message and send it to the reader
		DELETE_ROSPEC deleteROSpec = new DELETE_ROSPEC();
		deleteROSpec.setROSpecID(new UnsignedInteger(ROSPEC_ID));
		write(deleteROSpec, "DELETE_ROSPEC");
		pause(250);

		LLRPMessage delros = readInCommand();
		logger.debug(delros);

		return retVal;
	}

	/**
	 * Send a llrp message to the reader
	 * 
	 * @param msg
	 *            Message to be send
	 * @param message
	 *            Description for output purposes
	 */
	private void write(LLRPMessage msg, String message) {
		try {
			logger.debug(" Sending message: \n" + msg.toXMLString());
			out.write(msg.encodeBinary());
			out.flush();
		} catch (IOException e) {
			logger.error("Couldn't send Command ", e);
		} catch (InvalidLLRPMessageException e) {
			logger.error("Couldn't send Command", e);
		}
	}

	/**
	 * This method creates a SET_READER_CONFIG method
	 * 
	 * @return
	 */
	private SET_READER_CONFIG createSetReaderConfig() {
		SET_READER_CONFIG setReaderConfig = new SET_READER_CONFIG();

		// Create a default RoReportSpec so that reports are sent at the end of
		// ROSpecs
		ROReportSpec roReportSpec = new ROReportSpec();
		roReportSpec.setN(new UnsignedShort(0));
		roReportSpec.setROReportTrigger(new ROReportTriggerType(
				ROReportTriggerType.Upon_N_Tags_Or_End_Of_ROSpec));
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
				AccessReportTriggerType.End_Of_AccessSpec));
		setReaderConfig.setAccessReportSpec(accessReportSpec);

		// Set up reporting for AISpec events, ROSpec events, and GPI Events

		ReaderEventNotificationSpec eventNoteSpec = new ReaderEventNotificationSpec();
		EventNotificationState noteState = new EventNotificationState();
		noteState.setEventType(new NotificationEventType(
				NotificationEventType.AISpec_Event));
		noteState.setNotificationState(new Bit(1));
		eventNoteSpec.addToEventNotificationStateList(noteState);
		noteState = new EventNotificationState();
		noteState.setEventType(new NotificationEventType(
				NotificationEventType.ROSpec_Event));
		noteState.setNotificationState(new Bit(1));
		eventNoteSpec.addToEventNotificationStateList(noteState);
		noteState = new EventNotificationState();
		noteState.setEventType(new NotificationEventType(
				NotificationEventType.GPI_Event));
		noteState.setNotificationState(new Bit(1));
		eventNoteSpec.addToEventNotificationStateList(noteState);
		setReaderConfig.setReaderEventNotificationSpec(eventNoteSpec);

		setReaderConfig.setResetToFactoryDefault(new Bit(0));

		return setReaderConfig;
	}

	/**
	 * Parses the tags from xml.
	 * 
	 * @param msg
	 * @return
	 */
	private List<TagRead> parseTags(LLRPMessage msg) {

		List<TagRead> retVal = new ArrayList<TagRead>();
		
		RO_ACCESS_REPORT rar = (RO_ACCESS_REPORT)msg;
		
		//TagReportData list parse
		for(TagReportData t:rar.getTagReportDataList()) {
			TagRead newTag = new TagRead();
			EPC_96 id = (EPC_96)t.getEPCParameter();
			newTag.setId(ByteAndHexConvertingUtility.fromHexString(id.getEPC().toString()));
			//TODO: Either our classes or the Toolkit handles this in the wrong way.   
			//newTag.setLastSeenTime(t.getLastSeenTimestampUTC().getMicroseconds().toLong());
			newTag.setLastSeenTime(System.nanoTime());
			retVal.add(newTag);
		}
		

		return retVal;
	}

	/**
	 * Returns true if the adapter is blocking
	 */
	@Override
	public boolean isBlocking() {
		return false;
	}

	/**
	 * Read everything from the stream until the socket is closed
	 * 
	 * @throws InvalidLLRPMessageException
	 */
	public LLRPMessage read() throws IOException, InvalidLLRPMessageException {
		LLRPMessage m = null;

		// The message header
		byte[] first = new byte[6];

		// the complete message
		byte[] msg;

		// Read in the message header. If -1 is read, there is no more
		// data available, so close the socket
		if (in.read(first, 0, 6) == -1) {
			// TODO ErrorHandling
			return null;
		}
		int msgLength = 0;

		try {
			// calculate message length
			msgLength = calculateLLRPMessageLength(first);
		} catch (IllegalArgumentException e) {
			throw new IOException("Incorrect Message Length");
		}

		logger.debug("msg length is: " + msgLength);

		/*
		 * the rest of bytes of the message will be stored in here before they
		 * are put in the accumulator. If the message is short, all
		 * messageLength-6 bytes will be read in here at once. If it is long,
		 * the data might not be available on the socket all at once, so it make
		 * take a couple of iterations to read in all the bytes
		 */
		byte[] temp = new byte[msgLength - 6];

		// all the rest of the bytes will be put into the accumulator
		ArrayList<Byte> accumulator = new ArrayList<Byte>();

		// add the first six bytes to the accumulator so that it will
		// contain all the bytes at the end
		for (byte b : first) {
			accumulator.add(b);
		}

		// the number of bytes read on the last call to read()
		int numBytesRead = 0;

		// read from the input stream and put bytes into the accumulator
		// while there are still bytes left to read on the socket and
		// the entire message has not been read
		while (((msgLength - accumulator.size()) != 0) && numBytesRead != -1) {
			logger.debug("about to read stuff");
			numBytesRead = in.read(temp, 0, msgLength - accumulator.size());
			logger.debug("reading stuff: " + numBytesRead + ", msg length: "
					+ accumulator.size() + ", temp size: " + temp.length);
			for (int i = 0; i < numBytesRead; i++) {
				accumulator.add(temp[i]);
			}
		}

		logger.debug("after the while");
		if ((msgLength - accumulator.size()) != 0) {
			throw new IOException("Error: Discrepency between message size"
					+ " in header and actual number of bytes read");
		}

		msg = new byte[msgLength];

		// copy all bytes in the accumulator to the msg byte array
		for (int i = 0; i < accumulator.size(); i++) {
			msg[i] = accumulator.get(i);
		}

		// turn the byte array into an LLRP Message Object
		m = LLRPMessageFactory.createLLRPMessage(msg);
		return m;
	}

	/**
	 * Send in the first 6 bytes of an LLRP Message
	 * 
	 * @param bytes
	 * @return
	 */
	public static int calculateLLRPMessageLength(byte[] bytes)
			throws IllegalArgumentException {
		long msgLength = 0;
		int num1 = 0;
		int num2 = 0;
		int num3 = 0;
		int num4 = 0;

		num1 = ((ByteAndHexConvertingUtility.unsignedByteToInt(bytes[2])));
		num1 = num1 << 32;
		if (num1 > 127) {
			throw new RuntimeException(
					"Cannot construct a message greater than "
							+ "2147483647 bytes (2^31 - 1), due to the fact that there are "
							+ "no unsigned ints in java");
		}

		num2 = ((ByteAndHexConvertingUtility.unsignedByteToInt(bytes[3])));
		num2 = num2 << 16;

		num3 = ((ByteAndHexConvertingUtility.unsignedByteToInt(bytes[4])));
		num3 = num3 << 8;

		num4 = (ByteAndHexConvertingUtility.unsignedByteToInt(bytes[5]));

		msgLength = num1 + num2 + num3 + num4;

		if (msgLength < 0) {
			throw new IllegalArgumentException(
					"LLRP message length is less than 0");
		} else {
			return (int) msgLength;
		}
	}

	/**
	 * This method creates a ROSpec with null start and stop triggers
	 * 
	 * @return
	 */
	private ROSpec createROSpec() {
		// create a new rospec
		ROSpec roSpec = new ROSpec();
		roSpec.setPriority(new LLRPInteger(0));
		roSpec.setCurrentState(new ROSpecState(ROSpecState.Disabled));
		roSpec.setROSpecID(new UnsignedInteger(ROSPEC_ID));

		// set up ROBoundary (start and stop triggers)
		ROBoundarySpec roBoundarySpec = new ROBoundarySpec();

		ROSpecStartTrigger startTrig = new ROSpecStartTrigger();
		startTrig.setROSpecStartTriggerType(new ROSpecStartTriggerType(
				ROSpecStartTriggerType.Null));
		roBoundarySpec.setROSpecStartTrigger(startTrig);

		ROSpecStopTrigger stopTrig = new ROSpecStopTrigger();
		stopTrig.setDurationTriggerValue(new UnsignedInteger(100));
		stopTrig.setROSpecStopTriggerType(new ROSpecStopTriggerType(
				ROSpecStopTriggerType.Null));
		roBoundarySpec.setROSpecStopTrigger(stopTrig);

		roSpec.setROBoundarySpec(roBoundarySpec);

		// Add an AISpec
		AISpec aispec = new AISpec();

		// set AI Stop trigger to null
		AISpecStopTrigger aiStopTrigger = new AISpecStopTrigger();
		aiStopTrigger.setAISpecStopTriggerType(new AISpecStopTriggerType(
				AISpecStopTriggerType.Null));
		aiStopTrigger.setDurationTrigger(new UnsignedInteger(0));
		aispec.setAISpecStopTrigger(aiStopTrigger);

		UnsignedShortArray antennaIDs = new UnsignedShortArray();
		antennaIDs.add(new UnsignedShort(0));
		aispec.setAntennaIDs(antennaIDs);

		InventoryParameterSpec inventoryParam = new InventoryParameterSpec();
		inventoryParam.setProtocolID(new AirProtocols(
				AirProtocols.EPCGlobalClass1Gen2));
		inventoryParam.setInventoryParameterSpecID(new UnsignedShort(1));
		aispec.addToInventoryParameterSpecList(inventoryParam);

		roSpec.addToSpecParameterList(aispec);

		return roSpec;
	}

	/**
	 * This method causes the calling thread to sleep for a specified number of
	 * milliseconds
	 * 
	 * @param ms
	 */
	private void pause(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * From http://www.rgagnon.com/javadetails/java-0026.html
	 * 
	 * @param b
	 * @return
	 */
	public int unsignedByteToInt(byte b) {
		return (int) b & 0xFF;
	}

	/**
	 * Reads in an LLRPCommand
	 * 
	 * @return
	 */
	private LLRPMessage readInCommand() {
		LLRPMessage m = null;
		try {
			m = read();
		} catch (IOException e) {

		} catch (InvalidLLRPMessageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return m;
	}
}

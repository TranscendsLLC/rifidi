/**
 * 
 */
package org.rifidi.edge.adapter.llrp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.llrp.ltk.exceptions.InvalidLLRPMessageException;
import org.llrp.ltk.generated.enumerations.AISpecStopTriggerType;
import org.llrp.ltk.generated.enumerations.AirProtocols;
import org.llrp.ltk.generated.enumerations.ROSpecStartTriggerType;
import org.llrp.ltk.generated.enumerations.ROSpecState;
import org.llrp.ltk.generated.enumerations.ROSpecStopTriggerType;
import org.llrp.ltk.generated.messages.ADD_ROSPEC;
import org.llrp.ltk.generated.messages.CLOSE_CONNECTION;
import org.llrp.ltk.generated.messages.DELETE_ROSPEC;
import org.llrp.ltk.generated.messages.DISABLE_ROSPEC;
import org.llrp.ltk.generated.messages.ENABLE_ROSPEC;
import org.llrp.ltk.generated.messages.LLRPMessageFactory;
import org.llrp.ltk.generated.parameters.AISpec;
import org.llrp.ltk.generated.parameters.AISpecStopTrigger;
import org.llrp.ltk.generated.parameters.InventoryParameterSpec;
import org.llrp.ltk.generated.parameters.ROBoundarySpec;
import org.llrp.ltk.generated.parameters.ROSpec;
import org.llrp.ltk.generated.parameters.ROSpecStartTrigger;
import org.llrp.ltk.generated.parameters.ROSpecStopTrigger;
import org.llrp.ltk.types.LLRPInteger;
import org.llrp.ltk.types.LLRPMessage;
import org.llrp.ltk.types.UnsignedInteger;
import org.llrp.ltk.types.UnsignedShort;
import org.llrp.ltk.types.UnsignedShortArray;
import org.rifidi.edge.core.readerAdapter.IReaderAdapter;
import org.rifidi.edge.core.readerAdapter.commands.ICustomCommand;
import org.rifidi.edge.core.tag.TagRead;

/**
 * @author Matthew Dean - matt@pramari.com
 * 
 */
public class LLRPReaderAdapter implements IReaderAdapter {

	/**
	 * The connection info for this reader
	 */
	private LLRPConnectionInfo aci;

	/**
	 * The log4j logger
	 */
	private static Logger logger;

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
	public LLRPReaderAdapter(LLRPConnectionInfo aci) {
		this.aci = aci;

		logger = Logger.getLogger(LLRPReaderAdapter.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerAdapter.IReaderAdapter#connect()
	 */
	@Override
	public boolean connect() {
		// Connect to the Alien Reader
		try {
			connection = new Socket(aci.getIPAddress(), aci.getPort());
			out = new DataOutputStream(connection.getOutputStream());
			in = new DataInputStream(connection.getInputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerAdapter.IReaderAdapter#disconnect()
	 */
	@Override
	public boolean disconnect() {

		// wait for one second before closing the connection
		pause(1000);

		try {
			// Create a CLOSE_CONNECTION message and send it to the reader
			CLOSE_CONNECTION cc = new CLOSE_CONNECTION();
			write(cc, "CloseConnection");
			LLRPMessage m = read();
			logger.debug(m);
		} catch (IOException e) {

		} catch (InvalidLLRPMessageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerAdapter.IReaderAdapter#sendCommand(byte[])
	 */
	@Override
	public void sendCustomCommand(ICustomCommand customCommand) {

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
		stopTrig.setDurationTriggerValue(new UnsignedInteger(0));
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
	 * Send a llrp message to the reader
	 * 
	 * @param msg
	 *            Message to be send
	 * @param message
	 *            Description for output purposes
	 */
	private void write(LLRPMessage msg, String message) {
		try {
			logger.info(" Sending message: \n" + msg.toXMLString());
			out.write(msg.encodeBinary());
		} catch (IOException e) {
			logger.error("Couldn't send Command ", e);
		} catch (InvalidLLRPMessageException e) {
			logger.error("Couldn't send Command", e);
		}
	}

	/**
	 * Gets the next tag
	 * 
	 */
	@Override
	public List<TagRead> getNextTags() {
		List<TagRead> retVal = null;

		// CREATE an ADD_ROSPEC Message and send it to the reader
		ADD_ROSPEC addROSpec = new ADD_ROSPEC();
		addROSpec.setROSpec(createROSpec());
		write(addROSpec, "ADD_ROSPEC");
		pause(250);
		LLRPMessage adros = readInCommand();
		logger.debug(adros);

		// Create an ENABLE_ROSPEC message and send it to the reader
		ENABLE_ROSPEC enableROSpec = new ENABLE_ROSPEC();
		enableROSpec.setROSpecID(new UnsignedInteger(ROSPEC_ID));
		write(enableROSpec, "ENABLE_ROSPEC");
		pause(250);

		LLRPMessage enros = readInCommand();
		logger.debug(enros);

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

	@Override
	public boolean isBlocking() {
		// TODO Auto-generated method stub
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
			return null;
		}
		int msgLength = 0;

		try {
			// calculate message length
			msgLength = calculateLLRPMessageLength(first);
		} catch (IllegalArgumentException e) {
			throw new IOException("Incorrect Message Length");
		}

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

			numBytesRead = in.read(temp, 0, msgLength - accumulator.size());

			for (int i = 0; i < numBytesRead; i++) {
				accumulator.add(temp[i]);
			}
		}

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
	private int calculateLLRPMessageLength(byte[] bytes)
			throws IllegalArgumentException {
		long msgLength = 0;
		int num1 = 0;
		int num2 = 0;
		int num3 = 0;
		int num4 = 0;

		num1 = ((unsignedByteToInt(bytes[2])));
		num1 = num1 << 32;
		if (num1 > 127) {
			throw new RuntimeException(
					"Cannot construct a message greater than "
							+ "2147483647 bytes (2^31 - 1), due to the fact that there are "
							+ "no unsigned ints in java");
		}

		num2 = ((unsignedByteToInt(bytes[3])));
		num2 = num2 << 16;

		num3 = ((unsignedByteToInt(bytes[4])));
		num3 = num3 << 8;

		num4 = (unsignedByteToInt(bytes[5]));

		msgLength = num1 + num2 + num3 + num4;

		if (msgLength < 0) {
			throw new IllegalArgumentException(
					"LLRP message length is less than 0");
		} else {
			return (int) msgLength;
		}
	}

	/**
	 * From http://www.rgagnon.com/javadetails/java-0026.html
	 * 
	 * @param b
	 * @return
	 */
	private int unsignedByteToInt(byte b) {
		return (int) b & 0xFF;
	}

}

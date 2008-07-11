/**
 * 
 */
package org.rifidi.edge.readerplugin.llrp.protocol;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.llrp.ltk.exceptions.InvalidLLRPMessageException;
import org.llrp.ltk.generated.messages.LLRPMessageFactory;
import org.llrp.ltk.types.LLRPMessage;
import org.rifidi.edge.common.utilities.converter.ByteAndHexConvertingUtility;
import org.rifidi.edge.core.exceptions.RifidiInvalidMessageFormat;
import org.rifidi.edge.core.readerplugin.protocol.CommunicationProtocol;

/**
 * @author kyle
 * 
 */
public class LLRPCommunicationProtocol implements CommunicationProtocol {

	private ArrayList<Byte> byteArrayList;

	private int msgSize;

	/**
	 * The log4j logger.
	 */
	private static final Log logger = LogFactory
			.getLog(LLRPCommunicationProtocol.class);

	public LLRPCommunicationProtocol() {
		byteArrayList = new ArrayList<Byte>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.protocol.CommunicationProtocol#byteToMessage(byte)
	 */
	@Override
	public Object byteToMessage(byte b) throws RifidiInvalidMessageFormat {
		byteArrayList.add(b);
		LLRPMessage retVal = null;
		if (byteArrayList.size() == 6) {
			byte[] sizeArray = getByteArray(byteArrayList);
			msgSize = calculateLLRPMessageLength(sizeArray);
		} else if (byteArrayList.size() > 6) {
			if (byteArrayList.size() == msgSize) {
				byte[] finalArray = getByteArray(byteArrayList);
				try {
					retVal = LLRPMessageFactory.createLLRPMessage(finalArray);
					byteArrayList.clear();
					msgSize = 0;
				} catch (InvalidLLRPMessageException e) {
					logger.error("Invalid LLRP message creation");
					throw new RifidiInvalidMessageFormat();
				}
			}
		}

		return retVal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.protocol.CommunicationProtocol#messageToByte(java.lang.Object)
	 */
	@Override
	public byte[] messageToByte(Object message)
			throws RifidiInvalidMessageFormat {
		LLRPMessage llrpm = (LLRPMessage) message;
		try {
			return llrpm.encodeBinary();
		} catch (InvalidLLRPMessageException e) {
			logger.debug("Invalid LLRP message conversion");
			throw new RifidiInvalidMessageFormat(
					"Exception when converting LLRPMessage to byte array");
		}
	}

	private byte[] getByteArray(ArrayList<Byte> arg) {
		byte[] retVal = new byte[arg.size()];
		int omg = 0;
		for (Byte b : arg) {
			retVal[omg] = b.byteValue();
			omg++;
		}

		return retVal;
	}

	public int calculateLLRPMessageLength(byte[] bytes)
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

}

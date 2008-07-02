/* 
 * LLRPProtcol.java
 *  Created:	Jun 20, 2006
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.readerPlugin.llrp;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.llrp.ltk.exceptions.InvalidLLRPMessageException;
import org.llrp.ltk.generated.messages.LLRPMessageFactory;
import org.llrp.ltk.types.LLRPMessage;
import org.rifidi.edge.common.utilities.converter.ByteAndHexConvertingUtility;
import org.rifidi.edge.core.communication.protocol.Protocol;
import org.rifidi.edge.core.exception.readerConnection.RifidiInvalidMessageFormat;

/**
 * 
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class LLRPProtocol extends Protocol {

	private ArrayList<Byte> byteArrayList;

	private int msgSize;

	/**
	 * The log4j logger.
	 */
	private static final Log logger = LogFactory.getLog(LLRPProtocol.class);

	public LLRPProtocol() {
		byteArrayList = new ArrayList<Byte>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.communication.protocol.Protocol#add(byte)
	 */
	@Override
	public Object add(byte b) throws RifidiInvalidMessageFormat {
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
					logger.debug("Invalid LLRP message creation", e);
					throw new RifidiInvalidMessageFormat();
				}
			}
		}

		return retVal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.communication.protocol.Protocol#toByteArray(java.lang.Object)
	 */
	@Override
	public byte[] toByteArray(Object o) throws RifidiInvalidMessageFormat {
		LLRPMessage llrpm = (LLRPMessage) o;
		try {
			return llrpm.encodeBinary();
		} catch (InvalidLLRPMessageException e) {
			logger.debug("Invalid LLRP message conversion", e);
		}
		return null;
	}

	// /*
	// * (non-Javadoc)
	// *
	// * @see
	// org.rifidi.edge.core.communication.buffer.Protocol#toObject(byte[])
	// */
	// @Override
	// public List<Object> toObject(byte[] arg) {
	//
	// List<Object> retVal = new ArrayList<Object>();
	// boolean more = true;
	// while (more) {
	// if (bais.available() > 0) {
	// byte[] first = new byte[6];
	// bais.read(first, 0, 6);
	// int sizeOfMsg = calculateLLRPMessageLength(first);
	// byte[] msg = new byte[sizeOfMsg];
	// byte[] rest;
	//
	// rest = new byte[sizeOfMsg - 6];
	// bais.read(rest, 0, sizeOfMsg - 6);
	// System.arraycopy(first, 0, msg, 0, 6);
	// System.arraycopy(rest, 0, msg, 6, rest.length);
	//
	// try {
	// retVal.add(LLRPMessageFactory.createLLRPMessage(msg));
	// } catch (InvalidLLRPMessageException e) {
	// logger.debug("Invalid LLRP message creation", e);
	// }
	// } else {
	// more = false;
	// }
	// }
	//
	// return retVal;
	// }

	/**
	 * 
	 */
	private static byte[] getByteArray(ArrayList<Byte> arg) {
		byte[] retVal = new byte[arg.size()];
		int omg = 0;
		for (Byte b : arg) {
			retVal[omg] = b.byteValue();
			omg++;
		}

		return retVal;
	}

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

}

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

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.llrp.ltk.exceptions.InvalidLLRPMessageException;
import org.llrp.ltk.generated.messages.LLRPMessageFactory;
import org.llrp.ltk.types.LLRPMessage;
import org.rifidi.edge.common.utilities.converter.ByteAndHexConvertingUtility;
import org.rifidi.edge.core.communication.Protocol;

/**
 * @author Matthew Dean - matt@pramari.com
 * 
 */
public class LLRPProtcol extends Protocol {

	/**
	 * The log4j logger.
	 */
	private static final Log logger = LogFactory.getLog(LLRPProtcol.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.communication.buffer.Protocol#fromObject(java.lang.Object)
	 */
	@Override
	public byte[] fromObject(Object arg) {
		LLRPMessage llrpm = (LLRPMessage) arg;
		try {
			return llrpm.encodeBinary();
		} catch (InvalidLLRPMessageException e) {
			logger.debug("Invalid LLRP message conversion", e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.communication.buffer.Protocol#toObject(byte[])
	 */
	@Override
	public List<Object> toObject(byte[] arg) {
		ByteArrayInputStream bais = new ByteArrayInputStream(arg);

		List<Object> retVal = new ArrayList<Object>();
		boolean more = true;
		while (more) {
			if (bais.available() > 0) {
				byte[] first = new byte[6];
				bais.read(first, 0, 6);
				int sizeOfMsg = calculateLLRPMessageLength(first);
				byte[] msg = new byte[sizeOfMsg];
				byte[] rest;

				rest = new byte[sizeOfMsg - 6];
				bais.read(rest, 0, sizeOfMsg - 6);
				System.arraycopy(first, 0, msg, 0, 6);
				System.arraycopy(rest, 0, msg, 6, rest.length);

				try {
					retVal.add(LLRPMessageFactory.createLLRPMessage(msg));
				} catch (InvalidLLRPMessageException e) {
					logger.debug("Invalid LLRP message creation", e);
				}
			} else {
				more = false;
			}
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

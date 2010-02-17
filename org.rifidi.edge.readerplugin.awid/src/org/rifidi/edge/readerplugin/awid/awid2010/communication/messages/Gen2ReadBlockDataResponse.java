/*
 * Gen2ReadBlockDataResponse.java
 * 
 * Created:     Feb 15th, 2010
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:     The software in this package is published under the terms of the EPL License
 *                   A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.readerplugin.awid.awid2010.communication.messages;

import java.math.BigInteger;
import java.util.Arrays;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.services.notification.data.EPCGeneration2Event;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;
import org.rifidi.edge.readerplugin.awid.awid2010.communication.TagResponseMessage;

/**
 * This is a Response message from the Awid that contains a single Gen2 Tag. It
 * assumes that the antenna source is enabled.
 * 
 * TODO: this class needs to be cleaned up so that it puts the correct number of
 * bytes into the memory banks of the EPCGeneration2Event
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * @author Daniel Gómez - dgomez@idlinksolutions.com
 * 
 */
public class Gen2ReadBlockDataResponse extends AbstractAwidMessage implements
		TagResponseMessage {

	/** The timestamp of this tag read */
	private final long timestamp;
	/** The ID this tag was seen on */
	private final String readerID;
	/** The memorybank id. */
	private int memorybank;
	private final static Log logger = LogFactory
			.getLog(Gen2ReadBlockDataResponse.class);

	public Gen2ReadBlockDataResponse(byte[] rawmessage, int memorybank,
			String readerID) {

		super(rawmessage);
		this.timestamp = System.currentTimeMillis();
		this.readerID = readerID;
		this.memorybank = memorybank;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.readerplugin.awid.awid2010.communication.TagResponseMessage
	 * #getTagReadEvent() 16 20 1e 30 00 00 00 00 00 00 00 00 00 00 01 64 52 87
	 * c4 02 68 bd
	 */
	@Override
	public TagReadEvent getTagReadEvent() {
		EPCGeneration2Event gen2Event = new EPCGeneration2Event();
		byte[] shiftedmessage = new byte[super.rawmessage.length];
		BigInteger bi = new BigInteger(super.rawmessage);
		BigInteger bi2 = bi.shiftLeft(1);

		shiftedmessage = bi2.toByteArray();
		if (memorybank == 0) {
			logger.debug("Reserved Memory Bank"
					+ new String(Hex.encodeHex(shiftedmessage)));
			//TODO: put bytes in reserved mem bank
		} else if (memorybank == 1) {
			logger.debug("EPC Memory Bank"
					+ new String(Hex.encodeHex(shiftedmessage)));
			//TODO: what if we don't have 96 bits?
			gen2Event.setEPCMemory(new BigInteger(Arrays.copyOfRange(
					shiftedmessage, 7, 19)), 12 * 8);
		} else if (memorybank == 2) {
			logger.debug("TID Memory Bank "
					+ new String(Hex.encodeHex(shiftedmessage)));

			// TODO: this is not quite correct since it assumes that we have 96
			// bits
			gen2Event.setTIDMemory(new BigInteger(Arrays.copyOfRange(
					shiftedmessage, 3, 17)), 12 * 8);
		} else if (memorybank == 3) {
			logger.debug("User Memory Bank"
					+ new String(Hex.encodeHex(shiftedmessage)));
			// TODO: insert into user memory
		}

		TagReadEvent tre;
		if (super.rawmessage.length == 22) {
			logger.debug("AntennaID " + super.rawmessage[19]);
			tre = new TagReadEvent(readerID, gen2Event, super.rawmessage[19],
					timestamp);
		} else {
			logger.warn("No Antenna byte, assuming default value of " + 1);
			tre = new TagReadEvent(readerID, gen2Event, 1, timestamp);
		}
		return tre;
	}
}

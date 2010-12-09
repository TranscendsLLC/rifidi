/*
 * Gen2PortalIDResponse.java
 * 
 * Created:     Oct 20th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:     The software in this package is published under the terms of the EPL License
 *                   A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.adapter.awid.awid2010.communication.messages;

import java.math.BigInteger;
import java.util.Arrays;

import org.rifidi.edge.adapter.awid.awid2010.communication.TagResponseMessage;
import org.rifidi.edge.notification.EPCGeneration2Event;
import org.rifidi.edge.notification.TagReadEvent;

/**
 * This is a Response message from the Awid that contains a single Gen2 Tag. It
 * assumes that the antenna source is enabled.
 * 
 * The response from the AWID reader looks like this:
 * 
 * 15 20 1E 30 00 00 01 08 15 80 00 80 04 28 19 53 88 yy yy 01 xx xx
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class Gen2PortalIDResponse extends AbstractAwidMessage implements
		TagResponseMessage {

	/** The timestamp of this tag read */
	private final long timestamp;
	/** The ID this tag was seen on */
	private final String readerID;
	private boolean includesAntennaByte;

	public Gen2PortalIDResponse(byte[] rawmessage, String readerID,
			boolean includesAntennaByte) {
		super(rawmessage);
		this.timestamp = System.currentTimeMillis();
		this.readerID = readerID;
		this.includesAntennaByte = includesAntennaByte;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.adapter.awid.awid2010.communication.TagResponseMessage
	 * #getTagReadEvent()
	 */
	@Override
	public TagReadEvent getTagReadEvent() {
		EPCGeneration2Event gen2Event = new EPCGeneration2Event();
		int messageLength = super.rawmessage[0];
		int numIDBytes = messageLength - (9 + (includesAntennaByte ? 1 : 0));

		// copy the tag id payload, ignoring the PC bytes.
		gen2Event.setEPCMemory(new BigInteger(Arrays.copyOfRange(
				super.rawmessage, 5, numIDBytes + 5)), numIDBytes * 8);
		TagReadEvent tre;
		if (includesAntennaByte) {
			// antenna byte is third from last byte
			tre = new TagReadEvent(readerID, gen2Event,
					super.rawmessage[messageLength - 3], timestamp);
		} else {
			tre = new TagReadEvent(readerID, gen2Event, 1, timestamp);
		}
		return tre;
	}

}

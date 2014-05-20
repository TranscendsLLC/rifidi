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
		BigInteger epc = new BigInteger(Arrays.copyOfRange(
				super.rawmessage, 5, numIDBytes + 5));
		gen2Event.setEPCMemory(epc, epc.toString(16), numIDBytes * 8);
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

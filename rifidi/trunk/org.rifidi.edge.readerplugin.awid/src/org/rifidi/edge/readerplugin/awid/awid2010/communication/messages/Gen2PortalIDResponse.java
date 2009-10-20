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
package org.rifidi.edge.readerplugin.awid.awid2010.communication.messages;

import java.math.BigInteger;
import java.util.Arrays;

import org.rifidi.edge.core.services.notification.data.EPCGeneration2Event;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;
import org.rifidi.edge.readerplugin.awid.awid2010.communication.TagResponseMessage;

/**
 * This is a Response message from the Awid that contains a single Gen2 Tag. It
 * assumes that the antenna source is enabled.
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

	public Gen2PortalIDResponse(byte[] rawmessage, String readerID) {
		super(rawmessage);
		this.timestamp = System.currentTimeMillis();
		this.readerID = readerID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.readerplugin.awid.awid2010.communication.TagResponseMessage
	 * #getTagReadEvent()
	 */
	@Override
	public TagReadEvent getTagReadEvent() {
		EPCGeneration2Event gen2Event = new EPCGeneration2Event();
		gen2Event.setEPCMemory(new BigInteger(Arrays.copyOfRange(
				super.rawmessage, 5, 17)), 12 * 8);
		TagReadEvent tre = new TagReadEvent(readerID, gen2Event,
				super.rawmessage[17], timestamp);
		return tre;
	}

}

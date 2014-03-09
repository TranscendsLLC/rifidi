/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
/**
 * 
 */
package org.rifidi.edge.adapter.alien.messages;

import java.math.BigInteger;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.rifidi.edge.notification.DatacontainerEvent;
import org.rifidi.edge.notification.EPCGeneration1Event;
import org.rifidi.edge.notification.EPCGeneration2Event;
import org.rifidi.edge.notification.TagReadEvent;

/**
 * This is a factory to create new TagReadEvent objects from AlienTag objects
 * for a single reader.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AlienTagReadEventFactory {

	/** The reader this factory is used for */
	private String readerID;

	/**
	 * Constructor
	 * 
	 * @param readrID
	 *            The FACTORY_ID of the reader this factory is used for
	 */
	public AlienTagReadEventFactory(String readerID) {
		this.readerID = readerID;
	}

	/**
	 * Convert an AlienTag into a TagReadEvent. This method is threadsafe.
	 * 
	 * @param alienTag
	 *            The tag to convert
	 * @return
	 */
	public TagReadEvent getTagReadEvent(AlienTag alienTag) {
		// the new event
		DatacontainerEvent tagData = null;
		// a big integer representation of the epc
		BigInteger epc = null;
		try {
			epc = new BigInteger(Hex.decodeHex(alienTag.getId_hex()
					.toCharArray()));
		} catch (DecoderException e) {
			throw new RuntimeException("Cannot decode tag: "
					+ alienTag.getId_hex());
		}
		int numbits = alienTag.getId_hex().length() * 4;

		// choose whether to make a gen1 or a gen2 tag
		if (alienTag.getProtocol() == 1) {
			EPCGeneration1Event gen1event = new EPCGeneration1Event();
			// make some wild guesses on the length of the epc field
			gen1event.setEPCMemory(epc, numbits);
			tagData = gen1event;
		} else {
			EPCGeneration2Event gen2event = new EPCGeneration2Event();
			gen2event.setEPCMemory(epc, numbits);
			tagData = gen2event;
		}
		TagReadEvent retVal = new TagReadEvent(readerID, tagData, alienTag
				.getAntenna(), alienTag.getLastSeenDate().getTime());
		if (alienTag.getSpeed() != null) {
			retVal.addExtraInformation(TagReadEvent.SPEED, alienTag.getSpeed());
		}
		if (alienTag.getRssi() != null) {
			retVal.addExtraInformation(TagReadEvent.RSSI, alienTag.getRssi());
//			double rand = (Math.random() * 200);
//			if(this.readerID.equals("Alien_1")) {
//				retVal.addExtraInformation(TagReadEvent.RSSI, 200.0 );
//			} else {
//				retVal.addExtraInformation(TagReadEvent.RSSI, 100.0);
//			}
		}
		if (alienTag.getDirection() != null) {
			retVal.addExtraInformation(TagReadEvent.DIRECTION, alienTag
					.getDirection());
		}
		return retVal;
	}

}

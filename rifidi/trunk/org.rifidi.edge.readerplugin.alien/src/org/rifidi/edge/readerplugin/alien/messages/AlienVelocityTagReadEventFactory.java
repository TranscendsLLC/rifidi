/*
 *  AlienVelocityTagReadEventFactory.java
 *
 *  Created:	Jan 4, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.alien.messages;

import java.math.BigInteger;

import org.rifidi.edge.core.services.notification.data.DatacontainerEvent;
import org.rifidi.edge.core.services.notification.data.EPCGeneration1Event;
import org.rifidi.edge.core.services.notification.data.EPCGeneration2Event;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;

/**
 * 
 * 
 * @author Matthew Dean
 */
public class AlienVelocityTagReadEventFactory {
	/** The reader this factory is used for */
	private String readerID;

	/**
	 * Constructor
	 * 
	 * @param readrID
	 *            The FACTORY_ID of the reader this factory is used for
	 */
	public AlienVelocityTagReadEventFactory(String readerID) {
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
		BigInteger epc = new BigInteger(alienTag.getId_hex(), 16);

		// choose whether to make a gen1 or a gen2 tag
		if (alienTag.getProtocol() == 1) {
			EPCGeneration1Event gen1event = new EPCGeneration1Event();
			// make some wild guesses on the length of the epc field
			if (epc.bitLength() > 96) {
				gen1event.setEPCMemory(epc, 192);
			} else if (epc.bitLength() > 64) {
				gen1event.setEPCMemory(epc, 96);
			} else {
				gen1event.setEPCMemory(epc, 64);
			}
			tagData = gen1event;
		} else {
			EPCGeneration2Event gen2event = new EPCGeneration2Event();
			// make some wild guesses on the length of the epc field
			if (epc.bitLength() > 96) {
				gen2event.setEPCMemory(epc, 192);
			} else if (epc.bitLength() > 64) {
				gen2event.setEPCMemory(epc, 96);
			} else {
				gen2event.setEPCMemory(epc, 64);
			}
			tagData = gen2event;
		}
		TagReadEvent retVal = new TagReadEvent(readerID, tagData, alienTag.getAntenna(),
				alienTag.getLastSeenDate().getTime());
		retVal.addExtraInformation(AlienTag.RSSI_ID, alienTag.getRssi());
		retVal.addExtraInformation(AlienTag.SPEED_ID, alienTag.getSpeed());
		retVal.addExtraInformation(AlienTag.DIRECTION, alienTag.getDirection());
		return retVal;
	}
}

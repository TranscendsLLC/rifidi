/*
 *  OpticonTagHandler.java
 *
 *  Created:	May 12, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.opticon.tags;

import java.util.HashSet;
import java.util.Set;

import org.rifidi.edge.core.services.notification.data.BarcodeTagEvent;
import org.rifidi.edge.core.services.notification.data.ReadCycle;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;

/**
 * This class takes in raw byte data and outputs a "ReadCycle" object which will
 * eventually go to JMS.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class OpticonTagHandler {
	/**
	 * The ID of the reader.
	 */
	private String readerID;

	/**
	 * Constructor.
	 * 
	 * @param readerID
	 */
	public OpticonTagHandler(String readerID) {
		this.readerID = readerID;
	}

	/**
	 * Convert the tag we receive into a ReadCycle that will be processed by
	 * Rifidi Edge.
	 * 
	 * @param tag
	 * @return
	 */
	public ReadCycle processTag(byte[] tag) {
		// TODO: We may need to modify this to handle the tag data.
		StringBuilder sb = new StringBuilder();
		for (byte b : tag) {
			sb.append((char) b);
		}

		BarcodeTagEvent barcodetag = new BarcodeTagEvent();
		barcodetag.setBarcode(tag);

		TagReadEvent tre = new TagReadEvent(this.readerID, barcodetag, 0,
				System.currentTimeMillis());
		Set<TagReadEvent> events = new HashSet<TagReadEvent>();
		events.add(tre);
		ReadCycle cycle = new ReadCycle(events, this.readerID, System
				.currentTimeMillis());
		return cycle;
	}
}

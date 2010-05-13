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

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import org.rifidi.edge.core.services.notification.data.ReadCycle;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;

/**
 * 
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class OpticonTagHandler {
	/**
	 * 
	 */
	private String readerID;

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
		
		System.out.println("Processing the tag!  ");
		
		
		
		// We need every byte but the first one, which is an unprintable
		// character.
		
		StringBuilder sb = new StringBuilder();
		for (byte b : tag) {
			System.out.print(b + " ");
			sb.append((char) b);
		}
		System.out.println();
		
		BigInteger epc = new BigInteger(sb.toString());
		OpticonTagEvent barcodetag = new OpticonTagEvent();

		barcodetag.setBarcode(epc, 72);

		TagReadEvent tre = new TagReadEvent(this.readerID, barcodetag, 0,
				System.currentTimeMillis());
		Set<TagReadEvent> events = new HashSet<TagReadEvent>();
		events.add(tre);
		ReadCycle cycle = new ReadCycle(events, this.readerID, System
				.currentTimeMillis());
		
		System.out.println("Returning the cycle");
		
		return cycle;
	}
}

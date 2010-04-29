/*
 *  AmbientBarcodeTagHandler.java
 *
 *  Created:	Apr 28, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.ambient.barcode.tag;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import org.rifidi.edge.core.services.notification.data.ReadCycle;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;

/**
 * @author Matthew Dean - matt@pramari.com
 * 
 */
public class AmbientBarcodeTagHandler {

	/**
	 * 
	 */
	private String readerID;

	public AmbientBarcodeTagHandler(String readerID) {
		this.readerID = readerID;
	}

	public ReadCycle processTag(byte[] tag) {
		// We need every byte but the first one, which is an unprintable
		// character.
		byte[] actualtag = new byte[tag.length - 1];
		for (int i = 1; i < tag.length; i++) {
			actualtag[i - 1] = (byte)(tag[i]);
		}
		
		StringBuilder sb = new StringBuilder();
		for (byte b : actualtag) {
			sb.append((char) b);
		}
		
		BigInteger epc = new BigInteger(sb.toString());
		AmbientBarcodeTagEvent ambienttag = new AmbientBarcodeTagEvent();
		
		ambienttag.setBarcode(epc, 72);
		
		TagReadEvent tre = new TagReadEvent(this.readerID, ambienttag, 0,
				System.currentTimeMillis());
		Set<TagReadEvent> events = new HashSet<TagReadEvent>();
		events.add(tre);
		ReadCycle cycle = new ReadCycle(events, this.readerID, System
				.currentTimeMillis());
		return cycle;
	}
}

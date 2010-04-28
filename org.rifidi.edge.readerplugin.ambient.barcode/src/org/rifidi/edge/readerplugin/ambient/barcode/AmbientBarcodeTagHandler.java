/*
 *  AmbientBarcodeTagHandler.java
 *
 *  Created:	Apr 23, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.ambient.barcode;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.rifidi.edge.core.services.notification.data.ReadCycle;
import org.rifidi.edge.core.services.notification.data.ReadCycleMessageCreator;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;
import org.rifidi.edge.readerplugin.ambient.barcode.tag.AmbientBarcodeTagEvent;
import org.rifidi.edge.readerplugin.ambient.barcode.utilities.ByteAndHexConvertingUtility;

/**
 * 
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class AmbientBarcodeTagHandler {
	private AmbientBarcodeReaderSession session;

	private String readerID;

	/**
	 * 
	 */
	public AmbientBarcodeTagHandler(AmbientBarcodeReaderSession session, String readerID) {
		this.session = session;
		this.readerID = readerID;
	}

	/**
	 * 
	 * @param tagdata
	 */
	public void processTag(List<Byte> tagdata) {
		byte[] byteArray = new byte[tagdata.size()];
		int x = 0;
		for (Byte b : tagdata) {
			byteArray[x] = b;
			x++;
		}

		BigInteger epc = new BigInteger(ByteAndHexConvertingUtility
				.toHexStringNoSpaces(byteArray), 16);
		AmbientBarcodeTagEvent acuraTag = new AmbientBarcodeTagEvent();
		// make some wild guesses on the length of the epc field
		if (epc.bitLength() > 96) {
			acuraTag.setEPCMemory(epc, 192);
		} else if (epc.bitLength() > 64) {
			acuraTag.setEPCMemory(epc, 96);
		} else {
			acuraTag.setEPCMemory(epc, 64);
		}

		TagReadEvent tre = new TagReadEvent(this.readerID, acuraTag, 0, System
				.currentTimeMillis());

		Set<TagReadEvent> events = new HashSet<TagReadEvent>();
		events.add(tre);
		ReadCycle cycle = new ReadCycle(events, this.readerID, System
				.currentTimeMillis());

		this.session.getSensor().send(cycle);
		// System.out.println("Sending tag: " + ByteAndHexConvertingUtility
		// .toHexStringNoSpaces(byteArray));
		this.session.getTemplate().send(
				this.session.getTemplate().getDefaultDestination(),
				new ReadCycleMessageCreator(cycle));
	}
}

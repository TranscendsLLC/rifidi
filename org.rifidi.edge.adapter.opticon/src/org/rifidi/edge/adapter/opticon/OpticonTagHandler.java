/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.adapter.opticon;

import java.util.HashSet;
import java.util.Set;

import org.rifidi.edge.notification.BarcodeTagEvent;
import org.rifidi.edge.notification.ReadCycle;
import org.rifidi.edge.notification.TagReadEvent;

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

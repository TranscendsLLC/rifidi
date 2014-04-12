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

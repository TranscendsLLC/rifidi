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
package org.rifidi.edge.adapter.thinkify50;

import java.math.BigInteger;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.rifidi.edge.notification.EPCGeneration2Event;
import org.rifidi.edge.notification.TagReadEvent;

/**
 * 
 * 
 * @author Matthew Dean - matt@transcends.co
 */
public class Thinkify50TagHandler {
	private Thinkify50SensorSession session;

	private String readerID;

	/**
	 * 
	 * @param session
	 */
	public Thinkify50TagHandler(Thinkify50SensorSession session, String readerID) {
		this.session = session;
		this.readerID = readerID;
	}

	/**
	 * 
	 * @param tag
	 */
	public TagReadEvent tagArrived(String epc, Long time, Float rssi, int count) {
		EPCGeneration2Event gen2event = new EPCGeneration2Event();
		int numbits = epc.length() * 4;
		BigInteger bigint = null;
		try {
			bigint = new BigInteger(Hex.decodeHex(epc.toCharArray()));
		} catch (DecoderException e) {
			throw new RuntimeException("Cannot decode tag: " + epc);
		}
		gen2event.setEPCMemory(bigint, numbits);
		TagReadEvent tre = new TagReadEvent(readerID, gen2event, 1, time);
		tre.addExtraInformation(Thinkify50Constants.RSSI, rssi);
		return tre;
	}

}

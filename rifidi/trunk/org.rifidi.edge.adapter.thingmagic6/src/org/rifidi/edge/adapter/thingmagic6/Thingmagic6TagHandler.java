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
package org.rifidi.edge.adapter.thingmagic6;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.rifidi.edge.notification.DatacontainerEvent;
import org.rifidi.edge.notification.EPCGeneration2Event;
import org.rifidi.edge.notification.ReadCycle;
import org.rifidi.edge.notification.TagReadEvent;

import com.thingmagic.ReadListener;
import com.thingmagic.Reader;
import com.thingmagic.TagReadData;
import com.thingmagic.Gen2.TagData;

/**
 * 
 * 
 * @author Matthew Dean
 */
public class Thingmagic6TagHandler implements ReadListener {

	public Thingmagic6SensorSession session;

	public Thingmagic6TagHandler(Thingmagic6SensorSession session) {
		this.session = session;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.thingmagic.ReadListener#tagRead(com.thingmagic.Reader,
	 * com.thingmagic.TagReadData)
	 */
	@Override
	public void tagRead(Reader arg0, TagReadData arg1) {
		// System.out.println("Reading a tag!");
		// System.out.println(arg1.epcString() + ", "
		// + arg1.getTag().getProtocol());
		this.session.sendReadCycle(createReadCycle(arg1));
	}

	private ReadCycle createReadCycle(TagReadData trd) {
		Set<TagReadEvent> tagreaderevents = new HashSet<TagReadEvent>();
		TagData tag = (TagData) trd.getTag();
		DatacontainerEvent tagData = null;
		// Assuming it is a gen2 at the moment

		int numBits = tag.epcString().length()*4;
		BigInteger epc;
		try {
			epc = new BigInteger(Hex.decodeHex(tag.epcString().toCharArray()));
		} catch (DecoderException e) {
			throw new RuntimeException("Cannot decode ID: " + tag.epcString());
		}

		EPCGeneration2Event gen2event = new EPCGeneration2Event();
		gen2event.setEPCMemory(epc, numBits);
		tagData = gen2event;

		TagReadEvent tagevent = new TagReadEvent(session.getReaderID(), tagData,
				trd.getAntenna(), trd.getTime());
		
		tagreaderevents.add(tagevent);

		return new ReadCycle(tagreaderevents, session.getReaderID(), System
				.currentTimeMillis());
	}
}

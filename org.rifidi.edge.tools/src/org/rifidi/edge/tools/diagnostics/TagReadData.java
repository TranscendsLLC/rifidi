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
package org.rifidi.edge.tools.diagnostics;

import java.math.BigInteger;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.rifidi.edge.notification.EPCGeneration2Event;

/**
 * This object is a factory for generating a TagReadEvent for getting from
 * esper. It is created from three pieces of information:
 * 
 * tagID - a hex string that is the tag ID
 * 
 * readerID - The ID of the reader that 'saw' this tag
 * 
 * antenna - an integer that represents the antenna that 'saw' this tag.
 * 
 * The tags properties file in the data directory contains a comma-separated
 * list of these three variables.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class TagReadData extends AbstractReadData<EPCGeneration2Event>{


	/**
	 * @param tagID
	 * @param readerID
	 * @param antenna
	 */
	public TagReadData(String tagID, String readerID, int antennaID) {
		super(tagID,readerID, antennaID);
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.diagnostics.tags.generator.AbstractReadData#createTag(java.lang.String)
	 */
	@Override
	protected EPCGeneration2Event createTag(String id) {
		EPCGeneration2Event tag = new EPCGeneration2Event();
		try {
			tag.setEPCMemory(
					new BigInteger(Hex.decodeHex(id.toCharArray())), id, 96);
		} catch (DecoderException e) {
			e.printStackTrace();
			tag.setEPCMemory(new BigInteger("0"), id, 96);
		}
		return tag;
	}
	
	



}

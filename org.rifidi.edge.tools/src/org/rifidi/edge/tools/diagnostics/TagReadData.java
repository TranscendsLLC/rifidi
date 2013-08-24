/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
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
					new BigInteger(Hex.decodeHex(id.toCharArray())), 96);
		} catch (DecoderException e) {
			e.printStackTrace();
			tag.setEPCMemory(new BigInteger("0"), 96);
		}
		return tag;
	}
	
	



}

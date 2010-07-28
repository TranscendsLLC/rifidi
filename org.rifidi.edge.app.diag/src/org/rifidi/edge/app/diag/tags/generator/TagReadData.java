/**
 * 
 */
package org.rifidi.edge.app.diag.tags.generator;

import java.math.BigInteger;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.rifidi.edge.core.services.notification.data.EPCGeneration2Event;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;

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
public class TagReadData {
	/** The reader ID that 'saw' this tag */
	private final String readerID;
	/** The antenna that saw this tag */
	private final int antennaID;
	/** The event */
	private final EPCGeneration2Event tag = new EPCGeneration2Event();

	/**
	 * @param tagID
	 * @param readerID
	 * @param antenna
	 */
	public TagReadData(String tagID, String readerID, String antennaID) {
		super();
		this.readerID = readerID;
		this.antennaID = Integer.parseInt(antennaID);
		try {
			tag.setEPCMemory(
					new BigInteger(Hex.decodeHex(tagID.toCharArray())), 96);
		} catch (DecoderException e) {
			e.printStackTrace();
			tag.setEPCMemory(new BigInteger("0"), 96);
		}
	}

	/**
	 * Return the reader this tag was seen on
	 * 
	 * @return
	 */
	public String getReaderID() {
		return readerID;
	}

	/**
	 * Return the TagReadEvent for this tag
	 * 
	 * @return
	 */
	public TagReadEvent getTagReadEvent() {
		return new TagReadEvent(readerID, tag, antennaID, System
				.currentTimeMillis());
	}

}

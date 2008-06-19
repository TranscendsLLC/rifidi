/*
 *  TagRead.java
 *
 *  Created:	Jun 19, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.core.tag;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;

import org.rifidi.edge.common.utilities.converter.ByteAndHexConvertingUtility;

/**
 * @author Matthew Dean - matt@pramari.com
 * 
 */
@XmlRootElement
public class TagRead {

	private byte[] id;
	private long lastSeenTime;
	private JAXBContext context;
	private Marshaller marshaller;

	/**
	 * The ID of the tag
	 * @return Tag ID
	 */
	public byte[] getId() {
		return id;
	}

	/**
	 * Sets the id of the tag
	 * @param id 
	 */
	public void setId(byte[] id) {
		this.id = id;
	}

	/**
	 * Returns the last seen time in milliseconds.
	 * @return The last seen time
	 */
	public long getLastSeenTime() {
		return lastSeenTime;
	}

	/**
	 * The last seen time of the tag.
	 * @param lastSeenTime Time in milliseconds
	 */
	public void setLastSeenTime(long lastSeenTime) {
		this.lastSeenTime = lastSeenTime;
	}

	
	/**
	 * @return The tag in xml format
	 */
	public String toXML() {
		try {
			context = JAXBContext.newInstance(this.getClass());
			marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		} catch (JAXBException e) {
			e.printStackTrace();
			return null;
		}
		StringWriter writer = new StringWriter();
		try {
			marshaller.marshal(this, writer);
		} catch (JAXBException e) {
			e.printStackTrace();
			return null;
		}
		return writer.toString();
	}

	/**
	 * @return The id of this tag in hex format
	 */
	public String toString() {
		return ByteAndHexConvertingUtility.toHexString(id);
	}
}

package org.rifidi.edge.core.readerplugin.messages.impl;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;

import org.rifidi.edge.core.readerplugin.messages.Message;

/**
 * This is a Message containing information about a tag.
 * 
 * @author Andreas Huebner - andreas@pramari.com
 *
 */
@XmlRootElement
public class TagMessage implements Message {

	private byte[] id;
	private long lastSeenTime;

	private Marshaller marshaller;

	/**
	 * Get the ID field of the RFIDTagEvent
	 * 
	 * @return byte array representing the id of the RFIDTag
	 */
	public byte[] getId() {
		return id;
	}

	/**
	 * Set the ID field of a RFIDTagEvent
	 * 
	 * @param id byte array representing the id of the RFIDTag
	 */
	public void setId(byte[] id) {
		this.id = id;
	}

	/**
	 * Get the time stamp the tag was last seen in ms
	 * 
	 * @return time stamp in ms 
	 */
	public long getLastSeenTime() {
		return lastSeenTime;
	}

	/**
	 * Set the time stamp the tag was last seen in ms
	 * 
	 * @param lastSeenTime in ms
	 */
	public void setLastSeenTime(long lastSeenTime) {
		this.lastSeenTime = lastSeenTime;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.messages.Message#toXML()
	 */
	@Override
	public String toXML() {
		if (marshaller == null) {
			try {
				JAXBContext context = JAXBContext.newInstance(this.getClass());
				marshaller = context.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			} catch (JAXBException e) {
				e.printStackTrace();
				return null;
			}
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

}

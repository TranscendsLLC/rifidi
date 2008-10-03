package org.rifidi.edge.core.readerplugin.messages.impl;

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
		return TagMarshaller.getInstance().getXML(this);
	}

}

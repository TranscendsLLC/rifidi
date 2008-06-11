/**
 * 
 */
package org.rifidi.edge.core.tag;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Matthew Dean - matt@pramari.com
 * 
 */
@XmlRootElement
public class TagRead {

	private byte[] id;
	private long lastSeenTime;

	public byte[] getId() {
		return id;
	}

	public void setId(byte[] id) {
		this.id = id;
	}

	public long getLastSeenTime() {
		return lastSeenTime;
	}

	public void setLastSeenTime(long lastSeenTime) {
		this.lastSeenTime = lastSeenTime;
	}

}

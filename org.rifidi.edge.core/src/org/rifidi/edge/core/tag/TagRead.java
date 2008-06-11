/**
 * 
 */
package org.rifidi.edge.core.tag;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;

import org.rifidi.common.utilities.ByteAndHexConvertingUtility;

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

	public String toString() {
		return ByteAndHexConvertingUtility.toHexString(id);
	}
}

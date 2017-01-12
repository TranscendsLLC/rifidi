/**
 * 
 */
package org.rifidi.edge.rest;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * 
 * @author Matthew Dean - matt@transcends.co
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "response")
public class ReaderResponseMessageDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2510040385822992897L;
	
	@XmlElementWrapper(required = true, name="sensors")
	@XmlElement(name = "sensor")
	private List<ReaderNameDTO> readers;
	
	public ReaderResponseMessageDTO() {
		this.readers = new LinkedList<ReaderNameDTO>();
	}

	public List<ReaderNameDTO> getReaders() {
		return readers;
	}

	public void setReaders(List<ReaderNameDTO> readers) {
		this.readers = readers;
	}
	
	public void addReader(ReaderNameDTO readerID) {
		readers.add(readerID);
	}
	
}

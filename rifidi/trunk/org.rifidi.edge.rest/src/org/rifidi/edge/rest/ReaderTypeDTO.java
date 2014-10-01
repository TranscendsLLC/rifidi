/**
 * 
 */
package org.rifidi.edge.rest;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Matthew Dean - matt@transcends.co
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "reader")
public class ReaderTypeDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 289658215644693284L;
	
	@XmlElement(name = "factoryID")
	private String readerType;
	
	@XmlElement(name = "description")
	private String readerDesc;

	public String getReaderDesc() {
		return readerDesc;
	}

	public void setReaderDesc(String readerDesc) {
		this.readerDesc = readerDesc;
	}

	public String getReaderType() {
		return readerType;
	}

	public void setReaderType(String readerType) {
		this.readerType = readerType;
	}
	
	
}

/**
 * 
 */
package org.rifidi.edge.rest;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * 
 * @author Matthew Dean
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "readerfactorymetadata")
public class ReaderFactoryMetadataDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 583704403990760632L;
	
	
	@XmlElementWrapper(required = true, name="properties")
	@XmlElement(name = "property")
	private List<ReaderMetadataDTO> readermetadata;
	
	@XmlElement(name = "readerid")
	private String id;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<ReaderMetadataDTO> getReadermetadata() {
		return readermetadata;
	}

	public void setReadermetadata(List<ReaderMetadataDTO> readermetadata) {
		this.readermetadata = readermetadata;
	}
	
}

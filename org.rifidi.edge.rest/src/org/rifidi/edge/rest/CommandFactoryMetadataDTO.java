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
@XmlRootElement(name = "commandfactorymetadata")
public class CommandFactoryMetadataDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6594760888996696495L;
	
	/**
	 * 
	 */
	@XmlElementWrapper(required = true, name="properties")
	@XmlElement(name = "property")
	private List<CommandMetadataDTO> commandmetadata;

	@XmlElement(name = "id")
	private String id;
	
	@XmlElement(name="readerID")
	private String readerID;
	
	public String getReaderID() {
		return readerID;
	}

	public void setReaderID(String readerID) {
		this.readerID = readerID;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<CommandMetadataDTO> getCommandmetadata() {
		return commandmetadata;
	}

	public void setCommandmetadata(List<CommandMetadataDTO> commandmetadata) {
		this.commandmetadata = commandmetadata;
	}
}

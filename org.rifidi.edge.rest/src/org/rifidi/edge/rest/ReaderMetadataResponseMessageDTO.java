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
 * @author Matthew Dean
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "response")
public class ReaderMetadataResponseMessageDTO implements Serializable {
	
	private static final long serialVersionUID = 8255666531952794219L;
	
	@XmlElementWrapper(required = true, name="readers")
	@XmlElement(name = "reader")
	private List<ReaderFactoryMetadataDTO> readerMetaList;
	
	@XmlElementWrapper(required = true, name="commands")
	@XmlElement(name = "command")
	private List<CommandFactoryMetadataDTO> commandMetaList;

	public List<ReaderFactoryMetadataDTO> getReaderMetaList() {
		return readerMetaList;
	}

	public void setReaderMetaList(List<ReaderFactoryMetadataDTO> readerFactoryMetaList) {
		this.readerMetaList = readerFactoryMetaList;
	}

	public List<CommandFactoryMetadataDTO> getCommandMetaList() {
		return commandMetaList;
	}

	public void setCommandMetaList(List<CommandFactoryMetadataDTO> commandFactoryMetadataList) {
		this.commandMetaList = commandFactoryMetadataList;
	}
	
	
}

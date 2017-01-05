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
 * @author Matthew Dean - matt@transcends.co
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "response")
public class CommandTypesResponseMessageDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7073194007507750696L;
	
	@XmlElementWrapper(required = true, name="commands")
	@XmlElement(name = "command")
	private List<CommandTypeDTO> commands;

	public List<CommandTypeDTO> getCommands() {
		return commands;
	}

	public void setCommands(List<CommandTypeDTO> commands) {
		this.commands = commands;
	}
	
	
}

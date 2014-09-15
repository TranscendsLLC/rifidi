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
@XmlRootElement(name = "Response")
public class CommandResponseMessageDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5025125865015126988L;
	
	@XmlElementWrapper(required = true, name="Commands")
	@XmlElement(name = "Command")
	private List<CommandNameDTO> commands;

	public List<CommandNameDTO> getCommands() {
		return commands;
	}

	public void setCommands(List<CommandNameDTO> commands) {
		this.commands = commands;
	}
}

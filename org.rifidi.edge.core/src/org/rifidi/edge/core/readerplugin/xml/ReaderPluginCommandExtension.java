package org.rifidi.edge.core.readerplugin.xml;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ReaderPluginCommandExtension {

	private String info;

	private ArrayList<CommandDescription> commandList;

	private ArrayList<CommandDescription> propertyList;

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info.trim();
	}

	@XmlElement(name = "property")
	public ArrayList<CommandDescription> getPropertyList() {
		return propertyList;
	}

	@XmlElement(name = "command")
	public ArrayList<CommandDescription> getCommandList() {
		return commandList;
	}

	public void setCommandList(ArrayList<CommandDescription> commandList) {
		this.commandList = commandList;
	}

	public void setPropertyList(ArrayList<CommandDescription> propertyList) {
		this.propertyList = propertyList;
	}

}

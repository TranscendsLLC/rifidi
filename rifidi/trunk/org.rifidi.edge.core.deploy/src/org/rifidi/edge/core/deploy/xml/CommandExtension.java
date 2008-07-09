package org.rifidi.edge.core.deploy.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CommandExtension {

	private String extendPlugin;

	private List<String> commands;

	public String getExtendPlugin() {
		return extendPlugin;
	}

	public void setExtendPlugin(String extendPlugin) {
		this.extendPlugin = extendPlugin;
	}

	public List<String> getCommands() {
		return commands;
	}

	public void setCommands(List<String> commands) {
		this.commands = commands;
	}

}

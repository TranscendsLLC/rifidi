package org.rifidi.edge.core.readerplugin;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.rifidi.edge.core.readerplugin.commands.Command;

@XmlRootElement
public class ReaderPlugin {

	private ArrayList<CommandDescription> commandList;

	private String connectionManager;

	private String info;

	private String plugin;

	private ArrayList<CommandDescription> propertyList;

	
	public String getConnectionManager() {
		return connectionManager;
	}

	public String getInfo() {
		return info;
	}

	public String getPlugin() {
		return plugin;
	}

	@XmlElement(name="property")	
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

	public void setConnectionManager(String connectionManager) {
		this.connectionManager = connectionManager;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public void setPlugin(String plugin) {
		this.plugin = plugin;
	}

	public void setPropertyList(ArrayList<CommandDescription> propertyList) {
		this.propertyList = propertyList;
	}
	
	
	
	// AUTO Generated Methods

	public List<String> getAvailableCommands() {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeCommand(List<Class<? extends Command>> commands) {
		// TODO Auto-generated method stub
		
	}

	public void addCommand(ArrayList<Class<? extends Command>> commands) {
		// TODO Auto-generated method stub
		
	}

}

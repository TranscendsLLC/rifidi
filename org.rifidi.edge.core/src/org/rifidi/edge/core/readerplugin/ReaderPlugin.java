package org.rifidi.edge.core.readerplugin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager;

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

	// Suggested Commands

	public ConnectionManager newConnectionManager(ReaderInfo readerInfo) {
		try {
			return (ConnectionManager) Class.forName(connectionManager)
					.getConstructor(readerInfo.getClass()).newInstance(
							readerInfo);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// AUTO Generated Methods

	public List<String> getAvailableCommands() {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeCommand(List<CommandDescription> commands) {
		// TODO Auto-generated method stub

	}

	public void addCommand(ArrayList<CommandDescription> commands) {
		// TODO Auto-generated method stub

	}

}

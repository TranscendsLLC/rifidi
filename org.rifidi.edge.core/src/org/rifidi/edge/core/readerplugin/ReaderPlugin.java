package org.rifidi.edge.core.readerplugin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager;
import org.rifidi.edge.core.readerplugin.xml.CommandDescription;
import org.rifidi.edge.core.readerplugin.xml.ReaderPluginXML;

public class ReaderPlugin {

	ReaderPluginXML readerPluginXML;

	public ReaderPlugin(ReaderPluginXML readerPluginXML) {
		this.readerPluginXML = readerPluginXML;
	}

	public CommandDescription getProperty(String propertyName) {
		return getCommand(propertyName, readerPluginXML.getPropertyList());
	}

	public CommandDescription getCommand(String commandName) {
		return getCommand(commandName, readerPluginXML.getCommandList());
	}

	private CommandDescription getCommand(String name,
			List<CommandDescription> commandList) {
		for (CommandDescription desc : commandList) {
			if (desc.getName().equals(name)) {
				return desc;
			}
		}
		return null;
	}

	public ConnectionManager newConnectionManager(ReaderInfo readerInfo) {
		try {
			// TODO: make a constructor with class and superclass
			return (ConnectionManager) Class.forName(
					readerPluginXML.getConnectionManager()).getConstructor(
					readerInfo.getClass().getSuperclass()).newInstance(
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

	public ArrayList<String> getCommands() {
		return getCommands(readerPluginXML.getCommandList());
	}
	
	public ArrayList<String> getProperties() {
		return getCommands(readerPluginXML.getPropertyList());
	}

	// === Get Groups ==
	
	public Collection<String> getCommandGroups() {
		return getGroups(readerPluginXML.getCommandList());
	}

	public Collection<String> getPropertyGroups() {
		return getGroups(readerPluginXML.getPropertyList());
	}
	
	// == Get Commands for Group

	public Collection<String> getCommandsForGroup(String groupName) {
		return getListForGroup(groupName, readerPluginXML.getCommandList());
	}

	public Collection<String> getPropertiesForGroup(String groupName) {
		return getListForGroup(groupName, readerPluginXML.getPropertyList());
	}
	
	// == Helper method for groups

	private Collection<String> getGroups(
			List<CommandDescription> availableCommands) {
		HashSet<String> commandGroups = new HashSet<String>();
		for (CommandDescription desc : availableCommands) {
			for (String s : desc.getGroups()) {
				commandGroups.add(s);
			}
		}

		return commandGroups;
	}

	// == Helper method for get commands in group 
	
	private Collection<String> getListForGroup(String groupName,
			List<CommandDescription> availableCommands) {
		HashSet<String> commandsInGroup = new HashSet<String>();
		for (CommandDescription desc : availableCommands) {
			if (desc.getGroups().contains(groupName)) {
				commandsInGroup.add(desc.getName());
			}
		}
		return commandsInGroup;
	}

	private ArrayList<String> getCommands(ArrayList<CommandDescription> commands) {
		ArrayList<String> names = new ArrayList<String>();
		for (CommandDescription d : commands) {
			names.add(d.getName());
		}
		return names;
	}

}

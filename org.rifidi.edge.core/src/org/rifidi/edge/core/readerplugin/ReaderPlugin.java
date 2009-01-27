package org.rifidi.edge.core.readerplugin;

import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.dynamicswtforms.xml.exceptions.DynamicSWTFormAnnotationException;
import org.rifidi.dynamicswtforms.xml.processor.DynamicSWTFormXMLProcessor;
import org.rifidi.edge.core.api.exceptions.RifidiReaderInfoNotFoundException;
import org.rifidi.edge.core.api.exceptions.RifidiReaderPluginXMLNotFoundException;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager;
import org.rifidi.edge.core.readerplugin.xml.CommandDescription;
import org.rifidi.edge.core.readerplugin.xml.ReaderPluginXML;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;
import org.w3c.dom.Document;

public class ReaderPlugin {

	private ReaderPluginXML readerPluginXML;

	@SuppressWarnings("unused")
	private Log logger = LogFactory.getLog(ReaderPlugin.class);

	private DynamicSWTFormXMLProcessor dynamicSWTFormXMLProcessor;

	public ReaderPlugin(ReaderPluginXML readerPluginXML) {
		this.readerPluginXML = readerPluginXML;
		ServiceRegistry.getInstance().service(this);
	}

	public String getReaderPluginXML()
			throws RifidiReaderPluginXMLNotFoundException {
		try {
			JAXBContext context = JAXBContext
					.newInstance(ReaderPluginXML.class);
			Marshaller m = context.createMarshaller();
			Writer writer = new StringWriter();
			m.marshal(readerPluginXML, writer);
			return writer.toString();

		} catch (JAXBException e) {
			throw new RifidiReaderPluginXMLNotFoundException(e.getMessage());
		}
	}

	public ConnectionManager newConnectionManager(ReaderInfo readerInfo) {
		try {
			// TODO: make a constructor with class and superclass
			// TODO:Add exception and remote stack trace prints
			return (ConnectionManager) Class.forName(
				readerPluginXML.getConnectionManager()).getConstructor(
				readerInfo.getClass().getSuperclass()).newInstance(readerInfo);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Document getReaderInfoAnnotation()
			throws RifidiReaderInfoNotFoundException {

		try {
			Class<?> clazz = Class.forName(this.readerPluginXML.getInfo());
			return dynamicSWTFormXMLProcessor.processAnnotation(clazz);
		} catch (ClassNotFoundException e1) {
			throw new RifidiReaderInfoNotFoundException(e1);
		} catch (DynamicSWTFormAnnotationException e) {
			throw new RifidiReaderInfoNotFoundException(e);
		}

	}

	/*
	 * COMMAND METHODS
	 */

	public void addCommands(List<CommandDescription> commands) {
		if (readerPluginXML.getCommandList() == null) {
			readerPluginXML.setCommandList(new ArrayList<CommandDescription>());
		}
		readerPluginXML.getCommandList().addAll(commands);
	}

	public void removeCommands(List<CommandDescription> commands) {
		readerPluginXML.getCommandList().removeAll(commands);
	}

	public CommandDescription getCommand(String commandName) {
		return getCommand(commandName, readerPluginXML.getCommandList());
	}

	public Collection<String> getCommandGroups() {
		return getGroups(readerPluginXML.getCommandList());
	}

	public ArrayList<String> getCommands() {
		return getCommands(readerPluginXML.getCommandList());
	}

	public Collection<String> getCommandsForGroup(String groupName) {
		return getListForGroup(groupName, readerPluginXML.getCommandList());
	}

	public CommandDescription getProperty(String propertyName) {
		return getCommand(propertyName, readerPluginXML.getPropertyList());
	}

	/*
	 * PROPERTY METHODS
	 */

	public void addProperties(List<CommandDescription> properties) {
		if (readerPluginXML.getPropertyList() == null) {
			readerPluginXML
					.setPropertyList(new ArrayList<CommandDescription>());
		}
		readerPluginXML.getPropertyList().addAll(properties);
	}

	public void removeProperties(List<CommandDescription> properties) {
		readerPluginXML.getPropertyList().removeAll(properties);
	}

	public Collection<String> getPropertyGroups() {
		return getGroups(readerPluginXML.getPropertyList());
	}

	public ArrayList<String> getProperties() {
		return getCommands(readerPluginXML.getPropertyList());
	}

	public Collection<String> getPropertiesForGroup(String groupName) {
		return getListForGroup(groupName, readerPluginXML.getPropertyList());
	}
	
	

	/*
	 * HELPER METHODS
	 */

	private CommandDescription getCommand(String name,
			List<CommandDescription> commandList) {
		for (CommandDescription desc : commandList) {
			if (desc.getName().equals(name)) {
				return desc;
			}
		}
		return null;
	}

	/**
	 * This is a private helper method for getCommands and getProperties
	 * 
	 * @param commands
	 * @return
	 */
	private ArrayList<String> getCommands(ArrayList<CommandDescription> commands) {
		ArrayList<String> names = new ArrayList<String>();
		for (CommandDescription d : commands) {
			names.add(d.getName());
		}
		return names;
	}

	private Collection<String> getGroups(
			List<CommandDescription> availableCommands) {
		HashSet<String> commandGroups = new HashSet<String>();
		if (availableCommands != null) {
			for (CommandDescription desc : availableCommands) {
				if (desc.getGroups() != null) {
					for (String s : desc.getGroups()) {
						commandGroups.add(s);
					}
				}
			}
		}

		return commandGroups;
	}

	// == Helper method for get commands in group

	private Collection<String> getListForGroup(String groupName,
			List<CommandDescription> availableCommands) {
		HashSet<String> commandsInGroup = new HashSet<String>();
		if (availableCommands != null) {
			for (CommandDescription desc : availableCommands) {
				if (desc.getGroups() != null) {
					if (desc.getGroups().contains(groupName)) {
						commandsInGroup.add(desc.getName());
					}
				}
			}
		}
		return commandsInGroup;
	}

	@Inject
	public void setWidgetAnnoationProcessorService(
			DynamicSWTFormXMLProcessor service) {
		dynamicSWTFormXMLProcessor = service;
	}

}

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
import org.rifidi.edge.core.exceptions.RifidiReaderInfoNotFoundException;
import org.rifidi.edge.core.exceptions.RifidiReaderPluginXMLNotFoundException;
import org.rifidi.edge.core.exceptions.RifidiWidgetAnnotationException;
import org.rifidi.edge.core.readerplugin.annotations.service.WidgetAnnotationProcessorService;
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
	
	private WidgetAnnotationProcessorService widgetAnnotationProcessorService;

	public ReaderPlugin(ReaderPluginXML readerPluginXML) {
		this.readerPluginXML = readerPluginXML;
		ServiceRegistry.getInstance().service(this);
	}

	public CommandDescription getProperty(String propertyName) {
		return getCommand(propertyName, readerPluginXML.getPropertyList());
	}
	
	public String getReaderPluginXML() throws RifidiReaderPluginXMLNotFoundException{
		try {
			JAXBContext context = JAXBContext.newInstance(ReaderPluginXML.class);
			Marshaller m = context.createMarshaller();
			Writer writer = new StringWriter();
			m.marshal(readerPluginXML, writer);
			return writer.toString();

		} catch (JAXBException e) {
			throw new RifidiReaderPluginXMLNotFoundException(e.getMessage());
		}
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

	public void addCommands(List<CommandDescription> commands) {
		if (readerPluginXML.getCommandList() == null) {
			readerPluginXML.setCommandList(new ArrayList<CommandDescription>());
		}
		readerPluginXML.getCommandList().addAll(commands);
	}

	public void addProperties(List<CommandDescription> properties) {
		if (readerPluginXML.getPropertyList() == null) {
			readerPluginXML
					.setPropertyList(new ArrayList<CommandDescription>());
		}
		readerPluginXML.getPropertyList().addAll(properties);
	}

	public void removeCommands(List<CommandDescription> commands) {
		readerPluginXML.getCommandList().removeAll(commands);
	}

	public void removeProperties(List<CommandDescription> properties) {
		readerPluginXML.getPropertyList().removeAll(properties);
	}

	public Document getReaderInfoAnnotation()
			throws RifidiReaderInfoNotFoundException {

		try {
			Class<?> clazz = Class.forName(this.readerPluginXML.getInfo());
			return widgetAnnotationProcessorService.processAnnotation(clazz);
		} catch (ClassNotFoundException e1) {
			throw new RifidiReaderInfoNotFoundException(e1);
		} catch (RifidiWidgetAnnotationException e) {
			throw new RifidiReaderInfoNotFoundException(e);
		}

	}
	
	@Inject
	public void setWidgetAnnoationProcessorService(WidgetAnnotationProcessorService service){
		widgetAnnotationProcessorService = service;
	}

}

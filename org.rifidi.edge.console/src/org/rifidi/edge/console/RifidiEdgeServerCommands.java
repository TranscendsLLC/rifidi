/**
 * 
 */
package org.rifidi.edge.console;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.rifidi.edge.core.exceptions.NonExistentCommandFactoryException;
import org.rifidi.edge.core.exceptions.NonExistentReaderConfigurationException;
import org.rifidi.edge.core.readersession.ReaderSessionDAO;
import org.rifidi.edge.core.rmi.CommandConfigurationStub;
import org.rifidi.edge.core.rmi.EdgeServerStub;
import org.rifidi.edge.core.rmi.ReaderConfigurationStub;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class RifidiEdgeServerCommands implements CommandProvider {

	private ReaderSessionDAO readerSessionDAO;
	/** The reader configuration RMI stub */
	private ReaderConfigurationStub readerConfigStub;
	/** The command configuration RMI sstub */
	private CommandConfigurationStub commandConfigStub;
	/** The edge server RMI stub */
	private EdgeServerStub edgeServerStub;

	/**
	 * Set by spring
	 * 
	 * @param commandConfigStub
	 *            the commandConfigStub to set
	 */
	public void setCommandConfigStub(CommandConfigurationStub commandConfigStub) {
		this.commandConfigStub = commandConfigStub;
	}

	/**
	 * set by spring
	 * 
	 * @param readerSessionDAO
	 *            the readerSessionDAO to set
	 */
	public void setReaderSessionManagement(ReaderSessionDAO readerSessionDAO) {
		this.readerSessionDAO = readerSessionDAO;
	}

	/**
	 * set by spring
	 * 
	 * @param readerConfigStub
	 *            the readerConfigStub to set
	 */
	public void setReaderConfigStub(ReaderConfigurationStub readerConfigStub) {
		this.readerConfigStub = readerConfigStub;
	}

	/**
	 * set by spring
	 * 
	 * @param edgeServerStub
	 *            the edgeServerStub to set
	 */
	public void setEdgeServerStub(EdgeServerStub edgeServerStub) {
		this.edgeServerStub = edgeServerStub;
	}

	/**
	 * Get the available reader configuration factory IDs
	 * 
	 * @param intp
	 * @return
	 */
	public Object _readerconfigfactories(CommandInterpreter intp) {
		Set<String> factories;
		try {
			factories = readerConfigStub
					.getAvailableReaderConfigurationFactories();
			Iterator<String> iter = factories.iterator();
			while (iter.hasNext()) {
				intp.println(iter.next());
			}
		} catch (RemoteException e) {
			intp.println("Exception: " + e.getMessage());
		}

		return null;
	}

	public Object _configurereader(CommandInterpreter intp) {
		String id = intp.nextArgument();
		if (id == null) {
			intp
					.println("Format is: configurereader <ReaderConfigurationID>");
		}
		try {
			AttributeList attrs = readerConfigStub.configureReader(id);
			if (attrs == null) {
				intp.println("No reader configuraiton with ID " + id
						+ " is available");
			}

			StringBuilder sb = new StringBuilder();
			sb.append("Reader ConfigurationID: " + id + "\n");
			for (Attribute attr : attrs.asList()) {
				sb.append("\t" + attr.getName() + " - " + attr.getValue()
						+ "\n");
			}
			intp.println(sb.toString());
		} catch (RemoteException e) {
			intp.println(e.getMessage());
		}

		return null;
	}

	/**
	 * Display the list of available reader configurations.
	 * 
	 * @param intp
	 * @return
	 */
	public Object _readers(CommandInterpreter intp) {
		try {
			Map<String, String> readers = readerConfigStub
					.getAvailableReaderConfigurations();
			Iterator<String> iter = readers.keySet().iterator();
			while (iter.hasNext()) {
				String reader = iter.next();
				String type = readers.get(reader);
				intp.println("Reader: " + reader + " Factory: " + type);
			}
		} catch (RemoteException e) {
			intp.println("Exception: " + e.getMessage());
		}

		return null;
	}

	/**
	 * Get a reader description for a particular reader configuration ID
	 * 
	 * @param intp
	 * @return
	 */
	public Object _readerdescription(CommandInterpreter intp) {
		String readerConfigFacID = intp.nextArgument();
		if (readerConfigFacID == null) {
			intp.println("format is: readerdescription "
					+ "<readerConfigurationFactoryID> ");
			return null;
		}
		try {
			MBeanInfo mbeaninfo = readerConfigStub
					.getReaderConfigurationDescription(readerConfigFacID);
			if (mbeaninfo == null) {
				intp.println("No Reader Configuration Factory"
						+ " is found with ID " + readerConfigFacID);
				return null;
			}
			MBeanAttributeInfo[] attrs = mbeaninfo.getAttributes();
			StringBuilder sb = new StringBuilder();
			sb.append("Description for Configurations produced by "
					+ readerConfigFacID + "\n");
			for (MBeanAttributeInfo attr : attrs) {
				String name = attr.getName();
				String description = attr.getDescription();
				sb.append("\t" + name + " : " + description + "\n");
			}
			intp.println(sb.toString());
		} catch (RemoteException e) {
			intp.println(e.getMessage());
		}
		return null;
	}

	/**
	 * Create a reader with a supplied Configuration factory and set of
	 * name-value pairs
	 * 
	 * @param intp
	 * @return
	 */
	public Object _createreader(CommandInterpreter intp) {
		String factoryName = intp.nextArgument();
		if (factoryName == null) {
			intp.println("format is: createreader "
					+ "<readerConfigurationFactoryID> "
					+ "[<propertyName> <propertyValue>...]");
			return null;
		}

		AttributeList attrs = new AttributeList();
		String propName = intp.nextArgument();
		String propValue = intp.nextArgument();
		while (propName != null && propValue != null) {
			Attribute attr = new Attribute(propName, propValue);
			attrs.add(attr);
			propName = intp.nextArgument();
			propValue = intp.nextArgument();
		}
		try {
			String id = readerConfigStub.createReaderConfiguration(factoryName,
					attrs);
			intp.println("New reader configuration created.  ID is : " + id);
		} catch (RemoteException e) {
			intp.println("Exception" + e.getMessage());
		}

		return null;
	}

	/**
	 * Set properties on a reader
	 * 
	 * @param intp
	 * @return
	 */
	public Object _setreaderproperties(CommandInterpreter intp) {
		String readerConfigID = intp.nextArgument();
		if (readerConfigID == null) {
			intp.println("format is setreaderproperties "
					+ "<readerConfigurationID>"
					+ "[<propertyName> <propertyValue>]*");
			return null;
		}

		AttributeList attrs = new AttributeList();
		String propName = intp.nextArgument();
		String propValue = intp.nextArgument();
		while (propName != null && propValue != null) {
			Attribute attr = new Attribute(propName, propValue);
			attrs.add(attr);
			propName = intp.nextArgument();
			propValue = intp.nextArgument();
		}
		try {
			AttributeList retAttrs = readerConfigStub
					.setReaderConfigurationProperties(readerConfigID, attrs);

			if (retAttrs == null) {
				intp.println("Command Configuration with ID " + readerConfigID
						+ " is not available");
			}

			StringBuilder sb = new StringBuilder();
			sb.append("New Properties for reader configuration "
					+ readerConfigID + "\n");
			for (Attribute attr : retAttrs.asList()) {
				sb.append("\t" + attr.getName() + " - " + attr.getValue()
						+ "\n");
			}
			intp.println(sb.toString());
		} catch (RemoteException e) {
			intp.println("Exception" + e.getMessage());
		}

		return null;

	}

	/**
	 * Delete a reader configuration
	 * 
	 * @param intp
	 * @return
	 */
	public Object _deletereader(CommandInterpreter intp) {
		String readerConfigurationID = intp.nextArgument();
		if (readerConfigurationID == null) {
			intp.println("format is: " + "deletereader "
					+ "<ReaderConfigurationID>");
			return null;
		}
		try {
			readerConfigStub.deleteReaderConfiguration(readerConfigurationID);
		} catch (RemoteException e) {
			intp.println(e.getMessage());
		}
		return null;
	}

	/**
	 * Get the properties of a reader configuration
	 * 
	 * @param intp
	 * @return
	 */
	public Object _getreaderproperties(CommandInterpreter intp) {
		String readerID = intp.nextArgument();
		if (readerID == null) {
			intp.println("format is: " + "getreaderproperties"
					+ "<readerconfigID> ");
		}
		try {
			AttributeList attrs = readerConfigStub
					.getReaderConfigurationProperties(readerID);
			if (attrs == null) {
				intp.println("No reader configuration with ID " + readerID
						+ " is avaliable");
				return null;
			}
			StringBuilder sb = new StringBuilder();
			sb.append("Reader ConfigurationID: " + readerID + "\n");
			for (Attribute attr : attrs.asList()) {
				sb.append("\t" + attr.getName() + " - " + attr.getValue()
						+ "\n");
			}
			intp.println(sb.toString());
		} catch (RemoteException e) {
			intp.println(e.getMessage());
		}
		return null;
	}

	public Object _commandtypes(CommandInterpreter intp) {
		try {
			Map<String, String> types = commandConfigStub
					.getCommandConfigurationTypes();
			Iterator<String> iter = types.keySet().iterator();
			StringBuilder sb = new StringBuilder();
			sb.append("Available Command Types: \n");
			while (iter.hasNext()) {
				String type = iter.next();
				String commandConfigFactory = types.get(type);
				sb.append("\t" + type + " - " + commandConfigFactory);
			}
			intp.println(sb.toString());

		} catch (RemoteException e) {
			intp.println(e.getMessage());
		}
		return null;
	}

	public Object _createcommand(CommandInterpreter intp) {
		String factoryType = intp.nextArgument();
		if (factoryType == null) {
			intp.println("format is: createcommand " + "<commandType> "
					+ "[<propertyName> <propertyValue>...]");
			return null;
		}
		AttributeList properties = new AttributeList();
		String attrName = intp.nextArgument();
		String attrValue = intp.nextArgument();
		while (attrName != null && attrValue != null) {
			properties.add(new Attribute(attrName, attrValue));
			attrName = intp.nextArgument();
			attrValue = intp.nextArgument();
		}

		try {
			String ID = this.commandConfigStub.createCommandConfiguration(
					factoryType, properties);
			intp.println("New Command Configuraiton Created.   ID: " + ID);
		} catch (RemoteException e) {
			intp.println(e.getMessage());
		}

		return null;
	}

	/**
	 * Display the list of available commands.
	 * 
	 * @param intp
	 * @return
	 */
	public Object _commands(CommandInterpreter intp) {
		Map<String, String> configurations;
		try {
			configurations = this.commandConfigStub.getCommandConfigurations();
			Iterator<String> iter = configurations.keySet().iterator();
			StringBuilder sb = new StringBuilder();
			while (iter.hasNext()) {
				String commandConfigID = iter.next();
				sb.append("Command: " + commandConfigID + " Type: "
						+ configurations.get(commandConfigID) + "\n");
			}
			intp.println(sb.toString());
		} catch (RemoteException e) {
			intp.println(e.getMessage());
		}

		return null;
	}

	public Object _commanddescription(CommandInterpreter intp) {
		String commandConfigFacID = intp.nextArgument();
		if (commandConfigFacID == null) {
			intp.println("format is: commanddescription "
					+ "<CommandConfigurationType> ");
			return null;
		}
		try {
			MBeanInfo mbeaninfo = commandConfigStub
					.getCommandConfigurationDescription(commandConfigFacID);
			if (mbeaninfo == null) {
				intp.println("No Command Configuration Factory"
						+ " is found with Type " + commandConfigFacID);
				return null;
			}
			MBeanAttributeInfo[] attrs = mbeaninfo.getAttributes();
			StringBuilder sb = new StringBuilder();
			sb.append("Description for Configurations produced by "
					+ commandConfigFacID + "\n");
			for (MBeanAttributeInfo attr : attrs) {
				String name = attr.getName();
				String description = attr.getDescription();
				sb.append("\t" + name + " : " + description + "\n");
			}
			intp.println(sb.toString());
		} catch (RemoteException e) {
			intp.println(e.getMessage());
		}
		return null;
	}

	/**
	 * Get the properties of a reader configuration
	 * 
	 * @param intp
	 * @return
	 */
	public Object _getcommandproperties(CommandInterpreter intp) {
		String commandID = intp.nextArgument();
		if (commandID == null) {
			intp.println("format is: " + "getcommandproperties"
					+ "<commandconfigID> ");
		}
		try {
			AttributeList attrs = commandConfigStub
					.getCommandConfigurationProperties(commandID);
			if (attrs == null) {
				intp.println("No command configuration with ID " + commandID
						+ " is avaliable");
				return null;
			}
			StringBuilder sb = new StringBuilder();
			sb.append("Command ConfigurationID: " + commandID + "\n");
			for (Attribute attr : attrs.asList()) {
				sb.append("\t" + attr.getName() + " - " + attr.getValue()
						+ "\n");
			}
			intp.println(sb.toString());
		} catch (RemoteException e) {
			intp.println(e.getMessage());
		}
		return null;
	}

	/**
	 * Get the properties of a reader configuration
	 * 
	 * @param intp
	 * @return
	 */
	public Object _setcommandproperties(CommandInterpreter intp) {
		String commandConfigID = intp.nextArgument();
		if (commandConfigID == null) {
			intp.println("format is setcommandproperties "
					+ "<commandConfigurationID>"
					+ "[<propertyName> <propertyValue>]*");
			return null;
		}

		AttributeList attrs = new AttributeList();
		String propName = intp.nextArgument();
		String propValue = intp.nextArgument();
		while (propName != null && propValue != null) {
			Attribute attr = new Attribute(propName, propValue);
			attrs.add(attr);
			propName = intp.nextArgument();
			propValue = intp.nextArgument();
		}
		try {
			AttributeList retAttrs = commandConfigStub
					.setCommandConfigurationProperties(commandConfigID, attrs);

			if (retAttrs == null) {
				intp.println("Command Configuration with ID " + commandConfigID
						+ " is not available");
			}

			StringBuilder sb = new StringBuilder();
			sb.append("New Properties for command configuration "
					+ commandConfigID + "\n");
			for (Attribute attr : retAttrs.asList()) {
				sb.append("\t" + attr.getName() + " - " + attr.getValue()
						+ "\n");
			}
			intp.println(sb.toString());
		} catch (RemoteException e) {
			intp.println("Exception" + e.getMessage());
		}

		return null;
	}

	/**
	 * Delete a command configuration
	 * 
	 * @param intp
	 * @return
	 */
	public Object _deletecommand(CommandInterpreter intp) {
		String commandConfigurationID = intp.nextArgument();
		if (commandConfigurationID == null) {
			intp.println("format is: " + "deletecommand "
					+ "<CommandConfigurationID>");
			return null;
		}
		try {
			commandConfigStub
					.deleteCommandConfiguration(commandConfigurationID);
		} catch (RemoteException e) {
			intp.println(e.getMessage());
		}
		return null;
	}

	/**
	 * Create and start a reader session.
	 * 
	 * @param intp
	 * @return
	 */
	public Object _createreadersession(CommandInterpreter intp) {
		String readerName = intp.nextArgument();
		String commandName = intp.nextArgument();
		if (readerName == null || commandName == null) {
			intp
					.println("format is: createreadersession <readername> <commandname>");
			return null;
		}

		try {
			String id = edgeServerStub.startReaderSession(readerName,
					commandName);
			intp.println("Reader Session Started.  ID is: " + id);
		} catch (NonExistentCommandFactoryException e) {
			intp.println("Command " + commandName + " doesn't exist.");
		} catch (NonExistentReaderConfigurationException e) {
			intp.println("Reader " + readerName + " doesn't exist.");
		} catch (RemoteException e) {
			intp.println(e.getMessage());
		}
		return null;
	}

	public Object _save(CommandInterpreter intp) {
		try {
			edgeServerStub.save();
			intp.println("Configuration Saved");
		} catch (RemoteException e) {
			intp.println(e.getMessage());
		}
		return null;
	}

	public Object _stopreadersession(CommandInterpreter intp) {
		String readerSessionName = intp.nextArgument();
		if (readerSessionName == null) {
			intp.println("Format is: stopreadersession <readersessionID>");
			return null;
		}
		try {
			edgeServerStub.stopReaderSession(readerSessionName);
		} catch (RemoteException e) {
			intp.println(e.getMessage());
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.osgi.framework.console.CommandProvider#getHelp()
	 */
	@Override
	public String getHelp() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("---Rifidi Edge Server Commands---\n");
		buffer.append("------Reader Config Comamnds ------\n\t");
		buffer.append("readerconfigfactories - "
				+ "list all reader configuration factories\n\t");
		buffer.append("readers - List all available reader configurations\n\t");
		buffer.append("createreader - create a new reader configuration\n\t");
		buffer.append("deletereader - " + "delete a reader configuration\n\t");
		buffer.append("getreaderproperties - "
				+ "get the properties of a reader configuration\n\t");
		buffer.append("setreaderproperties - "
				+ "set the properties of a reader configuration\n\t");
		buffer.append("readerdescription - "
				+ "get the names of all properties in a configuration\n\t");
		buffer.append("configurereader - "
				+ "Make a connection to reader and set and get properties\n");
		buffer.append("------Command Config Comamnds ------\n\t");
		buffer.append("commandtypes - List all command types\n\t");
		buffer.append("commands - "
				+ "List all available command configurations\n\t");
		buffer.append("createcommand - create a new command configuration\n\t");
		buffer.append("deletecommand - delete a command configuration\n\t");
		buffer
				.append("getcommandproperties - get the properties of a command configuration\n\t");
		buffer
				.append("setcommandproperties - set the properties of a command configuration\n\t");
		buffer
				.append("commanddesription - get the names of all properties in a command configuraiton\n");
		buffer.append("------Reader Session Commands------\n\t");
		buffer
				.append("createreadersession - create and start readersession\n\t");
		buffer.append("stopreadersession - stop a reader session\n");
		buffer.append("------Edge Server Commands------\n\t");
		buffer.append("save - save configuraiton file\n");
		return buffer.toString();
	}
}

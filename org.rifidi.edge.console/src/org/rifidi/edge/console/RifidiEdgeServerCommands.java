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
import org.rifidi.edge.core.commands.CommandConfiguration;
import org.rifidi.edge.core.exceptions.NonExistentCommandFactoryException;
import org.rifidi.edge.core.exceptions.NonExistentReaderConfigurationException;
import org.rifidi.edge.core.readers.AbstractReaderConfiguration;
import org.rifidi.edge.core.readersession.ReaderSessionDAO;
import org.rifidi.edge.core.rmi.ReaderConfigurationStub;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class RifidiEdgeServerCommands implements CommandProvider {

	private ReaderSessionDAO readerSessionDAO;
	/** The reader configuration RMI stub */
	private ReaderConfigurationStub readerConfigStub;

	/**
	 * @param readerSessionDAO
	 *            the readerSessionDAO to set
	 */
	public void setReaderSessionManagement(ReaderSessionDAO readerSessionDAO) {
		this.readerSessionDAO = readerSessionDAO;
	}

	/**
	 * @param readerConfigStub
	 *            the readerConfigStub to set
	 */
	public void setReaderConfigStub(ReaderConfigurationStub readerConfigStub) {
		this.readerConfigStub = readerConfigStub;
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
					+ "[<popertyName> <propertyValue>...]");
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

			StringBuilder sb = new StringBuilder();
			sb.append("New Properties for reader configuration "+readerConfigID+"\n");
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
			intp
					.println("format is: deletereader "
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

	/**
	 * Display the list of available commands.
	 * 
	 * @param intp
	 * @return
	 */
	public Object _commands(CommandInterpreter intp) {
		// for (CommandConfiguration<?> command : readerSessionDAO
		// .getAvailableCommandFactories()) {
		// intp.println(command.getCommandName() + ": "
		// + command.getCommandDescription());
		// }
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
		AbstractReaderConfiguration<?> reader = null;
		CommandConfiguration<?> command = null;
		try {
			readerSessionDAO.createAndStartReaderSession(readerName,
					commandName);
		} catch (NonExistentCommandFactoryException e) {
			intp.println("Reader " + readerName + " doesn't exist.");
		} catch (NonExistentReaderConfigurationException e) {
			intp.println("Command " + readerName + " doesn't exist.");
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
		buffer.append("---Rifidi Edge Server Commands---\n\t");
		buffer.append("readerconfigfactories - "
				+ "list all reader configuration factories\n\t");
		buffer.append("createreader - create a new reader configuration\n\t");
		buffer.append("deletereader - " + "delete a reader configuration\n\t");
		buffer.append("getreaderproperties - "
				+ "get the properties of a reader configuration\n\t");
		buffer.append("setreaderproperties - "
				+ "set the properties of a reader configuration\n\t");
		buffer.append("readerdescription - "
				+ "get the names of all properties in a configuration\n\t");
		buffer.append("readers - List all available reader configurations\n\t");
		buffer.append("commands - "
				+ "List all available command configurations\n\t");
		buffer.append("createreadersession - create and start readersession\n");
		return buffer.toString();
	}

}

/**
 * 
 */
package org.rifidi.edge.console;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.rifidi.edge.core.commands.CommandConfiguration;
import org.rifidi.edge.core.exceptions.NonExistentCommandFactoryException;
import org.rifidi.edge.core.exceptions.NonExistentReaderConfigurationException;
import org.rifidi.edge.core.readers.ReaderConfiguration;
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

	public Object _readerConfigFactories(CommandInterpreter intp) {
		Set<String> factories;
		try {
			factories = readerConfigStub
					.getAvailableReaderConfigurationFactories();
			Iterator<String> iter = factories.iterator();
			while (iter.hasNext()) {
				intp.println(iter.next());
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Display the list of available readers.
	 * 
	 * @param intp
	 * @return
	 */
	public Object _readers(CommandInterpreter intp) {
		// for (ReaderConfiguration<?> reader : readerSessionDAO
		// .getAvailableReaderConfigurations()) {
		// intp.println(reader.getName() + ": " + reader.getDescription());
		// }
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
		ReaderConfiguration<?> reader = null;
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
		buffer.append("readers - God mode\n\t");
		buffer.append("commands - God mode\n\t");
		buffer.append("createreadersession - create and start readersession\n");
		return buffer.toString();
	}

}

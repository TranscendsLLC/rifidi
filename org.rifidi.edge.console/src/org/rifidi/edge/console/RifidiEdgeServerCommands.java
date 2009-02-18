/**
 * 
 */
package org.rifidi.edge.console;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.rifidi.edge.newcore.commands.CommandFactory;
import org.rifidi.edge.newcore.exceptions.NonExistentCommandFactoryException;
import org.rifidi.edge.newcore.exceptions.NonExistentReaderConfigurationException;
import org.rifidi.edge.newcore.readers.ReaderConfiguration;
import org.rifidi.edge.newcore.readersession.ReaderSessionManagement;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class RifidiEdgeServerCommands implements CommandProvider {

	private ReaderSessionManagement readerSessionManagement;

	/**
	 * @param readerSessionManagement
	 *            the readerSessionManagement to set
	 */
	public void setReaderSessionManagement(
			ReaderSessionManagement readerSessionManagement) {
		this.readerSessionManagement = readerSessionManagement;
	}

	/**
	 * Display the list of available readers.
	 * 
	 * @param intp
	 * @return
	 */
	public Object _readers(CommandInterpreter intp) {
//		for (ReaderConfiguration<?> reader : readerSessionManagement
//				.getAvailableReaderConfigurations()) {
//			intp.println(reader.getName() + ": " + reader.getDescription());
//		}
		return null;
	}

	/**
	 * Display the list of available commands.
	 * 
	 * @param intp
	 * @return
	 */
	public Object _commands(CommandInterpreter intp) {
//		for (CommandFactory<?> command : readerSessionManagement
//				.getAvailableCommandFactories()) {
//			intp.println(command.getCommandName() + ": "
//					+ command.getCommandDescription());
//		}
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
			intp.println("format is: createreadersession <readername> <commandname>");
			return null;
		}
		ReaderConfiguration<?> reader = null;
		CommandFactory<?> command = null;
		try {
			readerSessionManagement.createAndStartReaderSession(readerName, commandName);
		} catch (NonExistentCommandFactoryException e) {
			intp.println("Reader "+readerName+" doesn't exist.");
		} catch (NonExistentReaderConfigurationException e) {
			intp.println("Command "+readerName+" doesn't exist.");
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

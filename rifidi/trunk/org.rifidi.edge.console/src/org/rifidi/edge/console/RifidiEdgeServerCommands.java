/**
 * 
 */
package org.rifidi.edge.console;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.rifidi.edge.newcore.commands.CommandFactory;
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
		for (ReaderConfiguration<?> reader : readerSessionManagement
				.getAvailableReaderConfigurations()) {
			intp.println(reader.getName() + ": " + reader.getDescription());
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
		for (CommandFactory<?> command : readerSessionManagement
				.getAvailableCommandFactories()) {
			intp.println(command.getCommandName() + ": "
					+ command.getCommandDescription());
		}
		return null;
	}

	/**
	 * Create and start a reader session.
	 * 
	 * @param intp
	 * @return
	 */
	public Object _rs(CommandInterpreter intp) {
		String readerName = intp.nextArgument();
		String commandName = intp.nextArgument();
		if (readerName == null || commandName == null) {
			intp.println("format is: rs <readername> <commandname>");
			return null;
		}
		ReaderConfiguration<?> reader = null;
		CommandFactory<?> command = null;
		for (ReaderConfiguration<?> tempReader : readerSessionManagement
				.getAvailableReaderConfigurations()) {
			if (tempReader.getName().equals(readerName)) {
				reader = tempReader;
				break;
			}
		}
		for (CommandFactory<?> tempCommand : readerSessionManagement
				.getAvailableCommandFactories()) {
			if (tempCommand.getCommandName().equals(commandName)) {
				command = tempCommand;
				break;
			}
		}
		readerSessionManagement.createAndStartReaderSession(reader, command);
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
		buffer.append("rs - create and start readersession\n");
		return buffer.toString();
	}

}

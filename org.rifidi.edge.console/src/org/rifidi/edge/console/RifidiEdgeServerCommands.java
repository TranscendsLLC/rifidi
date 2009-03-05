/**
 * 
 */
package org.rifidi.edge.console;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.rifidi.edge.core.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.commands.AbstractCommandConfigurationFactory;
import org.rifidi.edge.core.daos.CommandDAO;
import org.rifidi.edge.core.daos.ReaderDAO;
import org.rifidi.edge.core.readers.AbstractReader;
import org.rifidi.edge.core.readers.AbstractReaderFactory;
import org.rifidi.edge.core.readers.Command;
import org.rifidi.edge.core.readers.ReaderSession;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class RifidiEdgeServerCommands implements CommandProvider {
	/** DAO for accessing readers. */
	private ReaderDAO readerDAO;
	/** DAO for managing commands. */
	private CommandDAO commandDAO;

	/**
	 * @param commandDAO
	 *            the commandDAO to set
	 */
	public void setCommandDAO(CommandDAO commandDAO) {
		this.commandDAO = commandDAO;
	}

	/**
	 * @param readerDAO
	 *            the readerDAO to set
	 */
	public void setReaderDAO(ReaderDAO readerDAO) {
		this.readerDAO = readerDAO;
	}

	// ReaderSession session = null;
	// Command command = null;
	// ScheduledFuture<?> future = null;
	//
	// public Object _tester(CommandInterpreter intp) {
	// String id = intp.nextArgument();
	// AbstractReader<?> reader = readerConfigStub.getReader(id);
	// session = reader.createReaderSession();
	// try {
	// session.connect();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// AbstractCommandConfiguration<?> commandConfig = commandConfigStub
	// .getCommand(intp.nextArgument());
	// command = commandConfig.getCommand();
	// future = session.submit(command, 500, TimeUnit.MILLISECONDS);
	// return null;
	// }
	//
	// public Object _stopit(CommandInterpreter intp) {
	// future.cancel(true);
	// return null;
	// }

	/**
	 * Get the available reader factory IDs
	 * 
	 * @param intp
	 * @return
	 */
	public Object _readertypes(CommandInterpreter intp) {
		Set<AbstractReaderFactory<?>> factories;
		factories = readerDAO.getReaderFactories();
		for (AbstractReaderFactory<?> factory : factories) {
			intp.println(factory.getFactoryIDs().get(0));
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
		for (AbstractReader<?> reader : readerDAO.getReaders()) {
			intp
					.println("ID: " + reader.getID() + " Name: "
							+ reader.getName());
			int count = 0;
			for (ReaderSession session : reader.getReaderSessions()) {
				intp.println("\tsession (" + count + "): " + session);
				Map<Integer, Command> commands = session.currentCommands();
				for (Integer commandId : commands.keySet()) {
					intp.println("\t\tcommand (" + commandId + "): "
							+ commands.get(commandId));
				}
			}
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
		for (AbstractCommandConfiguration<?> command : commandDAO.getCommands()) {
			intp.println("ID: " + command.getID() + " Name: "
					+ command.getCommandName());
		}
		return null;
	}

	/**
	 * Display the list of available command types.
	 * 
	 * @param intp
	 * @return
	 */
	public Object _commandtypes(CommandInterpreter intp) {
		for (AbstractCommandConfigurationFactory factory : commandDAO
				.getCommandFactories()) {
			for (String id : factory.getFactoryIDs()) {
				intp.println("ID: " + id);
			}
		}
		return null;
	}

	/**
	 * Create a new reader session. Takes a reader id as argument.
	 * 
	 * @param intp
	 * @return
	 */
	public Object _createsession(CommandInterpreter intp) {
		String readerid = intp.nextArgument();
		if (readerid == null) {
			intp.println("Give a reader id!");
			return null;
		}
		AbstractReader<?> reader = readerDAO.getReaderByID(readerid);
		ReaderSession session = reader.createReaderSession();
		if (session == null) {
			intp.println("Unable to create session.");
			return null;
		}
		intp.println("Session created.");
		return null;
	}

	/**
	 * Create a new reader session. Takes a reader id as argument.
	 * 
	 * @param intp
	 * @return
	 */
	public Object _startsession(CommandInterpreter intp) {
		String readerid = intp.nextArgument();
		String sessionid = intp.nextArgument();
		if (readerid == null) {
			intp.println("Give a reader id!");
			return null;
		}
		if (sessionid == null) {
			intp.println("Give a session id!");
			return null;
		}
		AbstractReader<?> reader = readerDAO.getReaderByID(readerid);
		if (reader == null) {
			intp.println("Non existent reader id!");
			return null;
		}
		try {
			List<ReaderSession> sessions = reader.getReaderSessions();
			if (sessions.size() - 1 < Integer.parseInt(sessionid)) {
				intp.println("Non existent session id.");
				return null;
			}
			ReaderSession session = sessions.get(Integer.parseInt(sessionid));
			session.connect();
		} catch (NumberFormatException e) {
			intp.println("Session id not an integer.");
			return null;
		} catch (IOException e) {
			intp.println("Unable to connect: " + e);
			return null;
		}
		intp.println("Session started.");
		return null;
	}

	/**
	 * Execute a command in a given session.
	 * 
	 * @param intp
	 * @return
	 */
	public Object _executecommand(CommandInterpreter intp) {
		String readerid = intp.nextArgument();
		String sessionid = intp.nextArgument();
		String commandid = intp.nextArgument();
		String interval = intp.nextArgument();
		if (readerid == null || sessionid == null || commandid == null
				|| interval == null) {
			intp
					.println("Please use the following command format: executecommand <readerid> <sessionid> <commandid> <interval>");
			return null;
		}
		AbstractReader<?> reader = readerDAO.getReaderByID(readerid);
		if (reader == null) {
			intp.println("Non existent reader id.");
			return null;
		}
		AbstractCommandConfiguration<?> command = commandDAO
				.getCommandByID(commandid);
		if (command == null) {
			intp.println("Non existent command id.");
			return null;
		}
		List<ReaderSession> sessions = reader.getReaderSessions();
		try {
			if (sessions.size() - 1 >= Integer.parseInt(sessionid)) {
				ReaderSession session = reader.getReaderSessions().get(
						Integer.parseInt(sessionid));
				Long ival = Long.parseLong(interval);
				if (ival > 0) {
					session.submit(command.getCommand(), ival,
							TimeUnit.MILLISECONDS);
				} else {
					session.submit(command.getCommand());
				}
			}
		} catch (NumberFormatException e) {
			intp.println("Session id or interval is not a number.");
			return null;
		}
		intp.println("Command submitted.");
		return null;
	}

	/**
	 * Kill a command.
	 * 
	 * @param intp
	 * @return
	 */
	public Object _killcommand(CommandInterpreter intp) {
		String readerid = intp.nextArgument();
		String sessionid = intp.nextArgument();
		String commandid = intp.nextArgument();
		if (readerid == null || sessionid == null || commandid == null) {
			intp
					.println("Please use the following command format: killcommand <readerid> <sessionid> <commandid>");
			return null;
		}
		AbstractReader<?> reader = readerDAO.getReaderByID(readerid);
		List<ReaderSession> sessions = reader.getReaderSessions();
		try {
			if (sessions.size() - 1 >= Integer.parseInt(sessionid)) {
				ReaderSession session = reader.getReaderSessions().get(
						Integer.parseInt(sessionid));
				session.killComand(Integer.parseInt(commandid));
			}
		} catch (NumberFormatException e) {
			intp.println("Session id or command id is not a number.");
			return null;
		}
		intp.println("Command killed.");
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
		buffer.append("----List Commands----\n");
		buffer
				.append("\treadertypes - get the list of available reader types\n");
		buffer.append("\treaders - get the list of configured readers\n");
		buffer
				.append("\tcommandtypes - get the list of available command types\n");
		buffer.append("\tcommands - get the list of configured commands\n");
		buffer.append("----Management Commands----\n");
		buffer
				.append("\tcreatesession <readerid> - create a new session on the given reader\n");
		buffer
				.append("\texecutecommand <readerid> <sessionid> <commandid>  <interval>- execute a command in a session\n");
		buffer
				.append("\tkillcommand <readerid> <sessionid> <commandid> - execute a command in a session\n");
		return buffer.toString();
	}
}

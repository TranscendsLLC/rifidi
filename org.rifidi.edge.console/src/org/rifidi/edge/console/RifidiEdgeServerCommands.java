/*
 * 
 * RifidiEdgeServerCommands.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
package org.rifidi.edge.console;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.management.Attribute;
import javax.management.AttributeList;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.rifidi.edge.api.rmi.dto.CommandDTO;
import org.rifidi.edge.core.configuration.Configuration;
import org.rifidi.edge.core.configuration.impl.AbstractCommandConfigurationFactory;
import org.rifidi.edge.core.configuration.services.ConfigurationService;
import org.rifidi.edge.core.daos.CommandDAO;
import org.rifidi.edge.core.daos.ReaderDAO;
import org.rifidi.edge.core.exceptions.CannotCreateServiceException;
import org.rifidi.edge.core.exceptions.CannotCreateSessionException;
import org.rifidi.edge.core.sensors.SensorSession;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.base.AbstractSensorFactory;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.sensors.exceptions.CannotDestroySensorException;
import org.rifidi.edge.core.services.logging.LoggingService;

/**
 * Command line commands for the edge server.
 * 
 * @author Jochen Mader - jochen@pramari.com
 */
public class RifidiEdgeServerCommands implements CommandProvider {
	/** DAO for accessing readers. */
	private volatile ReaderDAO readerDAO;
	/** DAO for managing commands. */
	private volatile CommandDAO commandDAO;
	/** Configuration Service */
	private volatile ConfigurationService configService;
	/** Service that allows you to change logging level at runtime */
	private volatile LoggingService loggingService;

	/**
	 * Sets the configuration service for this class.
	 * 
	 * @param configService
	 *            the configService to set
	 */
	public void setConfigService(ConfigurationService configService) {
		this.configService = configService;
	}

	/**
	 * Called by spring to inject the CommandDAO
	 * 
	 * @param commandDAO
	 *            the commandDAO to set
	 */
	public void setCommandDAO(CommandDAO commandDAO) {
		this.commandDAO = commandDAO;
	}

	/**
	 * Called by spring to inject the ReaderDAO
	 * 
	 * @param readerDAO
	 *            the readerDAO to set
	 */
	public void setReaderDAO(ReaderDAO readerDAO) {
		this.readerDAO = readerDAO;
	}

	/**
	 * Called by spring to inject LoggingService
	 * 
	 * @param loggingService
	 */
	public void setLoggingService(LoggingService loggingService) {
		this.loggingService = loggingService;
	}

	/**
	 * Get the available reader factory IDs
	 * 
	 * @param intp
	 * @return
	 */
	public Object _readertypes(CommandInterpreter intp) {
		Set<AbstractSensorFactory<?>> factories;
		factories = readerDAO.getReaderFactories();
		for (AbstractSensorFactory<?> factory : factories) {
			intp.println(factory.getFactoryID());
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
		for (AbstractSensor<?> reader : readerDAO.getReaders()) {
			intp.println("ID: " + reader.getID());
			for (SensorSession session : reader.getSensorSessions().values()) {
				intp.println("\tsession (" + session.getID() + "): " + session);
				List<CommandDTO> commands = session.getCommands();
				for (CommandDTO dto : commands) {
					String type = null;
					if (dto.getRepeat()) {
						type = "Recurring Command";
					} else {
						type = "Single-Shot Command";
					}
					intp.println("\t\t" + type + "(" + dto.getProcessID()
							+ "): " + dto.getCommandID());
				}
			}
		}
		return null;
	}

	/**
	 * List the available configurations
	 * 
	 * @param intp
	 * @return
	 */
	public Object _configurations(CommandInterpreter intp) {
		for (Configuration config : configService.getConfigurations()) {
			intp.println("ID: " + config.getServiceID());
		}
		return null;
	}

	/**
	 * Creates a reader with the given arguments.
	 * 
	 * @param intp
	 * @return
	 */
	public Object _createreader(CommandInterpreter intp) {
		String readerFacID = intp.nextArgument();
		if (readerFacID == null) {
			intp.println("Give a ReaderFactoryID");
			return null;
		}
		AbstractSensorFactory<?> factory = this.readerDAO
				.getReaderFactoryByID(readerFacID);
		if (factory == null) {
			intp
					.println("Factory with ID " + readerFacID
							+ " is not available");
			return null;
		}
		AttributeList list = new AttributeList();
		String attrname = intp.nextArgument();
		String attrval = intp.nextArgument();
		while (attrname != null && attrval != null) {
			list.add(new Attribute(attrname, attrval));
			attrname = intp.nextArgument();
			attrval = intp.nextArgument();
		}
		try {
			String id = configService.createService(readerFacID, list);
			intp.println("Reader Created with ID " + id);
		} catch (CannotCreateServiceException e) {
			intp.println("Cannot create reader");
		}

		return null;
	}

	/**
	 * Deletes a reader with the given ID.
	 * 
	 * @param intp
	 * @return
	 */
	public Object _deletereader(CommandInterpreter intp) {
		String readerID = intp.nextArgument();
		if (readerID == null) {
			intp.println("Give a Reader ID");
			return null;
		}
		AbstractSensor<?> reader = this.readerDAO.getReaderByID(readerID);
		if (reader == null) {
			intp.println("No reader with ID " + readerID + " is available");
			return null;
		}
		configService.destroyService(readerID);
		intp.println("Sensor with ID " + readerID + " has been deleted");
		return null;
	}

	/**
	 * Gets the properties of a reader with the given ID.
	 * 
	 * @param intp
	 * @return
	 */
	public Object _getproperties(CommandInterpreter intp) {
		String readerID = intp.nextArgument();
		if (readerID == null) {
			intp.println("Give a ReaderFactoryID");
			return null;
		}
		Configuration configuration = configService.getConfiguration(readerID);
		if (configuration == null) {
			intp.println("Reader with ID " + readerID + " is not available");
			return null;
		}
		Map<String, Object> attrs = configuration.getAttributes();
		intp.println("Properties for Reader " + readerID);
		for (String name : attrs.keySet()) {
			intp.println("\t" + name + " : " + attrs.get(name));
		}
		return null;

	}

	/**
	 * Sets the properties of a reader with the given ID.
	 * 
	 * @param intp
	 * @return
	 */
	public Object _setproperties(CommandInterpreter intp) {
		String readerID = intp.nextArgument();
		if (readerID == null) {
			intp.println("Give a ReaderFactoryID");
			return null;
		}
		Configuration configuration = configService.getConfiguration(readerID);
		if (configuration == null) {
			intp.println("Reader with ID " + readerID + " is not available");
			return null;
		}
		AttributeList list = new AttributeList();
		String attrname = intp.nextArgument();
		String attrval = intp.nextArgument();
		while (attrname != null && attrval != null) {
			list.add(new Attribute(attrname, attrval));
			attrname = intp.nextArgument();
			attrval = intp.nextArgument();
		}
		list = configuration.setAttributes(list);
		intp.println("Properties for Reader " + readerID);
		for (Attribute a : list.asList()) {
			intp.println("\t" + a.getName() + " : " + a.getValue());
		}
		return null;

	}

	/**
	 * Applies the property changes to a reader with the given ID.
	 * 
	 * @param intp
	 * @return
	 */
	public Object _applypropchanges(CommandInterpreter intp) {
		String readerID = intp.nextArgument();
		if (readerID == null) {
			intp.println("Give a ReaderFactoryID");
			return null;
		}
		AbstractSensor<?> reader = this.readerDAO.getReaderByID(readerID);
		if (reader == null) {
			intp.println("Reader with ID " + readerID + " is not available");
			return null;
		}
		reader.applyPropertyChanges();
		intp.println("Property Changes Applied");
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
			intp.println("ID: " + command.getID());
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
		for (AbstractCommandConfigurationFactory<?> factory : commandDAO
				.getCommandFactories()) {
			intp.println("ID: " + factory.getFactoryID());
		}
		return null;
	}

	/**
	 * Creates a command with the given ID and properties.
	 * 
	 * @param intp
	 * @return
	 */
	public Object _createcommand(CommandInterpreter intp) {
		String commandFacID = intp.nextArgument();
		if (commandFacID == null) {
			intp.println("Give a CommandFactoryID");
			return null;
		}
		AbstractCommandConfigurationFactory<?> factory = this.commandDAO
				.getCommandFactory(commandFacID);
		if (factory == null) {
			intp.println("Factory with ID " + commandFacID
					+ " is not available");
			return null;
		}
		AttributeList list = new AttributeList();
		String attrname = intp.nextArgument();
		String attrval = intp.nextArgument();
		while (attrname != null && attrval != null) {
			list.add(new Attribute(attrname, attrval));
			attrname = intp.nextArgument();
			attrval = intp.nextArgument();
		}
		try {
			String id = configService.createService(commandFacID, list);
			intp.println("Command Configuration Created with ID " + id);
		} catch (CannotCreateServiceException e) {
			intp.println("Cannot create command");
		}
		return null;
	}

	/**
	 * 
	 * 
	 * @param intp
	 * @return
	 */
	public Object _deletecommand(CommandInterpreter intp) {
		String commandID = intp.nextArgument();
		if (commandID == null) {
			intp.println("Please supply a command ID");
			return null;
		}
		AbstractCommandConfiguration<?> command = this.commandDAO
				.getCommandByID(commandID);
		if (command == null) {
			intp.println("No Command with ID " + commandID + " is avaialbe");
			return null;
		}
		this.configService.destroyService(commandID);
		intp.println("Command Configuration with id " + commandID
				+ " has been deleted");
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
		AbstractSensor<?> reader = readerDAO.getReaderByID(readerid);
		try {
			if(reader==null){
				intp.print("No reader with ID " + readerid + " found");
				return null;
			}
			String sessionID = reader.createSensorSession();
			intp.println("Session created: " + readerid + ":" + sessionID);
		} catch (CannotCreateSessionException e) {
			intp.println("Session cannot be created");
		}
		return null;
	}

	/**
	 * Create a new reader session. Takes a reader id as argument.
	 * 
	 * @param intp
	 * @return
	 */
	public Object _deletesession(CommandInterpreter intp) {
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
		AbstractSensor<?> reader = readerDAO.getReaderByID(readerid);
		if (reader == null) {
			intp.println("No reader with ID " + readerid + " is available");
			return null;
		}
		try {
			reader.destroySensorSession(sessionid);
			intp
					.println("Session " + readerid + ":" + sessionid
							+ " destroyed");
		} catch (CannotDestroySensorException e) {
			intp.println("Cannot destroy session");
		}
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
		final AbstractSensor<?> reader = readerDAO.getReaderByID(readerid);
		if (reader == null) {
			intp.println("Non existent reader id!");
			return null;
		}
		final SensorSession session;
		try {
			session = reader.getSensorSessions().get(sessionid);
			if (session == null) {
				intp.println("Non existent session id.");
				return null;
			}
		} catch (NumberFormatException e) {
			intp.println("Session id not an integer.");
			return null;
		}
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					session.connect();
					reader.applyPropertyChanges();
				} catch (IOException e) {
					session.disconnect();
				}
			}
		});
		t.start();
		intp.println("Session " + readerid + ":" + sessionid + " started.");
		return null;
	}

	/**
	 * Create a new reader session. Takes a reader id as argument.
	 * 
	 * @param intp
	 * @return
	 */
	public Object _stopsession(CommandInterpreter intp) {
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
		AbstractSensor<?> reader = readerDAO.getReaderByID(readerid);
		if (reader == null) {
			intp.println("Non existent reader id!");
			return null;
		}
		try {
			SensorSession session = reader.getSensorSessions().get(sessionid);
			if (session == null) {
				intp.println("Non existent session id.");
				return null;
			}
			session.disconnect();
		} catch (NumberFormatException e) {
			intp.println("Session id not an integer.");
			return null;
		}
		intp.println("Session " + readerid + ":" + sessionid + " stopped.");
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
		AbstractSensor<?> reader = readerDAO.getReaderByID(readerid);
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
		SensorSession session = reader.getSensorSessions().get(sessionid);
		try {
			if (session != null) {
				Long ival = Long.parseLong(interval);
				session.submit(commandid, ival, TimeUnit.MILLISECONDS);
			} else {
				intp.print("Session ID not found " + sessionid);
				return null;
			}
		} catch (NumberFormatException e) {
			intp.println("interval is not a number.");
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
		AbstractSensor<?> reader = readerDAO.getReaderByID(readerid);
		SensorSession session = reader.getSensorSessions().get(sessionid);
		try {
			if (session != null) {
				session.killComand(Integer.parseInt(commandid));
			} else {
				intp.println("session ID does not exist");
			}
		} catch (NumberFormatException e) {
			intp.println("command id is not a number.");
			return null;
		}
		intp.println("Command killed.");
		return null;
	}

	/**
	 * Saves the configuration.
	 * 
	 * @param intp
	 * @return
	 */
	public Object _save(CommandInterpreter intp) {
		configService.storeConfiguration();
		intp.println("Configuration Saved!");
		return null;
	}

	public Object _setloglevel(CommandInterpreter intp) {
		String loggerName = intp.nextArgument();
		if (loggerName == null) {
			intp
					.println("Please use the following command format: setloglevel <loggername> <loglevel>");
			return null;
		}
		String loggerLevel = intp.nextArgument();
		if (loggerLevel == null) {
			intp
					.println("Please use the following command format: setloglevel <loggername> <loglevel>");
		}
		try {
			loggingService.setLoggingLevel(loggerName, loggerLevel);
		} catch (IllegalArgumentException e) {
			intp.println("loggerLevel is not valid");
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
		buffer.append("  ----General commands----\n");
		buffer.append("\tsave - save the configuration to a file\n");
		buffer
				.append("\tsetloglevel <loggerName> <logLevel> - Set the log level for a given logger.\n");
		buffer.append("  ----Reader related commands----\n");
		buffer
				.append("\treadertypes - get the list of available reader types\n");
		buffer.append("\treaders - get the list of configured readers\n");
		buffer
				.append("\tcreatereader <readertype> [<propName> <propValue>]* - create a new reader\n");
		buffer.append("\tdeletereader <readerid> - delete a reader\n");
		buffer.append("  ----Command related commands----\n");
		buffer
				.append("\tcommandtypes - get the list of available command types\n");
		buffer.append("\tcommands - get the list of configured commands\n");
		buffer
				.append("\tcreatecommand <commandtype> [<propName> <propValue>]* - create a new command\n");
		buffer.append("\tdeletecommand <commandid> - delete a command\n");
		buffer.append("  ----Configuration related commands----\n");
		buffer.append("\tconfigurations - get the list of configurations\n");
		buffer
				.append("\tgetproperties <id> - get the properties of a configuration\n");
		buffer
				.append("\tsetproperties <id> [<propName> <propValue>]* - set the properties on a configuration\n");
		buffer
				.append("\tapplypropchanges <readerid> apply the property changes on the configuration to the reader\n");
		buffer.append("  ----Session related commands----\n");
		buffer
				.append("\tcreatesession <readerid> - create a new session on the given reader\n");
		buffer
				.append("\tdeletesession <readerid> <sessionid> - delete a session on the given reader\n");
		buffer
				.append("\tstartsession <readerid> <sessionid> - start a session on the given reader\n");
		buffer
				.append("\tstopsession <readerid> <sessionid> - stop a session on the given reader\n");
		buffer
				.append("\texecutecommand <readerid> <sessionid> <commandid>  <interval>- execute a command in a session\n");
		buffer
				.append("\tkillcommand <readerid> <sessionid> <commandid> - execute a command in a session\n");

		return buffer.toString();
	}
}

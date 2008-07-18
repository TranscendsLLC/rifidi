package org.rifidi.edge.core.readersession.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.communication.service.CommunicationStateListener;
import org.rifidi.edge.core.communication.service.ConnectionService;
import org.rifidi.edge.core.communication.service.ConnectionStatus;
import org.rifidi.edge.core.exceptions.RifidiCommandInterruptedException;
import org.rifidi.edge.core.exceptions.RifidiCommandNotFoundException;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.exceptions.RifidiExecutionException;
import org.rifidi.edge.core.messageQueue.MessageQueue;
import org.rifidi.edge.core.messageQueue.service.MessageService;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.commands.Command;
import org.rifidi.edge.core.readerplugin.commands.CommandReturnStatus;
import org.rifidi.edge.core.readerplugin.commands.annotations.CommandDesc;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager;
import org.rifidi.edge.core.readerplugin.service.ReaderPluginService;
import org.rifidi.edge.core.readersession.ReaderSession;
import org.rifidi.edge.core.readersession.impl.enums.CommandStatus;
import org.rifidi.edge.core.readersession.impl.enums.ReaderSessionStatus;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * 
 * The ReaderSession handles execution of commands to one physical reader. It
 * listens to the Communication to figure out if a physical connection is
 * established. It also listens to the Command to figure out when the command
 * has finished
 * 
 * @author Andreas Huebner - Andreas@pramari.com
 * @author Jerry Maine - jerry@pramari.com
 * @author kyle Neumeier - kyle@pramari.com
 * 
 */
public class ReaderSessionImpl implements ReaderSession,
		CommunicationStateListener, CommandExecutionListener {

	private static final Log logger = LogFactory
			.getLog(ReaderSessionImpl.class);

	// Services
	private ConnectionService connectionService;
	private MessageService messageService;
	private ReaderPluginService readerPluginService;

	// Session Information
	private ReaderInfo readerInfo;
	private ConnectionManager connectionManager;
	private Connection connection;
	private MessageQueue messageQueue;

	// CommandExecution
	private CommandWrapper curCommand;
	private ExecutionThread executionThread;
	private CommandJournal commandJournal = new CommandJournal();
	private long commandID = 0;

	// Status
	private ReaderSessionStatus readerSessionStatus = ReaderSessionStatus.OK;
	private ConnectionStatus connectionStatus = ConnectionStatus.DISCONNECTED;

	private int readerSessionID;

	public ReaderSessionImpl(ReaderInfo readerInfo, int readerSessionID) {
		this.readerInfo = readerInfo;
		this.readerSessionID = readerSessionID;
		ServiceRegistry.getInstance().service(this);
	}

	/*
	 * =====================================================================
	 * READER SESSION METHODS
	 * =====================================================================
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readersession.ReaderSession#getStatus()
	 */
	@Override
	public ReaderSessionStatus getStatus() {
		return readerSessionStatus;
	}

	/**
	 * This method returns all the commands that this reader session is able to
	 * execute
	 */
	@Override
	public List<String> getAvailableCommands() {
		List<String> commands = new ArrayList<String>();
		CommandDesc commandDesc = null;
		for (Class<? extends Command> commandClass : readerPluginService
				.getReaderPlugin(readerInfo.getClass()).getAvailableCommands()) {
			commandDesc = commandClass.getAnnotation(CommandDesc.class);
			if (commandDesc != null) {
				commands.add(commandDesc.name());
			}
		}
		return commands;
	}

	/**
	 * This method returns all the commands in a specified group that a session
	 * can execute
	 */
	@Override
	public List<String> getAvailableCommands(String groupName) {
		List<String> commands = new ArrayList<String>();
		CommandDesc commandDesc = null;
		for (Class<? extends Command> commandClass : readerPluginService
				.getReaderPlugin(readerInfo.getClass()).getAvailableCommands()) {
			commandDesc = commandClass.getAnnotation(CommandDesc.class);
			if (commandDesc != null) {
				for (String g : commandDesc.groups()) {
					if (g.equals(groupName)) {
						commands.add(commandDesc.name());
					}
				}
			}
		}
		return commands;
	}

	/**
	 * This method returns all the command groups available
	 */
	@Override
	public List<String> getAvailableCommandGroups() {
		Set<String> groups = new HashSet<String>();
		CommandDesc commandDesc = null;
		for (Class<? extends Command> commandClass : readerPluginService
				.getReaderPlugin(readerInfo.getClass()).getAvailableCommands()) {
			commandDesc = commandClass.getAnnotation(CommandDesc.class);
			if (commandDesc != null) {
				for (String g : commandDesc.groups()) {
					groups.add(g);
				}
			}
		}
		return new ArrayList<String>(groups);
	}

	// TODO: validate configuration XML String
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readersession.ReaderSession#executeCommand(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public long executeCommand(String command, String configuration)
			throws RifidiConnectionException,
			RifidiCommandInterruptedException, RifidiCommandNotFoundException {

		if (readerSessionStatus != ReaderSessionStatus.OK) {
			throw new RifidiCommandInterruptedException(
					"Cannot execute command because Reader Session is in "
							+ readerSessionStatus + " state");
		}

		curCommand = new CommandWrapper();
		commandID++;
		curCommand.setCommandID(commandID);
		curCommand.setCommandName(command);
		curCommand.setCommandStatus(CommandStatus.WAITING);
		commandJournal.addCommand(curCommand);

		/*
		 * Initialize the communication if not already done
		 */
		connect();
		/*
		 * Check if we are connected
		 */
		while (connectionStatus != ConnectionStatus.CONNECTED) {
			try {
				synchronized (this) {
					logger.debug(" Reader Session waiting for Connection"
							+ " Status to be Connected");
					wait();
				}

			} catch (InterruptedException e) {
			}
			if (connectionStatus == ConnectionStatus.ERROR) {
				curCommand = null;
				throw new RifidiConnectionException(
						"The connection to the reader is not valid anymore. (MAX_CONNECTION_ATTEMPTS)");
			}
		}

		/*
		 * lookup command
		 */
		Command com = lookupCommand(command);
		curCommand.setCommand(com);

		// Check if Execution Thread is Initialized
		if (executionThread == null) {
			executionThread = new ExecutionThread(connection, messageQueue,
					this);
		}

		readerSessionStatus = ReaderSessionStatus.BUSY;
		curCommand.setCommandStatus(CommandStatus.EXECUTING);
		try {
			executionThread.start(curCommand.getCommand(), configuration,
					commandID);
		} catch (RifidiExecutionException e) {
			curCommand = null;
			throw new RifidiCommandInterruptedException(
					"Cannot execute command because Reader Session is in "
							+ readerSessionStatus + " state");

		}
		logger.debug("Command given to execution thread");
		return commandID;
	}

	/**
	 * Create a connection
	 * 
	 * @throws RifidiConnectionException
	 *             if connection could not be established
	 */
	private void connect() throws RifidiConnectionException {
		if (connection == null) {
			connection = connectionService.createConnection(connectionManager,
					readerInfo, this);
		}
	}

	private Command lookupCommand(String command)
			throws RifidiCommandInterruptedException,
			RifidiCommandNotFoundException {

		// the Command object for the command we are searching for
		Command retVal = null;

		CommandDesc commandDesc = null;

		// get available commands from the reader plugin
		List<Class<? extends Command>> availableCommands = readerPluginService
				.getReaderPlugin(readerInfo.getClass()).getAvailableCommands();

		for (Class<? extends Command> commandClass : availableCommands) {

			logger.debug(readerPluginService.getReaderPlugin(
					readerInfo.getClass()).getAvailableCommands().size()
					+ " Commands found");

			commandDesc = commandClass.getAnnotation(CommandDesc.class);

			if (commandDesc != null) {
				if (commandDesc.name().equalsIgnoreCase(command)) {
					// Create Command via reflection
					Constructor<? extends Command> constructor;
					try {
						constructor = commandClass.getConstructor();
						retVal = (Command) constructor.newInstance();
						// command has been found. break out of loop
						break;
					} catch (SecurityException e) {
						e.printStackTrace();
						throw new RifidiCommandInterruptedException(
								"Command not found.", e);
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
						throw new RifidiCommandInterruptedException(
								"Command not found.", e);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
						throw new RifidiCommandInterruptedException(
								"Command not found.", e);
					} catch (InstantiationException e) {
						e.printStackTrace();
						throw new RifidiCommandInterruptedException(
								"Command not found.", e);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
						throw new RifidiCommandInterruptedException(
								"Command not found.", e);
					} catch (InvocationTargetException e) {
						e.printStackTrace();
						throw new RifidiCommandInterruptedException(
								"Command not found.", e);
					}
				}
			}
		}

		if (retVal == null) {
			throw new RifidiCommandNotFoundException("Command not found "
					+ command);
		}

		return retVal;

	}

	// TODO: figure out return value
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readersession.ReaderSession#stopCurCommand(boolean)
	 */
	@Override
	public boolean stopCurCommand(boolean force) {
		if (readerSessionStatus == ReaderSessionStatus.BUSY
				|| readerSessionStatus == ReaderSessionStatus.ERROR) {
			logger.debug("Stoping Command.");
			executionThread.stop(force);
			curCommand.setCommandStatus(CommandStatus.INTERRUPTED);
			curCommand = null;
			readerSessionStatus = ReaderSessionStatus.OK;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readersession.ReaderSession#stopCurCommand(boolean,
	 *      long)
	 */
	@Override
	public boolean stopCurCommand(boolean force, long commandID) {
		if (commandID == this.commandID) {
			return stopCurCommand(force);
		} else {
			return false;
		}
	}

	// TODO: what should this return if we are not executing anything?
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readersession.ReaderSession#curExecutingCommand()
	 */
	@Override
	public String curExecutingCommand() {
		if (curCommand != null)
			return curCommand.getCommandName();
		else
			return "NO CURRENT COMMAND";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readersession.ReaderSession#curExecutingCommandID()
	 */
	@Override
	public long curExecutingCommandID() {
		if (curCommand != null) {
			return curCommand.getCommandID();
		} else
			return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readersession.ReaderSession#commandStatus()
	 */
	@Override
	public CommandStatus commandStatus() {
		if (curCommand != null) {
			return curCommand.getCommandStatus();
		} else
			return CommandStatus.NOCOMMAND;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readersession.ReaderSession#commandStatus(long)
	 */
	@Override
	public CommandStatus commandStatus(long id) {
		return this.commandJournal.getCommandStatus(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readersession.ReaderSession#getMessageQueueName()
	 */
	@Override
	public String getMessageQueueName() {
		return this.messageQueue.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readersession.ReaderSession#getReaderInfo()
	 */
	@Override
	public ReaderInfo getReaderInfo() {
		return readerInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readersession.ReaderSession#resetReaderSession()
	 */
	@Override
	public void resetReaderSession() {
		try {
			connect();
			readerSessionStatus = ReaderSessionStatus.OK;
		} catch (RifidiConnectionException ex) {
			logger.debug("Cannot reset Reader session");
		}
	}

	/*
	 * =====================================================================
	 * Command Execution Event
	 * =====================================================================
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readersession.impl.CommandExecutionListener#commandFinished(org.rifidi.edge.core.readerplugin.commands.Command,
	 *      org.rifidi.edge.core.readerplugin.commands.CommandReturnStatus)
	 */
	@Override
	public void commandFinished(Command command, CommandReturnStatus status) {
		readerSessionStatus = ReaderSessionStatus.OK;

		// only update commandStatus if the command status has not already been
		// changed from executing
		if (commandJournal.getCommandStatus(command) == CommandStatus.EXECUTING) {
			commandJournal.updateCommand(command, status);
		}
	}

	/*
	 * =====================================================================
	 * Connection Event Listener
	 * =====================================================================
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.communication.service.CommunicationStateListener#connected()
	 */
	@Override
	public void connected() {
		connectionStatus = ConnectionStatus.CONNECTED;
		// we need to notify in case we are waiting for the sate to change to
		// connected
		synchronized (this) {
			notify();
		}
	}

	@Override
	public void disconnected() {
		connectionStatus = ConnectionStatus.DISCONNECTED;
		logger.debug("Connection disconnected");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.communication.service.CommunicationStateListener#error()
	 */
	@Override
	public void error() {
		connectionStatus = ConnectionStatus.ERROR;
		readerSessionStatus = ReaderSessionStatus.ERROR;
		// we need to notify in case we are waiting for the sate to change to
		// connected
		synchronized (this) {
			notify();
		}
	}

	/*
	 * =====================================================================
	 * Service Injection Framework
	 * =====================================================================
	 */

	/**
	 * Injecting method for obtaining a instance of the ConnectionService
	 * through the ServiceRegistry Framework
	 * 
	 * @param connectionService
	 *            instance of the ConnectionService
	 */
	@Inject
	public void setConnectionService(ConnectionService connectionService) {
		this.connectionService = connectionService;
	}

	/**
	 * Injection method to obtain a instance of the MessageService through the
	 * ServiceRegistry Framework
	 * 
	 * @param messageService
	 *            instance of the MessageService
	 */
	@Inject
	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
		messageQueue = this.messageService.createMessageQueue(Integer
				.toString(readerSessionID));
	}

	/**
	 * Injection method to obtain a instance of the ReaderPluginService through
	 * the ServiceRegistry Framework
	 * 
	 * @param readerPluginService
	 */
	@Inject
	public void setReaderPluginService(ReaderPluginService readerPluginService) {
		this.readerPluginService = readerPluginService;
		connectionManager = readerPluginService.getReaderPlugin(
				readerInfo.getClass()).getConnectionManager(readerInfo);
	}

	/*
	 * =====================================================================
	 * CleanUp for Instance destroy
	 * =====================================================================
	 */

	/**
	 * Clean up the ReaderSession
	 */
	public void cleanUP() {
		logger.debug("Cleaning up READDER SESSION: " + this.readerSessionID);
		try {
			if (connection != null) {
				connectionService.destroyConnection(connection, this);
			}
		} catch (RuntimeException e) {
			logger.debug("Got exception: ", e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	@Override
	public void finalize() {
		// TODO think about cleaning that up
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "READER SESSION: " + this.readerSessionID + " TYPE: "
				+ this.readerInfo.getClass().getSimpleName();
	}

}

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
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
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
 * @author Jerry Maine - jerry@pramari.com
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
	private long commandID = 0;

	// Status
	private ReaderSessionStatus readerSessionStatus = ReaderSessionStatus.OK;
	private ConnectionStatus connectionStatus = ConnectionStatus.DISCONNECTED;

	private CommandStatus commandExecutionStatus = CommandStatus.EXECUTING;
	private CommandJournal commandJournal= new CommandJournal();

	public ReaderSessionImpl(ReaderInfo readerInfo) {
		this.readerInfo = readerInfo;
		ServiceRegistry.getInstance().service(this);
	}

	/*
	 * =====================================================================
	 * READER SESSION METHODS
	 * =====================================================================
	 */

	@Override
	public ReaderSessionStatus getStatus() {
		return readerSessionStatus;
	}

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
	
	@Override
	public List<String> getAvailableCommandGroups(){
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

	//TODO Implement firing of events.
	//TODO: validate configuration XML String
	@Override
	public long executeCommand(String command, String configuration)
			throws RifidiConnectionException, RifidiCommandInterruptedException {

		/*
		 * Check if we currently run a command
		 */
		if (readerSessionStatus == ReaderSessionStatus.BUSY) {
			throw new RifidiCommandInterruptedException(
					"This reader is busy, please try this command again later.");
		}

		/*
		 * Life goes by a bit better with a bit of sanity checking...
		 */

		/*
		 * Check for needed Services
		 */
		if (connectionService == null) {
			readerSessionStatus = ReaderSessionStatus.ERROR;
			throw new RifidiConnectionException("No communication service.");
		}
		if (readerPluginService == null) {
			readerSessionStatus = ReaderSessionStatus.ERROR;
			throw new RifidiConnectionException("No ReaderPlugin service.");
		}
		if (messageService == null) {
			readerSessionStatus = ReaderSessionStatus.ERROR;
			throw new RifidiConnectionException("No Message service.");
		}

		/*
		 * If not initialized yet do so
		 */
		if (connectionManager == null)
			connectionManager = readerPluginService.getReaderPlugin(
					readerInfo.getClass()).getConnectionManager(readerInfo);
		if (connection == null) {
			connection = connectionService.createConnection(connectionManager,
					readerInfo, this);
		}
		if (messageQueue == null)
			messageQueue = messageService.createMessageQueue(connectionManager
					.toString());

		/*
		 * Check if we are in an error state
		 */
		if (readerSessionStatus == ReaderSessionStatus.ERROR) {
			// TODO find a way to tell a reason why we are in ERROR
			throw new RifidiConnectionException("");
		}
		if (connectionStatus == ConnectionStatus.ERROR) {
			throw new RifidiConnectionException(
					"The connection to the reader is not valid anymore. (MAX_CONNECTION_ATTEMPTS)");
		}

		/*
		 * Check if we are connected
		 */
		while (connectionStatus != ConnectionStatus.CONNECTED) {
			try {
				synchronized (this) {
					logger.debug("Waiting for Connection"
							+ " Status to be Connected");
					wait();
				}

			} catch (InterruptedException e) {
			}
			if (connectionStatus == ConnectionStatus.ERROR) {
				throw new RifidiConnectionException(
						"The connection to the reader is not valid anymore. (MAX_CONNECTION_ATTEMPTS)");
			}
		}

		/*
		 * lookup command
		 */
		CommandDesc commandDesc = null;
		for (Class<? extends Command> commandClass : readerPluginService
				.getReaderPlugin(readerInfo.getClass()).getAvailableCommands()) {
			logger.debug("Inspecting Command : " + commandClass.getName());

			commandDesc = commandClass.getAnnotation(CommandDesc.class);

			if (commandDesc != null) {
				// TODO make lookup case neutral
				if (commandDesc.name().equals(command)) {
					// Create Command via reflection
					Constructor<? extends Command> constructor;
					try {
						curCommand = new CommandWrapper();
						constructor = commandClass.getConstructor();
						curCommand.setCommand((Command) constructor.newInstance());
						curCommand.setCommandName(command);
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

		// Check if Execution Thread is Initialized
		if (executionThread == null) {
			executionThread = new ExecutionThread(connection, messageQueue,
					this);
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		readerSessionStatus = ReaderSessionStatus.BUSY;
		// EXECUTE THE COMMAND and generate commandID
		commandID++;
		curCommand.setCommandID(commandID);
		executionThread.start(curCommand.getCommand(), commandID);
		logger.debug("Command given to execution thread");
		return commandID;
	}

	//TODO: figure out return value
	@Override
	public boolean stopCurCommand(boolean force) {
		if (readerSessionStatus == ReaderSessionStatus.BUSY
				|| readerSessionStatus == ReaderSessionStatus.ERROR) {
			// TODO put commandstatus into Journal
			logger.debug("Stoping Command.");
			executionThread.stop(force);
			curCommand = null;
			readerSessionStatus = ReaderSessionStatus.OK;
		}
		return false;
	}
	
	public boolean stopCurCommand(boolean force, long commandID){
		if(commandID == this.commandID){
			return stopCurCommand(force);
		}else{
			return false;
		}
	}
	
	//TODO: what should this return if we are not executing anything?
	@Override
	public String curExecutingCommand(){
		return curCommand.getCommandName();
	}
	@Override
	public long curExecutingCommandID(){
		return curCommand.getCommandID();
	}
	
	@Override
	public CommandStatus commandStatus(long id){
		return this.commandJournal.getCommandStatus(id);
	}
	@Override
	public CommandStatus commandStatus(){
		return curCommand.getCommandStatus();
	}
	
	@Override
	public String getMessageQueueName(){
		return this.messageQueue.getName();
	}

	@Override
	public ReaderInfo getReaderInfo() {
		return readerInfo;
	}
	
	@Override
	public void resetReaderSession(){
		//TODO implement this
	}

	/*
	 * =====================================================================
	 * Command Execution Event
	 * =====================================================================
	 */
	@Override
	public void commandFinished(Command command, CommandReturnStatus status) {
		// TODO implement Command Execution End
		readerSessionStatus = ReaderSessionStatus.OK;
		// commandJournal.put(command, status);
	}

	/*
	 * =====================================================================
	 * Connection Event Listener
	 * =====================================================================
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

	@Override
	public void error() {
		connectionStatus = ConnectionStatus.ERROR;
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
	 * @param connectionService
	 */
	@Inject
	public void setConnectionService(ConnectionService connectionService) {
		this.connectionService = connectionService;
	}

	/**
	 * @param messageService
	 */
	@Inject
	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
	}

	/**
	 * @param readerPluginService
	 */
	@Inject
	public void setReaderPluginService(ReaderPluginService readerPluginService) {
		this.readerPluginService = readerPluginService;
	}

	/*
	 * =====================================================================
	 * CleanUp for Instance destroy
	 * =====================================================================
	 */

	public void cleanUP() {
		connectionService.destroyConnection(connection, this);
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

}

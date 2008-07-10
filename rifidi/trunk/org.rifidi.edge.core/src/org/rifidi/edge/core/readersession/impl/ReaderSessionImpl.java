package org.rifidi.edge.core.readersession.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.communication.service.ConnectionEventListener;
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
import org.rifidi.edge.core.readersession.ExecutionListener;
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
		ConnectionEventListener, CommandExecutionListener {

	private static final Log logger = LogFactory
			.getLog(ReaderSessionImpl.class);

	private Set<ExecutionListener> listeners = Collections
			.synchronizedSet(new HashSet<ExecutionListener>());

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
	private Command curCommand;
	private ExecutionThread executionThread;

	// Status
	private ReaderSessionStatus readerSessionStatus = ReaderSessionStatus.OK;
	private ConnectionStatus connectionStatus = ConnectionStatus.DISCONNECTED;

	private CommandStatus commandExecutionStatus = CommandStatus.EXECUTING;
	private HashMap<Command, CommandStatus> commandJournal = new HashMap<Command, CommandStatus>();

	public ReaderSessionImpl(ReaderInfo readerInfo) {
		this.readerInfo = readerInfo;
		ServiceRegistry.getInstance().service(this);
	}

	// TODO Implement firing of events.
	@Override
	public void executeCommand(String command)
			throws RifidiConnectionException, RifidiCommandInterruptedException {

		/*
		 * Check if we currently run a command
		 */
		if (commandExecutionStatus == CommandStatus.EXECUTING) {
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
		if (connection == null)
			connection = connectionService.createConnection(connectionManager,
					readerInfo, this);
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
						constructor = commandClass.getConstructor();
						curCommand = (Command) constructor.newInstance();
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

		readerSessionStatus = ReaderSessionStatus.OK;
		// EXECUTE THE COMMAND
		executionThread.start(curCommand);
	}

	@Override
	public void stopCommand() {
		if (readerSessionStatus == ReaderSessionStatus.BUSY || readerSessionStatus == ReaderSessionStatus.ERROR) {
			// TODO put commandstatus into Journal
			curCommand.stop();
			curCommand = null;
			readerSessionStatus = ReaderSessionStatus.OK;
		}
	}

	@Override
	public ReaderInfo getReaderInfo() {
		return readerInfo;
	}

	@Override
	public ReaderSessionStatus getStatus() {
		return readerSessionStatus;
	}

	/*
	 * =====================================================================
	 * Command Execution Notification Service
	 * This notifies when a execution started
	 * =====================================================================
	 */
	
	@Override
	public void removeExecutionListener(ExecutionListener listener) {
		listeners.add(listener);
	}

	@Override
	public void addExecutionListener(ExecutionListener listener) {
		listeners.add(listener);
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
	 * Service Injection Framework
	 * =====================================================================
	 */
	@Override
	public void connected() {
		connectionStatus = ConnectionStatus.CONNECTED;
		//notify();
	}

	@Override
	public void disconnected() {
		connectionStatus = ConnectionStatus.DISCONNECTED;
		//notify();
	}

	@Override
	public void error() {
		connectionStatus = ConnectionStatus.ERROR;
		//notify();
	}

	@Override
	public void connectionExceptionEvent(Exception exception) {
		// TODO Auto-generated method stub
		// fires whenever there connections momentarily fails.
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

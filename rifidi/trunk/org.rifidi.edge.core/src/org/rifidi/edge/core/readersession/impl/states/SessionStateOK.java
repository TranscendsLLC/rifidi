package org.rifidi.edge.core.readersession.impl.states;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.api.exceptions.RifidiCannotRestartCommandException;
import org.rifidi.edge.core.api.exceptions.RifidiCommandInterruptedException;
import org.rifidi.edge.core.api.exceptions.RifidiCommandNotFoundException;
import org.rifidi.edge.core.api.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.api.exceptions.RifidiExecutionException;
import org.rifidi.edge.core.api.exceptions.RifidiInvalidConfigurationException;
import org.rifidi.edge.core.api.readerplugin.Command;
import org.rifidi.edge.core.api.readerplugin.Property;
import org.rifidi.edge.core.api.readerplugin.ReaderInfo;
import org.rifidi.edge.core.api.readerplugin.commands.CommandArgument;
import org.rifidi.edge.core.api.readerplugin.commands.CommandConfiguration;
import org.rifidi.edge.core.api.readerplugin.property.PropertyConfiguration;
import org.rifidi.edge.core.api.readerplugin.xml.CommandDescription;
import org.rifidi.edge.core.api.readersession.enums.CommandStatus;
import org.rifidi.edge.core.api.readersession.enums.ReaderSessionStatus;
import org.rifidi.edge.core.communication.service.ConnectionStatus;
import org.rifidi.edge.core.readersession.impl.CommandExecutionListener;
import org.rifidi.edge.core.readersession.impl.CommandWrapper;
import org.rifidi.edge.core.readersession.impl.ExecutionThread;
import org.rifidi.edge.core.readersession.impl.ReaderSessionState;

public class SessionStateOK implements ReaderSessionState {

	private Log logger = LogFactory.getLog(SessionStateOK.class);

	private ReaderSessionImpl readerSessionImpl;

	public SessionStateOK(ReaderSessionImpl readerSessionImpl) {
		this.readerSessionImpl = readerSessionImpl;
	}

	@Override
	public void state_enable() {
		logger.debug("cannot execute enable when in " + ReaderSessionStatus.OK);
		readerSessionImpl.transition(new SessionStateOK(readerSessionImpl));

	}

	@Override
	public void state_disable() {
		readerSessionImpl.cleanUpConnection();
		readerSessionImpl.connection = null;
		readerSessionImpl.transition(new SessionStateConfigured(
				readerSessionImpl));
	}

	@Override
	public void state_commandFinished() {
		logger.debug("Cannot Execute commandFinished when in OK session state");
	}

	@Override
	public void state_error() {
		logger.debug("error called in OK Session state");
		readerSessionImpl.transition(new SessionStateError(readerSessionImpl));

	}

	@Override
	public long state_executeCommand(CommandConfiguration configuration)
			throws RifidiCommandNotFoundException, RifidiConnectionException,
			RifidiCommandInterruptedException {
		logger.debug("executeCommand called");
		String commandName = configuration.getCommandName();

		readerSessionImpl.curCommand = new CommandWrapper();
		readerSessionImpl.commandID++;
		readerSessionImpl.curCommand.setCommandID(readerSessionImpl.commandID);
		readerSessionImpl.curCommand.setConfiguration(configuration);
		readerSessionImpl.curCommand.setCommandName(commandName);
		readerSessionImpl.curCommand.setCommandStatus(CommandStatus.WAITING);
		readerSessionImpl.commandJournal
				.addCommand(readerSessionImpl.curCommand);
		/*
		 * lookup command
		 */
		CommandDescription cd = readerSessionImpl.plugin
				.getCommand(commandName);

		if (cd == null) {
			readerSessionImpl.curCommand
					.setCommandStatus(CommandStatus.NOCOMMAND);
			readerSessionImpl.curCommand = null;
			readerSessionImpl.transition(new SessionStateOK(readerSessionImpl));
			throw new RifidiCommandNotFoundException("Command: " + commandName
					+ " not found");
		}

		/*
		 * Instantiate new command object
		 */
		Command com = null;

		try {
			com = (Command) readerSessionImpl.instantiate(cd);
		} catch (RifidiCommandNotFoundException e1) {
			logger.debug(e1.getMessage());
			readerSessionImpl.commandJournal.updateCommand(
					readerSessionImpl.curCommand.getCommand(),
					CommandStatus.NOCOMMAND);
			readerSessionImpl.curCommand = null;
			readerSessionImpl.transition(new SessionStateOK(readerSessionImpl));
			throw e1;
		}
		readerSessionImpl.curCommand.setCommand(com);

		/*
		 * Check if we are connected
		 */

		// If we are disconnected, wait for a connection event to happen.
		while (readerSessionImpl.connectionStatus == ConnectionStatus.DISCONNECTED) {
			synchronized (this) {
				try {
					this.wait();
				} catch (InterruptedException e) {
				}
			}
		}

		if (readerSessionImpl.connectionStatus == ConnectionStatus.ERROR) {
			readerSessionImpl.commandJournal.updateCommand(
					readerSessionImpl.curCommand.getCommand(),
					CommandStatus.NOCOMMAND);
			readerSessionImpl.curCommand = null;
			// conn_error() will be called to transition to the error state
			throw new RifidiConnectionException(
					"The connection to the reader is not valid anymore. (MAX_CONNECTION_ATTEMPTS)");
		}

		/*
		 * Check if Execution Thread is Initialized
		 */
		if (readerSessionImpl.executionThread == null) {
			readerSessionImpl.executionThread = new ExecutionThread(
					readerSessionImpl.messageQueue,
					readerSessionImpl.errorQueue,
					(CommandExecutionListener) readerSessionImpl);
		}

		/*
		 * Prepare for execution of new command
		 */
		readerSessionImpl.connection.cleanQueues();

		/*
		 * Execute new command
		 */
		try {
			readerSessionImpl.executionThread.start(
					readerSessionImpl.connection, readerSessionImpl.curCommand);

			readerSessionImpl.curCommand
					.setCommandStatus(CommandStatus.EXECUTING);

			readerSessionImpl.transition(new SessionStateCommandExecuting(
					readerSessionImpl));

			logger.debug("Command given to execution thread");
			return readerSessionImpl.commandID;
		} catch (RifidiExecutionException e) {
			logger.debug(e.getMessage());
			readerSessionImpl.commandJournal.updateCommand(
					readerSessionImpl.curCommand.getCommand(),
					CommandStatus.UNSUCCESSFUL);
			readerSessionImpl.curCommand = null;
			readerSessionImpl.transition(new SessionStateOK(readerSessionImpl));
			throw new RifidiCommandInterruptedException(e.getMessage());
		}
	}

	@Override
	public PropertyConfiguration state_executeProperty(
			PropertyConfiguration propertiesToExecute, boolean set)
			throws RifidiInvalidConfigurationException,
			RifidiConnectionException, RifidiCommandNotFoundException,
			RifidiCannotRestartCommandException {

		List<String> propertyNames = new ArrayList<String>(propertiesToExecute
				.getPropertyNames());

		if (propertyNames == null || propertyNames.size() == 0) {
			// TODO: do we need a readerSessionImpl.state_propertyFinished()
			// call?
			logger.debug("No 'Properties' Node found in document root");
			readerSessionImpl.transition(new SessionStateOK(readerSessionImpl));
			throw new RifidiInvalidConfigurationException(
					"No 'Properties' Node found in document root");
		}

		/*
		 * Check if we are connected
		 */

		// If we are disconnected, wait for a connection event to happen.
		while (readerSessionImpl.connectionStatus == ConnectionStatus.DISCONNECTED) {
			synchronized (this) {
				try {
					this.wait();
				} catch (InterruptedException e) {
				}
			}
		}

		if (readerSessionImpl.connectionStatus == ConnectionStatus.ERROR) {
			// conn_error() will be called to transition to the error state
			throw new RifidiConnectionException(
					"The connection to the reader is not valid anymore."
							+ " (MAX_CONNECTION_ATTEMPTS)");
		}

		readerSessionImpl.transition(new SessionStatePropertyExecuting(
				readerSessionImpl));

		Set<CommandConfiguration> returnProperties = new HashSet<CommandConfiguration>();

		// for each property in propertiesToExecute
		for (String propertyName : propertyNames) {
			logger.debug("Property name: " + propertyName);

			CommandConfiguration property = propertiesToExecute
					.getProperty(propertyName);

			// validate element
			CommandDescription propertyCommandDescription = readerSessionImpl.plugin
					.getProperty(propertyName);
			if (propertyCommandDescription != null) {

				Property propObject;
				try {
					propObject = (Property) readerSessionImpl
							.instantiate(propertyCommandDescription);

					// readerSessionImpl.validate(propObject.getClass(), e);

					CommandConfiguration returnProperty = null;
					if (readerSessionImpl.connectionStatus == ConnectionStatus.ERROR) {
						logger
								.error("Cannot execute property due to connection error");
						returnProperties.add(createErrorProperty(property,
								"Connection Error"));
					} else if (readerSessionImpl.connectionStatus == ConnectionStatus.CONNECTED) {
						// execute property
						if (set) {
							returnProperty = propObject.setProperty(
									readerSessionImpl.connection,
									readerSessionImpl.errorQueue, property);
						} else {
							returnProperty = propObject.getProperty(
									readerSessionImpl.connection,
									readerSessionImpl.errorQueue, property);
						}

					} else {
						logger.debug("Default case on swtich called with "
								+ readerSessionImpl.connectionStatus);
					}
					// insert return value into element
					returnProperties.add(returnProperty);
				} catch (RifidiCommandNotFoundException e1) {
					logger.error("Cannot execute property.  "
							+ "No property found with name: "
							+ property.getCommandName());
					returnProperties.add(createErrorProperty(property,
							"Property Not Found"));
				}

			} else {
				logger.error("Cannot execute property.  "
						+ "No property found with name: "
						+ property.getCommandName());
				returnProperties.add(createErrorProperty(property,
						"Property Not Found"));
			}

		}// end for loop
		readerSessionImpl.state_propertyFinished();
		return new PropertyConfiguration(returnProperties);
	}

	@Override
	public void state_propertyFinished() {
		logger.debug("Cannot Execute "
				+ "propertyFinished when in OK session state");
	}

	@Override
	public void state_resetSession() {
		logger.debug("Cannot Execute resetSession when in OK session state");
		readerSessionImpl.transition(new SessionStateOK(readerSessionImpl));

	}

	@Override
	public void state_stopCommand(boolean force) {
		logger.debug("Cannot Execute stopCommand when in OK session state");
		readerSessionImpl.transition(new SessionStateOK(readerSessionImpl));

	}

	@Override
	public void conn_connected() {
		readerSessionImpl.connectionStatus = ConnectionStatus.CONNECTED;
		synchronized (this) {
			this.notify();
		}

	}

	@Override
	public void conn_disconnected() {
		readerSessionImpl.connectionStatus = ConnectionStatus.DISCONNECTED;
	}

	@Override
	public void conn_error() {
		readerSessionImpl.connectionStatus = ConnectionStatus.ERROR;
		synchronized (this) {
			this.notify();
		}
		this.state_error();
	}

	@Override
	public ReaderSessionStatus state_getStatus() {
		return ReaderSessionStatus.OK;
	}

	@Override
	public boolean state_setReaderInfo(ReaderInfo readerInfo) {
		logger.debug("Cannot execute setReaderInfo in "
				+ ReaderSessionStatus.OK);
		return false;
	}

	/**
	 * A helper method to step through each argument in a property and set the
	 * error flag to true for it.
	 * 
	 * @param property
	 *            The property to set an error for
	 * @param errorMessage
	 *            The error message
	 * @return
	 */
	static CommandConfiguration createErrorProperty(
			CommandConfiguration property, String errorMessage) {
		Set<CommandArgument> arguments = new HashSet<CommandArgument>();
		ArrayList<String> argumentNames = new ArrayList<String>(property
				.getArgNames());
		for (String argName : argumentNames) {
			arguments.add(new CommandArgument(argName, errorMessage, true));
		}
		return new CommandConfiguration(property.getCommandName(), arguments);
	}

}

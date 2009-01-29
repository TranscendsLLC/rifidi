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
import org.rifidi.edge.core.api.exceptions.RifidiInvalidConfigurationException;
import org.rifidi.edge.core.api.readerplugin.commands.CommandConfiguration;
import org.rifidi.edge.core.api.readerplugin.property.PropertyConfiguration;
import org.rifidi.edge.core.api.readerplugin.property.api.Property;
import org.rifidi.edge.core.api.readersession.enums.CommandStatus;
import org.rifidi.edge.core.api.readersession.enums.ReaderSessionStatus;
import org.rifidi.edge.core.communication.service.ConnectionStatus;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.xml.CommandDescription;
import org.rifidi.edge.core.readersession.impl.ReaderSessionState;

public class SessionStateCommandExecuting implements ReaderSessionState {

	private ReaderSessionImpl readerSessionImpl;
	private Log logger = LogFactory.getLog(SessionStateCommandExecuting.class);
	private boolean propertyWaiting=false;
	private boolean commandFinished=false;
	private Object propertyWaitLock = new Object();
	
	public SessionStateCommandExecuting(ReaderSessionImpl readerSessionImpl) {
		this.readerSessionImpl = readerSessionImpl;
	}

	@Override
	public void state_enable() {
		logger.debug("cannot execute enable when in " + ReaderSessionStatus.EXECUTING_COMMAND);
		readerSessionImpl.transition(new SessionStateCommandExecuting(readerSessionImpl));
		
	}

	@Override
	public void state_disable() {
		logger.debug("cannot execute disable when in " + ReaderSessionStatus.EXECUTING_COMMAND);
		readerSessionImpl.transition(new SessionStateCommandExecuting(readerSessionImpl));
		
	}

	@Override
	public void state_commandFinished() {
		logger.debug("command finished called when in "
				+ "COMMAND_EXECUTING session state");
		
		if (propertyWaiting) {
			logger.debug("property is waiting to execute");
			commandFinished = true;
			readerSessionImpl.curCommand
					.setCommandStatus(CommandStatus.YIELDED);
			synchronized (propertyWaitLock) {
				propertyWaitLock.notify();
			}
		}else{
			readerSessionImpl.transition(new SessionStateOK(readerSessionImpl));
		}
	}

	@Override
	public void state_error() {
		logger.debug("error called when in COMMAND_EXECUTING session state");
		readerSessionImpl.transition(new SessionStateError(readerSessionImpl));
	}

	@Override
	public long state_executeCommand(CommandConfiguration configuration)
			throws RifidiConnectionException,
			RifidiCommandInterruptedException, RifidiCommandNotFoundException {
		logger.debug("cannot executeCommand when in COMMAND_EXECUTING session state");
		readerSessionImpl.transition(new SessionStateCommandExecuting(readerSessionImpl));
		return 0;
	}

	@Override
	public PropertyConfiguration state_executeProperty(PropertyConfiguration propertiesToExecute, boolean set)
			throws RifidiConnectionException, RifidiCommandNotFoundException,
			RifidiCommandInterruptedException, RifidiInvalidConfigurationException, RifidiCannotRestartCommandException {
		propertyWaiting=true;
		commandFinished=false;
		logger.debug("Stopping command");
		readerSessionImpl.executionThread.stop(true);
		
		/*
		 * wait for command to stop
		 */
		while (!commandFinished) {
			synchronized (propertyWaitLock) {
				try {
					propertyWaitLock.wait();
				} catch (InterruptedException e) {
				}
			}
		}
		
		List<String> propertyNames = new ArrayList<String>(propertiesToExecute
				.getPropertyNames());

		if (propertyNames == null || propertyNames.size() == 0) {
			logger.debug("No 'Properties' Node found in document root");
			readerSessionImpl.transition(new SessionStatePropertyExecutingCommYield(readerSessionImpl));
			readerSessionImpl.state_propertyFinished();
			throw new RifidiInvalidConfigurationException(
					"No 'Properties' Node found in document root");
		}


		/*
		 * Check if we are connected
		 */
		
		//If we are disconnected, wait for a connection event to happen.
		while(readerSessionImpl.connectionStatus == ConnectionStatus.DISCONNECTED){
			synchronized (this) {
				try {
					this.wait();
				} catch (InterruptedException e) {
				}
			}
		}

		if (readerSessionImpl.connectionStatus == ConnectionStatus.ERROR) {
			//conn_error() will be called to transition to the error state
			throw new RifidiConnectionException(
					"The connection to the reader is not valid anymore."
							+ " (MAX_CONNECTION_ATTEMPTS)");
		}
		readerSessionImpl.transition(new SessionStatePropertyExecutingCommYield(
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
						returnProperties.add(SessionStateOK.createErrorProperty(property,
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
					returnProperties.add(SessionStateOK.createErrorProperty(property,
							"Property Not Found"));
				}

			} else {
				logger.error("Cannot execute property.  "
						+ "No property found with name: "
						+ property.getCommandName());
				returnProperties.add(SessionStateOK.createErrorProperty(property,
						"Property Not Found"));
			}

		}// end for loop
		readerSessionImpl.state_propertyFinished();
		return new PropertyConfiguration(returnProperties);
	}

	@Override
	public void state_propertyFinished() {
		logger.debug("cannot execute propertyFinished when in COMMAND_EXECUTING session state");
	}

	@Override
	public void state_resetSession() {
		logger.debug("cannot execute resetSession when in COMMAND_EXECUTING session state");
		readerSessionImpl.transition(new SessionStateCommandExecuting(readerSessionImpl));

	}

	@Override
	public void state_stopCommand(boolean force) {
		logger.debug("stopCommand in COMMAND_EXECUTING session state");
		readerSessionImpl.curCommand.setCommandStatus(CommandStatus.INTERRUPTED);
		readerSessionImpl.curCommand = null;
		readerSessionImpl.transition(new SessionStateOK(readerSessionImpl));
		readerSessionImpl.executionThread.stop(force);
	}

	@Override
	public void conn_connected() {
		readerSessionImpl.connectionStatus = ConnectionStatus.CONNECTED;
		synchronized (this) {
			notify();
		}
	}

	@Override
	public void conn_disconnected() {
		readerSessionImpl.connectionStatus = ConnectionStatus.DISCONNECTED;
		logger.debug("Connection disconnected");
	}

	@Override
	public void conn_error() {
		readerSessionImpl.connectionStatus = ConnectionStatus.ERROR;
		synchronized (this) {
			notify();
		}
		this.state_error();
	}

	@Override
	public ReaderSessionStatus state_getStatus() {
		return ReaderSessionStatus.EXECUTING_COMMAND;
	}

	@Override
	public boolean state_setReaderInfo(ReaderInfo readerInfo) {
		logger.debug("cannot execute setReaderInfo when in " + ReaderSessionStatus.EXECUTING_COMMAND);
		readerSessionImpl.transition(new SessionStateOK(readerSessionImpl));
		return false;
		
	}

}

package org.rifidi.edge.core.readersession.impl.states;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.LogFactoryImpl;
import org.rifidi.edge.core.communication.service.ConnectionStatus;
import org.rifidi.edge.core.exceptions.RifidiCannotRestartCommandException;
import org.rifidi.edge.core.exceptions.RifidiCommandInterruptedException;
import org.rifidi.edge.core.exceptions.RifidiCommandNotFoundException;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.exceptions.RifidiExecutionException;
import org.rifidi.edge.core.exceptions.RifidiInvalidConfigurationException;
import org.rifidi.edge.core.readerplugin.commands.Command;
import org.rifidi.edge.core.readerplugin.commands.CommandConfiguration;
import org.rifidi.edge.core.readerplugin.property.Property;
import org.rifidi.edge.core.readerplugin.xml.CommandDescription;
import org.rifidi.edge.core.readersession.impl.CommandExecutionListener;
import org.rifidi.edge.core.readersession.impl.CommandWrapper;
import org.rifidi.edge.core.readersession.impl.ExecutionThread;
import org.rifidi.edge.core.readersession.impl.ReaderSessionState;
import org.rifidi.edge.core.readersession.impl.enums.CommandStatus;
import org.rifidi.edge.core.readersession.impl.enums.ReaderSessionStatus;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class SessionStateOK implements ReaderSessionState {

	private Log logger = LogFactoryImpl.getLog(SessionStateOK.class);

	private ReaderSessionImpl readerSessionImpl;

	public SessionStateOK(ReaderSessionImpl readerSessionImpl) {
		this.readerSessionImpl = readerSessionImpl;
	}

	@Override
	public void state_commandFinished() {
		logger.debug("Cannot Execute commandFinished when in OK session state");
	}

	@Override
	public void state_error() {
		logger.debug("error called in SessionOK state");
		readerSessionImpl.transition(new SessionStateError(readerSessionImpl));

	}

	@Override
	public long state_executeCommand(Document configuration)
			throws RifidiInvalidConfigurationException,
			RifidiCommandNotFoundException, RifidiConnectionException,
			RifidiCommandInterruptedException {
		logger.debug("executeCommand called");
		CommandConfiguration commandConfiguration = new CommandConfiguration(
				configuration);
		String commandName = null;
		Element element = null;

		/*
		 * Verify configuration is ok
		 */
		try {
			commandName = commandConfiguration.getCommandName();
			element = commandConfiguration.getConfigAsElement();
		} catch (RifidiInvalidConfigurationException e1) {
			logger.debug(e1.getMessage());
			readerSessionImpl.transition(new SessionStateOK(readerSessionImpl));
			throw e1;
		}

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
		
		if(cd == null){
			readerSessionImpl.curCommand.setCommandStatus(CommandStatus.NOCOMMAND);
			readerSessionImpl.curCommand=null;
			readerSessionImpl.transition(new SessionStateOK(readerSessionImpl));
			throw new RifidiCommandNotFoundException("Command: " + commandName + " not found");
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

		try {
			readerSessionImpl.validate(com.getClass(), element);
		} catch (SAXException e1) {
			logger.debug(e1.getMessage());
			readerSessionImpl.commandJournal.updateCommand(
					readerSessionImpl.curCommand.getCommand(),
					CommandStatus.NOCOMMAND);
			readerSessionImpl.curCommand = null;
			readerSessionImpl.transition(new SessionStateOK(readerSessionImpl));
			throw new RifidiInvalidConfigurationException(e1.getMessage());
		} catch (IOException e1) {
			logger.debug(e1.getMessage());
			readerSessionImpl.commandJournal.updateCommand(
					readerSessionImpl.curCommand.getCommand(),
					CommandStatus.NOCOMMAND);
			readerSessionImpl.curCommand = null;
			readerSessionImpl.transition(new SessionStateOK(readerSessionImpl));
			throw new RifidiInvalidConfigurationException(e1.getMessage());
		}

		/*
		 * Initialize the communication if not already done. Blocks until
		 * connected
		 */
		try {
			readerSessionImpl.connect();
		} catch (RifidiConnectionException e1) {
			logger.debug(e1.getMessage());
			readerSessionImpl.commandJournal.updateCommand(
					readerSessionImpl.curCommand.getCommand(),
					CommandStatus.NOCOMMAND);
			readerSessionImpl.curCommand = null;
			readerSessionImpl.connectionStatus = ConnectionStatus.ERROR;
			readerSessionImpl.transition(new SessionStateError(
					readerSessionImpl));
			throw e1;
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
			readerSessionImpl.commandJournal.updateCommand(
					readerSessionImpl.curCommand.getCommand(),
					CommandStatus.NOCOMMAND);
			readerSessionImpl.curCommand = null;
			//conn_error() will be called to transition to the error state
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

	// TODO: need to think about what happens when a property command screws up.
	// Do we keep going or send back an error
	@Override
	public Document state_executeProperty(Document propertiesToExecute)
			throws RifidiInvalidConfigurationException,
			RifidiConnectionException, RifidiCommandNotFoundException, RifidiCannotRestartCommandException {

		NodeList documentChildren = propertiesToExecute.getChildNodes();
		Node propertyNode = null;
		for (int i = 0; i < documentChildren.getLength(); i++) {
			if (documentChildren.item(i).getNodeName().equals("Properties")) {
				propertyNode = documentChildren.item(i);
				break;
			}
		}

		if (propertyNode == null) {
			logger.debug("No 'Properties' Node found in document root");
			readerSessionImpl.transition(new SessionStateOK(readerSessionImpl));
			throw new RifidiInvalidConfigurationException(
					"No 'Properties' Node found in document root");
		}

		/*
		 * Initialize the communication if not already done. Blocks until
		 * connected
		 */
		try {
			readerSessionImpl.connect();
		} catch (RifidiConnectionException e1) {
			logger.debug(e1.getMessage());
			readerSessionImpl.connectionStatus = ConnectionStatus.ERROR;
			readerSessionImpl.transition(new SessionStateError(
					readerSessionImpl));
			throw e1;
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


		readerSessionImpl.transition(new SessionStatePropertyExecuting(
				readerSessionImpl));

		// for each property in propertiesToExecute
		NodeList properties = propertyNode.getChildNodes();
		for (int i = 0; i < properties.getLength(); i++) {
			Node property = properties.item(i);
			if (property.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) property;
				logger.debug("Property name: " + e.getNodeName());
				// validate element
				CommandDescription propertyCommand = readerSessionImpl.plugin
						.getProperty(e.getNodeName());
				if (propertyCommand != null) {

					Property propObject;
					try {
						propObject = (Property) readerSessionImpl
								.instantiate(propertyCommand);
						
						readerSessionImpl.validate(propObject.getClass(), e);						

						// execute property
						Element returnVal = propObject.execute(
								readerSessionImpl.connection,
								readerSessionImpl.errorQueue, e);

						// insert return value into element
						propertyNode.replaceChild(returnVal, e);
						
					} catch (RifidiCommandNotFoundException e1) {
						logger.debug(e1.getMessage());
						Element error = propertiesToExecute.createElement(e.getNodeName());
						Text message = propertiesToExecute.createTextNode("Property Not Found");
						error.appendChild(message);
						propertyNode.replaceChild(error, e);
						
					} catch (SAXException e1) {
						logger.debug(e1.getMessage());
						Element error = propertiesToExecute.createElement(e.getNodeName());
						Text message = propertiesToExecute.createTextNode(e1.getMessage());
						error.appendChild(message);
						propertyNode.replaceChild(error, e);

					} catch (IOException e1) {
						logger.debug(e1.getMessage());
						Element error = propertiesToExecute.createElement(e.getNodeName());
						Text message = propertiesToExecute.createTextNode(e1.getMessage());
						error.appendChild(message);
						propertyNode.replaceChild(error, e);
					}

				} else {
					logger.debug("Property Not Found: " + e.getNodeName());
					Element error = propertiesToExecute.createElement(e.getNodeName());
					Text message = propertiesToExecute.createTextNode("Property Not Found");
					error.appendChild(message);
					propertyNode.replaceChild(error, e);
				}
			} else {
				// if property is not element, skip it.
			}
		}// end for loop
		readerSessionImpl.state_propertyFinished();
		return propertiesToExecute;
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

}

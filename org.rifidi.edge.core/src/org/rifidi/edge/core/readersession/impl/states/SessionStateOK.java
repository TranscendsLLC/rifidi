package org.rifidi.edge.core.readersession.impl.states;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.LogFactoryImpl;
import org.rifidi.edge.core.communication.service.ConnectionStatus;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
		readerSessionImpl.transition(new SessionStateOK(readerSessionImpl));

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
		logger.debug("executeCommand called in SessionOK state");
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
		readerSessionImpl.curCommand.setCommandName(commandName);
		readerSessionImpl.curCommand.setCommandStatus(CommandStatus.WAITING);
		readerSessionImpl.commandJournal
				.addCommand(readerSessionImpl.curCommand);
		/*
		 * lookup command
		 */
		CommandDescription cd = readerSessionImpl.plugin
				.getCommand(commandName);

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
		while (readerSessionImpl.connectionStatus != ConnectionStatus.CONNECTED) {
			if (readerSessionImpl.connectionStatus == ConnectionStatus.ERROR) {
				logger.debug("ConnectionStatus is Error");
				readerSessionImpl.commandJournal.updateCommand(
						readerSessionImpl.curCommand.getCommand(),
						CommandStatus.NOCOMMAND);
				readerSessionImpl.curCommand = null;
				readerSessionImpl.transition(new SessionStateError(
						readerSessionImpl));
				throw new RifidiConnectionException(
						"The connection to the reader is not valid anymore. (MAX_CONNECTION_ATTEMPTS)");
			}

			/*
			 * Wait until we are connected (this method's conn_connected() or
			 * conn_error() will be called)
			 */
			try {
				synchronized (this) {
					logger.debug(" Reader Session waiting for Connection"
							+ " Status to be Connected");
					wait();
				}

			} catch (InterruptedException e) {
			}
			if (readerSessionImpl.connectionStatus == ConnectionStatus.ERROR) {
				logger.debug("ConnectionStatus is Error");
				readerSessionImpl.commandJournal.updateCommand(
						readerSessionImpl.curCommand.getCommand(),
						CommandStatus.NOCOMMAND);
				readerSessionImpl.curCommand = null;
				readerSessionImpl.transition(new SessionStateError(
						readerSessionImpl));
				throw new RifidiConnectionException(
						"The connection to the reader is not valid anymore. (MAX_CONNECTION_ATTEMPTS)");
			}
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
					readerSessionImpl.connection, readerSessionImpl.curCommand
							.getCommand(), configuration,
					readerSessionImpl.commandID);

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
			RifidiConnectionException, RifidiCommandNotFoundException {

		NodeList documentChildren = propertiesToExecute.getChildNodes();
		Node propertyNode = null;
		for(int i=0; i<documentChildren.getLength(); i++){
			if(documentChildren.item(i).getNodeName().equals("Properties")){
				propertyNode = documentChildren.item(i);
				break;
			}
		}
		
		if(propertyNode==null){
			logger.debug("Malformed Property Configuration XML: no 'Properties' Node found in document root");
			readerSessionImpl.transition(new SessionStateOK(readerSessionImpl));
			throw new RifidiInvalidConfigurationException("No 'Properties' Node found in document root");
		}
		
		/*
		 * Initialize the communication if not already done. Blocks
		 * until connected
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
		while (readerSessionImpl.connectionStatus != ConnectionStatus.CONNECTED) {
			if (readerSessionImpl.connectionStatus == ConnectionStatus.ERROR) {
				logger.debug("ConnectionStatus is Error");
				readerSessionImpl.transition(new SessionStateError(
						readerSessionImpl));
				throw new RifidiConnectionException(
						"The connection to the reader is not valid anymore."
								+ " (MAX_CONNECTION_ATTEMPTS)");
			}

			/*
			 * Wait until we are connected (this method's
			 * conn_connected() or conn_error() will be called)
			 */
			try {
				synchronized (this) {
					logger.debug(" Reader Session "
							+ "waiting for Connection"
							+ " Status to be Connected");
					wait();
				}

			} catch (InterruptedException e1) {
			}
			if (readerSessionImpl.connectionStatus == ConnectionStatus.ERROR) {
				logger.debug("ConnectionStatus is Error");
				readerSessionImpl.transition(new SessionStateError(
						readerSessionImpl));
				throw new RifidiConnectionException(
						"The connection to the reader is not valid anymore."
								+ " (MAX_CONNECTION_ATTEMPTS)");
			}
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
					} catch (RifidiCommandNotFoundException e1) {
						logger.debug(e1.getMessage());
						readerSessionImpl.transition(new SessionStateOK(
								readerSessionImpl));
						throw e1;
					}
					

					try {
						readerSessionImpl.validate(propObject.getClass(), e);
					} catch (SAXException e1) {
						logger.debug(e1.getMessage());
						readerSessionImpl.transition(new SessionStateOK(
								readerSessionImpl));
						throw new RifidiInvalidConfigurationException(e1
								.getMessage());
					} catch (IOException e1) {
						logger.debug(e1.getMessage());
						readerSessionImpl.transition(new SessionStateOK(
								readerSessionImpl));
						throw new RifidiInvalidConfigurationException(e1
								.getMessage());
					}

					// execute property
					Element returnVal = propObject.execute(
							readerSessionImpl.connection,
							readerSessionImpl.errorQueue, e);

					// insert return value into element
					propertyNode.replaceChild(returnVal,e);
				} else {
					// TODO: what to do if propertyCommand is null?
				}
			} else {
				// TODO: what to do if property is not Element?
			}
		}// end for loop
		readerSessionImpl.state_propertyFinished();
		return propertiesToExecute;
	}

	@Override
	public void state_propertyFinished() {
		logger.debug("Cannot Execute "
				+ "propertyFinished when in OK session state");
		readerSessionImpl.transition(new SessionStateOK(readerSessionImpl));
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
		// we need to notify in case we are waiting for the sate to change to
		// connected
		synchronized (this) {
			notify();
		}
	}

}

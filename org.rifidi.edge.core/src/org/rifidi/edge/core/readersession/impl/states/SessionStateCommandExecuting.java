package org.rifidi.edge.core.readersession.impl.states;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.service.ConnectionStatus;
import org.rifidi.edge.core.exceptions.RifidiCannotRestartCommandException;
import org.rifidi.edge.core.exceptions.RifidiCommandInterruptedException;
import org.rifidi.edge.core.exceptions.RifidiCommandNotFoundException;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.exceptions.RifidiInvalidConfigurationException;
import org.rifidi.edge.core.readerplugin.property.Property;
import org.rifidi.edge.core.readerplugin.xml.CommandDescription;
import org.rifidi.edge.core.readersession.impl.ReaderSessionState;
import org.rifidi.edge.core.readersession.impl.enums.CommandStatus;
import org.rifidi.edge.core.readersession.impl.enums.ReaderSessionStatus;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

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
	public long state_executeCommand(Document configuration)
			throws RifidiConnectionException,
			RifidiCommandInterruptedException, RifidiCommandNotFoundException,
			RifidiInvalidConfigurationException {
		logger.debug("cannot executeCommand when in COMMAND_EXECUTING session state");
		readerSessionImpl.transition(new SessionStateCommandExecuting(readerSessionImpl));
		return 0;
	}

	@Override
	public Document state_executeProperty(Document propertiesToExecute)
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
			readerSessionImpl.transition(new SessionStatePropertyExecutingCommYield(readerSessionImpl));
			readerSessionImpl.state_propertyFinished();
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
		readerSessionImpl.transition(new SessionStatePropertyExecutingCommYield(
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

					Property propObject = null;
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
						Element error = propertiesToExecute.createElement(e
								.getNodeName());
						Text message = propertiesToExecute
								.createTextNode("Property Not Found");
						error.appendChild(message);
						propertyNode.replaceChild(error, e);

					} catch (SAXException e1) {
						logger.debug(e1.getMessage());
						Element error = propertiesToExecute.createElement(e
								.getNodeName());
						Text message = propertiesToExecute.createTextNode(e1
								.getMessage());
						error.appendChild(message);
						propertyNode.replaceChild(error, e);

					} catch (IOException e1) {
						logger.debug(e1.getMessage());
						Element error = propertiesToExecute.createElement(e
								.getNodeName());
						Text message = propertiesToExecute.createTextNode(e1
								.getMessage());
						error.appendChild(message);
						propertyNode.replaceChild(error, e);
					}
				} else {
					logger.debug("Property Not Found: " + e.getNodeName());
					Element error = propertiesToExecute.createElement(e
							.getNodeName());
					Text message = propertiesToExecute
							.createTextNode("Property Not Found");
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

}

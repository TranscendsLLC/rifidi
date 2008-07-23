package org.rifidi.edge.core.readersession.impl.states;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.service.ConnectionStatus;
import org.rifidi.edge.core.exceptions.RifidiCommandInterruptedException;
import org.rifidi.edge.core.exceptions.RifidiCommandNotFoundException;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.exceptions.RifidiInvalidConfigurationException;
import org.rifidi.edge.core.readersession.impl.ReaderSessionState;
import org.rifidi.edge.core.readersession.impl.enums.CommandStatus;
import org.w3c.dom.Document;

public class SessionStateCommandExecuting implements ReaderSessionState {

	ReaderSessionImpl readerSessionImpl;
	Log logger = LogFactory.getLog(SessionStateCommandExecuting.class);
	
	public SessionStateCommandExecuting(ReaderSessionImpl readerSessionImpl) {
		this.readerSessionImpl = readerSessionImpl;
	}
	
	@Override
	public void state_commandFinished() {
		logger.debug("command finished called when in COMMAND_EXECUTING session state");
		readerSessionImpl.transition(new SessionStateOK(readerSessionImpl));
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
			RifidiCommandInterruptedException {
		readerSessionImpl.executionThread.stop(true);
		readerSessionImpl.curCommand.setCommandStatus(CommandStatus.INTERRUPTED);
		readerSessionImpl.curCommand = null;
		return null;

	}

	@Override
	public void state_propertyFinished() {
		logger.debug("cannot propertyFinished when in COMMAND_EXECUTING session state");
		readerSessionImpl.transition(new SessionStateCommandExecuting(readerSessionImpl));

	}

	@Override
	public void state_resetSession() {
		logger.debug("cannot resetSession when in COMMAND_EXECUTING session state");
		readerSessionImpl.transition(new SessionStateCommandExecuting(readerSessionImpl));

	}

	@Override
	public void state_stopCommand(boolean force) {
		logger.debug("stopCommand in COMMAND_EXECUTING session state");
		readerSessionImpl.executionThread.stop(force);
		readerSessionImpl.curCommand.setCommandStatus(CommandStatus.INTERRUPTED);
		readerSessionImpl.curCommand = null;
		readerSessionImpl.transition(new SessionStateOK(readerSessionImpl));
	}

	@Override
	public void conn_connected() {
		readerSessionImpl.connectionStatus = ConnectionStatus.CONNECTED;
		
	}

	@Override
	public void conn_disconnected() {
		readerSessionImpl.connectionStatus = ConnectionStatus.DISCONNECTED;		
	}

	@Override
	public void conn_error() {
		readerSessionImpl.connectionStatus = ConnectionStatus.ERROR;
		this.state_error();
	}

}

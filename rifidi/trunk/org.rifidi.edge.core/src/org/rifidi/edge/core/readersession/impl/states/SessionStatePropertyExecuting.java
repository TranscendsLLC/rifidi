package org.rifidi.edge.core.readersession.impl.states;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.api.exceptions.RifidiCommandInterruptedException;
import org.rifidi.edge.core.api.exceptions.RifidiCommandNotFoundException;
import org.rifidi.edge.core.api.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.api.readerplugin.commands.CommandConfiguration;
import org.rifidi.edge.core.communication.service.ConnectionStatus;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readersession.impl.ReaderSessionState;
import org.rifidi.edge.core.readersession.impl.enums.ReaderSessionStatus;
import org.w3c.dom.Document;

public class SessionStatePropertyExecuting implements ReaderSessionState{

	private ReaderSessionImpl readerSessionImpl;
	private Log logger = LogFactory.getLog(SessionStateCommandExecuting.class);
	
	public SessionStatePropertyExecuting(ReaderSessionImpl readerSessionImpl) {
		this.readerSessionImpl = readerSessionImpl;
	}
	
	@Override
	public void state_enable() {
		logger.debug("cannot enable disable when in " + ReaderSessionStatus.EXECUTING_PROPERTY_WITH_NO_YIELDED_COMMAND);
		readerSessionImpl.transition(new SessionStatePropertyExecuting(readerSessionImpl));
		
	}

	@Override
	public void state_disable() {
		logger.debug("cannot execute disable when in " + ReaderSessionStatus.EXECUTING_PROPERTY_WITH_NO_YIELDED_COMMAND);
		readerSessionImpl.transition(new SessionStatePropertyExecuting(readerSessionImpl));
		
	}

	@Override
	public void state_commandFinished() {
		logger.debug("cannot execute resetSession when in " + ReaderSessionStatus.EXECUTING_PROPERTY_WITH_NO_YIELDED_COMMAND);
	}

	@Override
	public void state_error() {
		readerSessionImpl.transition(new SessionStateError(readerSessionImpl));
	}

	@Override
	public long state_executeCommand(CommandConfiguration configuration)
			throws RifidiConnectionException,
			RifidiCommandInterruptedException, RifidiCommandNotFoundException {
		logger.debug("cannot execute executeCommand when in " + ReaderSessionStatus.EXECUTING_PROPERTY_WITH_NO_YIELDED_COMMAND);
		readerSessionImpl.transition(new SessionStatePropertyExecuting(readerSessionImpl));
		return 0;
	}

	@Override
	public Document state_executeProperty(Document propertiesToExecute, boolean set)
			throws RifidiConnectionException, RifidiCommandNotFoundException,
			RifidiCommandInterruptedException {
		logger.debug("cannot execute executeProperty when in " + ReaderSessionStatus.EXECUTING_PROPERTY_WITH_NO_YIELDED_COMMAND);
		readerSessionImpl.transition(new SessionStatePropertyExecuting(readerSessionImpl));
		return null;
		
	}

	@Override
	public void state_propertyFinished() {
		logger.debug("Property is Finished");
		readerSessionImpl.transition(new SessionStateOK(readerSessionImpl));
		
	}

	@Override
	public void state_resetSession() {
		logger.debug("cannot execute resetSession when in " + ReaderSessionStatus.EXECUTING_PROPERTY_WITH_NO_YIELDED_COMMAND);
		readerSessionImpl.transition(new SessionStatePropertyExecuting(readerSessionImpl));
		
	}

	@Override
	public void state_stopCommand(boolean force) {
		logger.debug("cannot execute stopCommand when in " + ReaderSessionStatus.EXECUTING_PROPERTY_WITH_NO_YIELDED_COMMAND);
		readerSessionImpl.transition(new SessionStatePropertyExecuting(readerSessionImpl));
		
	}

	@Override
	public void conn_connected() {
		logger.debug("connected called in " + ReaderSessionStatus.EXECUTING_PROPERTY_WITH_NO_YIELDED_COMMAND);
		readerSessionImpl.connectionStatus = ConnectionStatus.CONNECTED;
		
	}

	@Override
	public void conn_disconnected() {
		logger.debug("disconnected called in " + ReaderSessionStatus.EXECUTING_PROPERTY_WITH_NO_YIELDED_COMMAND);
		readerSessionImpl.connectionStatus = ConnectionStatus.DISCONNECTED;
	}

	@Override
	public void conn_error() {
		logger.debug("error called in " + ReaderSessionStatus.EXECUTING_PROPERTY_WITH_NO_YIELDED_COMMAND);
		readerSessionImpl.connectionStatus = ConnectionStatus.ERROR;
		this.state_error();
	}

	@Override
	public ReaderSessionStatus state_getStatus() {
		return ReaderSessionStatus.EXECUTING_PROPERTY_WITH_NO_YIELDED_COMMAND;
	}

	@Override
	public boolean state_setReaderInfo(ReaderInfo readerInfo) {
		logger.debug("Cannot execute setReaderInfo in " + ReaderSessionStatus.EXECUTING_PROPERTY_WITH_NO_YIELDED_COMMAND);
		readerSessionImpl.transition(new SessionStatePropertyExecuting(readerSessionImpl));
		return false;
	}

}

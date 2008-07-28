package org.rifidi.edge.core.readersession.impl.states;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.exceptions.RifidiCommandInterruptedException;
import org.rifidi.edge.core.exceptions.RifidiCommandNotFoundException;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.exceptions.RifidiInvalidConfigurationException;
import org.rifidi.edge.core.readersession.impl.ReaderSessionState;
import org.rifidi.edge.core.readersession.impl.enums.ReaderSessionStatus;
import org.w3c.dom.Document;

public class SessionStateError implements ReaderSessionState {

	private ReaderSessionImpl readerSessionImpl;
	
	private Log logger = LogFactory.getLog(SessionStateError.class); 
	
	public SessionStateError(ReaderSessionImpl readerSessionImpl) {
		this.readerSessionImpl = readerSessionImpl;
	}
	
	@Override
	public void state_commandFinished() {
		logger.debug("cannot execute commandFinished when in " + ReaderSessionStatus.ERROR);
		readerSessionImpl.transition(new SessionStateError(readerSessionImpl));

	}

	@Override
	public void state_error() {
		logger.debug("cannot execute stateError when in " + ReaderSessionStatus.ERROR);
		readerSessionImpl.transition(new SessionStateError(readerSessionImpl));

	}

	@Override
	public long state_executeCommand(Document configuration)
			throws RifidiConnectionException,
			RifidiCommandInterruptedException, RifidiCommandNotFoundException,
			RifidiInvalidConfigurationException {
		logger.debug("cannot execute executeCommand when in " + ReaderSessionStatus.ERROR);
		readerSessionImpl.transition(new SessionStateError(readerSessionImpl));
		return -1;
	}

	@Override
	public Document state_executeProperty(Document propertiesToExecute)
			throws RifidiConnectionException, RifidiCommandNotFoundException,
			RifidiCommandInterruptedException {
		logger.debug("cannot execute executeProperty when in " + ReaderSessionStatus.ERROR);
		readerSessionImpl.transition(new SessionStateError(readerSessionImpl));
		return null;

	}

	@Override
	public void state_propertyFinished() {
		logger.debug("cannot execute propertyFinished when in " + ReaderSessionStatus.ERROR);
		readerSessionImpl.transition(new SessionStateError(readerSessionImpl));

	}

	@Override
	public void state_resetSession() {
		logger.debug("Need to implement this");
		readerSessionImpl.transition(new SessionStateError(readerSessionImpl));

	}

	@Override
	public void state_stopCommand(boolean force) {
		logger.debug("cannot execute stopCommand when in " + ReaderSessionStatus.ERROR);
		readerSessionImpl.transition(new SessionStateError(readerSessionImpl));

	}

	@Override
	public void conn_connected() {
		logger.debug("connected called in " + ReaderSessionStatus.ERROR);
		
	}

	@Override
	public void conn_disconnected() {
		logger.debug("disconnected called in " + ReaderSessionStatus.ERROR);
		
	}

	@Override
	public void conn_error() {
		logger.debug("error called in " + ReaderSessionStatus.ERROR);
		
	}

	@Override
	public ReaderSessionStatus state_getStatus() {
		return ReaderSessionStatus.ERROR;
	}

}

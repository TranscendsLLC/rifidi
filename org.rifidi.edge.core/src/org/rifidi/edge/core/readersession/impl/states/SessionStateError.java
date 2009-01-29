package org.rifidi.edge.core.readersession.impl.states;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.api.exceptions.RifidiCommandInterruptedException;
import org.rifidi.edge.core.api.exceptions.RifidiCommandNotFoundException;
import org.rifidi.edge.core.api.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.api.readerplugin.commands.CommandConfiguration;
import org.rifidi.edge.core.api.readerplugin.property.PropertyConfiguration;
import org.rifidi.edge.core.api.readersession.enums.ReaderSessionStatus;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readersession.impl.ReaderSessionState;

public class SessionStateError implements ReaderSessionState {

	private ReaderSessionImpl readerSessionImpl;
	
	private Log logger = LogFactory.getLog(SessionStateError.class); 
	
	public SessionStateError(ReaderSessionImpl readerSessionImpl) {
		this.readerSessionImpl = readerSessionImpl;
	}
	
	@Override
	public void state_enable() {
		logger.debug("cannot execute disable when in " + ReaderSessionStatus.ERROR);
		readerSessionImpl.transition(new SessionStateError(readerSessionImpl));
		
	}

	@Override
	public void state_disable() {
		logger.debug("cannot execute disable when in " + ReaderSessionStatus.ERROR);
		readerSessionImpl.transition(new SessionStateError(readerSessionImpl));
		
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
	public long state_executeCommand(CommandConfiguration configuration)
			throws RifidiConnectionException,
			RifidiCommandInterruptedException, RifidiCommandNotFoundException {
		logger.debug("cannot execute executeCommand when in " + ReaderSessionStatus.ERROR);
		readerSessionImpl.transition(new SessionStateError(readerSessionImpl));
		return -1;
	}

	@Override
	public PropertyConfiguration state_executeProperty(PropertyConfiguration propertiesToExecute, boolean set)
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
		readerSessionImpl.cleanUpConnection();
		readerSessionImpl.connection = null;
		readerSessionImpl.transition(new SessionStateConfigured(readerSessionImpl));
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

	@Override
	public boolean state_setReaderInfo(ReaderInfo readerInfo) {
		logger.debug("Cannot execute setReaderInfo in " + ReaderSessionStatus.ERROR);
		readerSessionImpl.transition(new SessionStateError(readerSessionImpl));
		return false;
		
	}

}

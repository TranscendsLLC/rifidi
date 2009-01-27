package org.rifidi.edge.core.readersession.impl.states;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.api.exceptions.RifidiCannotRestartCommandException;
import org.rifidi.edge.core.api.exceptions.RifidiCommandInterruptedException;
import org.rifidi.edge.core.api.exceptions.RifidiCommandNotFoundException;
import org.rifidi.edge.core.api.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.api.exceptions.RifidiExecutionException;
import org.rifidi.edge.core.api.readerplugin.commands.CommandConfiguration;
import org.rifidi.edge.core.api.readersession.enums.CommandStatus;
import org.rifidi.edge.core.api.readersession.enums.ReaderSessionStatus;
import org.rifidi.edge.core.communication.service.ConnectionStatus;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readersession.impl.CommandExecutionListener;
import org.rifidi.edge.core.readersession.impl.ExecutionThread;
import org.rifidi.edge.core.readersession.impl.ReaderSessionState;
import org.w3c.dom.Document;

public class SessionStatePropertyExecutingCommYield implements
		ReaderSessionState {

	private ReaderSessionImpl readerSessionImpl;
	private Log logger = LogFactory.getLog(SessionStatePropertyExecutingCommYield.class);
	
	public SessionStatePropertyExecutingCommYield(ReaderSessionImpl readerSessionImpl) {
		this.readerSessionImpl = readerSessionImpl;
	}
	
	@Override
	public void state_enable() {
		logger.debug("Cannot execute enable when in " + ReaderSessionStatus.EXECUTING_PROPERTY_WITH_YIELDED_COMMAND);
		readerSessionImpl.transition(new SessionStatePropertyExecutingCommYield(readerSessionImpl));
		
	}

	@Override
	public void state_disable() {
		logger.debug("Cannot execute disable when in " + ReaderSessionStatus.EXECUTING_PROPERTY_WITH_YIELDED_COMMAND);
		readerSessionImpl.transition(new SessionStatePropertyExecutingCommYield(readerSessionImpl));
		
	}

	@Override
	public void state_commandFinished() {
		logger.debug("cannot execute commandFinished when in " + ReaderSessionStatus.EXECUTING_PROPERTY_WITH_YIELDED_COMMAND);
	}

	@Override
	public void state_error() {
		readerSessionImpl.transition(new SessionStateError(readerSessionImpl));

	}

	@Override
	public long state_executeCommand(CommandConfiguration configuration)
			throws RifidiConnectionException,
			RifidiCommandInterruptedException, RifidiCommandNotFoundException {
		logger.debug("cannot execute executeCommand when in " + ReaderSessionStatus.EXECUTING_PROPERTY_WITH_YIELDED_COMMAND);
		readerSessionImpl.transition(new SessionStatePropertyExecutingCommYield(readerSessionImpl));
		return 0;
	}

	@Override
	public Document state_executeProperty(Document propertiesToExecute, boolean set)
			throws RifidiConnectionException, RifidiCommandNotFoundException,
			RifidiCommandInterruptedException, RifidiCannotRestartCommandException {
		
		
		return propertiesToExecute;

	}

	@Override
	public void state_propertyFinished() throws RifidiCannotRestartCommandException{
		if(readerSessionImpl.curCommand==null){
			readerSessionImpl.transition(new SessionStateOK(readerSessionImpl));
			throw new RifidiCannotRestartCommandException("Current command is null");
		}
		if(readerSessionImpl.curCommand.getCommandStatus()!=CommandStatus.YIELDED){
			readerSessionImpl.curCommand = null;
			readerSessionImpl.transition(new SessionStateOK(readerSessionImpl));
			throw new RifidiCannotRestartCommandException("Current command is not in yieled state");
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
			throw new RifidiCannotRestartCommandException(
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

			logger.debug("Command " + readerSessionImpl.curCommand.getCommandID() + " restarted");
		} catch (RifidiExecutionException e) {
			logger.debug(e.getMessage());
			readerSessionImpl.commandJournal.updateCommand(
					readerSessionImpl.curCommand.getCommand(),
					CommandStatus.UNSUCCESSFUL);
			readerSessionImpl.curCommand = null;
			readerSessionImpl.transition(new SessionStateOK(readerSessionImpl));
			throw new RifidiCannotRestartCommandException(e.getMessage());
		}

	}

	@Override
	public void state_resetSession() {
		logger.debug("cannot execute resetSession when in " + ReaderSessionStatus.EXECUTING_PROPERTY_WITH_YIELDED_COMMAND);
		readerSessionImpl.transition(new SessionStatePropertyExecutingCommYield(readerSessionImpl));

	}

	@Override
	public void state_stopCommand(boolean force) {
		logger.debug("cannot execute stopCommand when in " + ReaderSessionStatus.EXECUTING_PROPERTY_WITH_YIELDED_COMMAND);
		readerSessionImpl.transition(new SessionStatePropertyExecutingCommYield(readerSessionImpl));

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
		return ReaderSessionStatus.EXECUTING_PROPERTY_WITH_YIELDED_COMMAND;
	}

	@Override
	public boolean state_setReaderInfo(ReaderInfo readerInfo) {
		logger.debug("Cannot execute setReaderInfo in " + ReaderSessionStatus.EXECUTING_PROPERTY_WITH_NO_YIELDED_COMMAND);
		readerSessionImpl.transition(new SessionStatePropertyExecuting(readerSessionImpl));
		return false;
	}

}

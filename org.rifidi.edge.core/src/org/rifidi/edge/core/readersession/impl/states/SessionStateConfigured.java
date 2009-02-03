/**
 * 
 */
package org.rifidi.edge.core.readersession.impl.states;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.api.exceptions.RifidiCannotRestartCommandException;
import org.rifidi.edge.core.api.exceptions.RifidiCommandInterruptedException;
import org.rifidi.edge.core.api.exceptions.RifidiCommandNotFoundException;
import org.rifidi.edge.core.api.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.api.exceptions.RifidiInvalidConfigurationException;
import org.rifidi.edge.core.api.readerplugin.ReaderInfo;
import org.rifidi.edge.core.api.readerplugin.commands.CommandConfiguration;
import org.rifidi.edge.core.api.readerplugin.property.PropertyConfiguration;
import org.rifidi.edge.core.api.readersession.enums.ReaderSessionStatus;
import org.rifidi.edge.core.communication.service.ConnectionStatus;
import org.rifidi.edge.core.readersession.impl.ReaderSessionState;

/**
 * @author kyle
 * 
 */
public class SessionStateConfigured implements ReaderSessionState {

	private Log logger = LogFactory.getLog(SessionStateOK.class);

	private ReaderSessionImpl readerSessionImpl;

	public SessionStateConfigured(ReaderSessionImpl readerSessionImpl) {
		this.readerSessionImpl = readerSessionImpl;
	}

	@Override
	public void state_enable() {
		/*
		 * Initialize the communication if not already done. Blocks until
		 * connected
		 */

		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					readerSessionImpl.connect();
				} catch (RifidiConnectionException e1) {
					logger.debug(e1.getMessage());
					readerSessionImpl.connectionStatus = ConnectionStatus.ERROR;
					readerSessionImpl.transition(new SessionStateError(
							readerSessionImpl));
				}
			}
		}, "Connection Thread");
		t.start();
		readerSessionImpl.transition(new SessionStateOK(readerSessionImpl));
	}

	@Override
	public void state_disable() {
		logger.debug("Cannot execute disable() when in Configured state");
		readerSessionImpl.transition(new SessionStateConfigured(
				readerSessionImpl));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.readersession.impl.ReaderSessionState#
	 * state_commandFinished()
	 */
	@Override
	public void state_commandFinished() {
		logger.debug("Cannot execute commandFinished "
				+ "when in Configured session state");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readersession.impl.ReaderSessionState#state_error()
	 */
	@Override
	public void state_error() {
		logger.debug("error called in Configured state");
		readerSessionImpl.transition(new SessionStateError(readerSessionImpl));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.readersession.impl.ReaderSessionState#
	 * state_executeCommand(org.w3c.dom.Document)
	 */
	@Override
	public long state_executeCommand(CommandConfiguration configuration)
			throws RifidiConnectionException,
			RifidiCommandInterruptedException, RifidiCommandNotFoundException {
		logger.debug("Cannot execute executeCommand "
				+ "when in Configured session state");
		readerSessionImpl.transition(new SessionStateConfigured(
				readerSessionImpl));
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.readersession.impl.ReaderSessionState#
	 * state_executeProperty(org.w3c.dom.Document)
	 */
	@Override
	public PropertyConfiguration state_executeProperty(PropertyConfiguration propertiesToExecute,
			boolean set) throws RifidiConnectionException,
			RifidiCommandNotFoundException, RifidiCommandInterruptedException,
			RifidiInvalidConfigurationException,
			RifidiCannotRestartCommandException {
		logger.debug("Cannot execute "
				+ "executeProperty when in Configured session state");
		readerSessionImpl.transition(new SessionStateConfigured(
				readerSessionImpl));
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readersession.impl.ReaderSessionState#state_getStatus
	 * ()
	 */
	@Override
	public ReaderSessionStatus state_getStatus() {
		return ReaderSessionStatus.CONFIGURED;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.readersession.impl.ReaderSessionState#
	 * state_propertyFinished()
	 */
	@Override
	public void state_propertyFinished()
			throws RifidiCannotRestartCommandException {
		logger.debug("Cannot execute propertyFinished when "
				+ "in Configured session state");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readersession.impl.ReaderSessionState#state_resetSession
	 * ()
	 */
	@Override
	public void state_resetSession() {
		logger.debug("Cannot execute resetSession when in Configured state");
		readerSessionImpl.transition(new SessionStateConfigured(
				readerSessionImpl));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readersession.impl.ReaderSessionState#state_stopCommand
	 * (boolean)
	 */
	@Override
	public void state_stopCommand(boolean force) {
		logger.debug("Cannot execute stopCommand when in Configured state");
		readerSessionImpl.transition(new SessionStateConfigured(
				readerSessionImpl));

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

	@Override
	public boolean state_setReaderInfo(ReaderInfo readerInfo) {
		readerSessionImpl.readerInfo = readerInfo;
		readerSessionImpl.connectionManager = readerSessionImpl.plugin
				.newConnectionManager(readerInfo);
		readerSessionImpl.transition(new SessionStateConfigured(
				readerSessionImpl));
		return true;
	}
}

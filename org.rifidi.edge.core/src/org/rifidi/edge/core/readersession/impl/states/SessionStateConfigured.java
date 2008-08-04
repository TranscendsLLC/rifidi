/**
 * 
 */
package org.rifidi.edge.core.readersession.impl.states;

import org.rifidi.edge.core.exceptions.RifidiCannotRestartCommandException;
import org.rifidi.edge.core.exceptions.RifidiCommandInterruptedException;
import org.rifidi.edge.core.exceptions.RifidiCommandNotFoundException;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.exceptions.RifidiInvalidConfigurationException;
import org.rifidi.edge.core.readersession.impl.ReaderSessionState;
import org.rifidi.edge.core.readersession.impl.enums.ReaderSessionStatus;
import org.w3c.dom.Document;

/**
 * @author kyle
 *
 */
public class SessionStateConfigured implements ReaderSessionState {

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readersession.impl.ReaderSessionState#state_commandFinished()
	 */
	@Override
	public void state_commandFinished() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readersession.impl.ReaderSessionState#state_error()
	 */
	@Override
	public void state_error() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readersession.impl.ReaderSessionState#state_executeCommand(org.w3c.dom.Document)
	 */
	@Override
	public long state_executeCommand(Document configuration)
			throws RifidiConnectionException,
			RifidiCommandInterruptedException, RifidiCommandNotFoundException,
			RifidiInvalidConfigurationException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readersession.impl.ReaderSessionState#state_executeProperty(org.w3c.dom.Document)
	 */
	@Override
	public Document state_executeProperty(Document propertiesToExecute)
			throws RifidiConnectionException, RifidiCommandNotFoundException,
			RifidiCommandInterruptedException,
			RifidiInvalidConfigurationException,
			RifidiCannotRestartCommandException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readersession.impl.ReaderSessionState#state_getStatus()
	 */
	@Override
	public ReaderSessionStatus state_getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readersession.impl.ReaderSessionState#state_propertyFinished()
	 */
	@Override
	public void state_propertyFinished()
			throws RifidiCannotRestartCommandException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readersession.impl.ReaderSessionState#state_resetSession()
	 */
	@Override
	public void state_resetSession() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readersession.impl.ReaderSessionState#state_stopCommand(boolean)
	 */
	@Override
	public void state_stopCommand(boolean force) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.communication.service.CommunicationStateListener#conn_connected()
	 */
	@Override
	public void conn_connected() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.communication.service.CommunicationStateListener#conn_disconnected()
	 */
	@Override
	public void conn_disconnected() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.communication.service.CommunicationStateListener#conn_error()
	 */
	@Override
	public void conn_error() {
		// TODO Auto-generated method stub

	}

	@Override
	public void state_disable() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void state_enable() {
		// TODO Auto-generated method stub
		
	}

}

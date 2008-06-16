package org.rifidi.edge.rmi.session.impl;

import java.rmi.RemoteException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.readerAdapter.commands.ICustomCommand;
import org.rifidi.edge.core.session.ISession;
import org.rifidi.edge.core.session.Session;
import org.rifidi.edge.rmi.session.RemoteSession;

public class RemoteSessionImpl implements RemoteSession {

	private Log logger = LogFactory.getLog(RemoteSessionRegistryImpl.class);

	private Session session;

	public RemoteSessionImpl(Session session) {
		this.session = session;
	}

	@Override
	public void sendCustomCommand(ICustomCommand customCommand)
			throws RemoteException {
		logger.debug("Remote Call: sendCustomCommand()");
		session.sendCustomCommand(customCommand);
	}

	@Override
	public void startTagStream() throws RemoteException {
		logger.debug("Remote Call: startTagStream()");
		session.startTagStream();
	}

	@Override
	public void stopTagStream() throws RemoteException {
		logger.debug("Remote Call: stopTagStream()");
		session.stopTagStream();
	}

	// TODO Think about a better place for this
	public ISession getSession() {
		return session;
	}

	@Override
	public String getReaderDescription() {
		return session.getConnectionInfo().getReaderType();
	}

	@Override
	public String getReaderType() {
		return session.getConnectionInfo().getReaderDescription();
	}

}

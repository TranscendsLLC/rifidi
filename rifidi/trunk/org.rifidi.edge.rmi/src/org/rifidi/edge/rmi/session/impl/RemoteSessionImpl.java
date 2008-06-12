package org.rifidi.edge.rmi.session.impl;

import java.rmi.RemoteException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.session.ISession;
import org.rifidi.edge.rmi.session.RemoteSession;

public class RemoteSessionImpl implements RemoteSession {

	private Log logger = LogFactory.getLog(RemoteSessionRegistryImpl.class);
	
	private ISession session;
	
	public RemoteSessionImpl(ISession session) {
		this.session = session;
	}
	
	@Override
	public void sendCustomCommand(Object o) throws RemoteException {
		logger.debug("Remote Call: sendCustomCommand()");
		session.sendCustomCommand(o);
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
	
	//TODO Think about a better place for this
	public ISession getSession() {
		return session;
	}
	
	

}

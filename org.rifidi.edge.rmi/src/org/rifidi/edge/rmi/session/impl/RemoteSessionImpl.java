package org.rifidi.edge.rmi.session.impl;

import java.rmi.RemoteException;

import org.rifidi.edge.core.session.ISession;
import org.rifidi.edge.rmi.session.RemoteSession;

public class RemoteSessionImpl implements RemoteSession {

	private ISession session;
	
	public RemoteSessionImpl(ISession session) {
		this.session = session;
	}
	
	@Override
	public void sendCustomCommand(Object o) throws RemoteException {
		session.sendCustomCommand(o);
	}

	@Override
	public void startTagStream() throws RemoteException {
		session.startTagStream();
	}

	@Override
	public void stopTagStream() throws RemoteException {
		session.stopTagStream();
	}
	
	//TODO Think about a better place for this
	public ISession getSession() {
		return session;
	}
	
	

}

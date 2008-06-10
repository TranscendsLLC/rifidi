package org.rifidi.edge.rmi.session.impl;

import java.rmi.RemoteException;

import org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo;
import org.rifidi.edge.core.session.SessionRegistryService;
import org.rifidi.edge.rmi.session.RemoteSession;
import org.rifidi.edge.rmi.session.RemoteSessionRegistry;

public class RemoteSessionRegistryImpl implements RemoteSessionRegistry {

	private SessionRegistryService sessionRegistryService;

	public RemoteSessionRegistryImpl(SessionRegistryService sessionRegistryService) {
		this.sessionRegistryService = sessionRegistryService;
	}
	
	@Override
	public RemoteSession createReaderSession(
			AbstractConnectionInfo connectionInfo) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteReaderSession(RemoteSession remoteSession)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}

}

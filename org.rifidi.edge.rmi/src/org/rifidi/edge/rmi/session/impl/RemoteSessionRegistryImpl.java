package org.rifidi.edge.rmi.session.impl;

import java.rmi.RemoteException;

import org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo;
import org.rifidi.edge.core.session.ISession;
import org.rifidi.edge.core.session.Session;
import org.rifidi.edge.core.session.SessionRegistryService;
import org.rifidi.edge.rmi.session.RemoteSession;
import org.rifidi.edge.rmi.session.RemoteSessionRegistry;

public class RemoteSessionRegistryImpl implements RemoteSessionRegistry {

	private SessionRegistryService sessionRegistryService;

	public RemoteSessionRegistryImpl(
			SessionRegistryService sessionRegistryService) {
		this.sessionRegistryService = sessionRegistryService;
	}

	@Override
	public RemoteSession createReaderSession(
			AbstractConnectionInfo connectionInfo) throws RemoteException {
		RemoteSession remoteSession = new RemoteSessionImpl(
				sessionRegistryService.createReaderSession(connectionInfo));
		return remoteSession;
	}

	@Override
	public void deleteReaderSession(RemoteSession remoteSession)
			throws RemoteException {
		//TODO look if this is even working Session might be not available
		if(remoteSession instanceof RemoteSessionImpl)
		{
			ISession session = ((RemoteSessionImpl)remoteSession).getSession();
			if(session instanceof Session)
			{
				sessionRegistryService.deleteReaderSession(((Session)session).getSessionID());
			}else
			{
				//TODO Get Log4J
				System.err.println("RemoteSessionRegistry: Session Error... look this up");
			}
		}else
		{
			//TODO Get Log4J
			System.err.println("RemoteSessionRegistry: Session Error... look this up");
		}
		
		
	}
}

package org.rifidi.edge.rmi.session.impl;

import java.rmi.RemoteException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo;
import org.rifidi.edge.core.session.ISession;
import org.rifidi.edge.core.session.Session;
import org.rifidi.edge.core.session.SessionRegistryService;
import org.rifidi.edge.rmi.session.RemoteSession;
import org.rifidi.edge.rmi.session.RemoteSessionRegistry;

public class RemoteSessionRegistryImpl implements RemoteSessionRegistry {

	private Log logger = LogFactory.getLog(RemoteSessionRegistryImpl.class);
	
	private SessionRegistryService sessionRegistryService;

	public RemoteSessionRegistryImpl(
			SessionRegistryService sessionRegistryService) {
		this.sessionRegistryService = sessionRegistryService;
	}

	@Override
	public RemoteSession createReaderSession(
			AbstractConnectionInfo connectionInfo) throws RemoteException {
		logger.debug("Remote Call: createReaderSession()");
		RemoteSession remoteSession = new RemoteSessionImpl(
				sessionRegistryService.createReaderSession(connectionInfo));
		return remoteSession;
	}

	@Override
	public void deleteReaderSession(RemoteSession remoteSession)
			throws RemoteException {
		logger.debug("Remote Call: deleteReaderSession()");
		//TODO look if this is even working Session might be not available
		if(remoteSession instanceof RemoteSessionImpl)
		{
			ISession session = ((RemoteSessionImpl)remoteSession).getSession();
			if(session instanceof Session)
			{
				sessionRegistryService.deleteReaderSession(((Session)session).getSessionID());
			}else
			{
				logger.error("RemoteSessionRegistry: Session Error... look this up");
			}
		}else
		{
			logger.error("RemoteSessionRegistry: Session Error... look this up");
		}
		
		
	}
}

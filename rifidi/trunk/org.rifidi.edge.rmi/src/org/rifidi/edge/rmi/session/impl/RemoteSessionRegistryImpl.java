package org.rifidi.edge.rmi.session.impl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.connection.ReaderConnection;
import org.rifidi.edge.core.connection.ReaderConnectionRegistryService;
import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;
import org.rifidi.edge.core.readerPluginService.ReaderAdapterRegistryService;
import org.rifidi.edge.rmi.session.RemoteSession;
import org.rifidi.edge.rmi.session.RemoteSessionRegistry;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

public class RemoteSessionRegistryImpl implements RemoteSessionRegistry {

	private Log logger = LogFactory.getLog(RemoteSessionRegistryImpl.class);
	
	private ReaderConnectionRegistryService sessionRegistryService;
	private ReaderAdapterRegistryService readerAdapterRegistryService;

	private List<RemoteSession> remoteSessionList = new ArrayList<RemoteSession>();
	
	public RemoteSessionRegistryImpl(
			ReaderConnectionRegistryService sessionRegistryService) {
		this.sessionRegistryService = sessionRegistryService;
		ServiceRegistry.getInstance().service(this);
	}

	@Override
	public RemoteSession createReaderSession(
			AbstractReaderInfo connectionInfo) throws RemoteException {
		logger.debug("Remote Call: createReaderSession()");
		RemoteSession remoteSession = new RemoteSessionImpl(
				sessionRegistryService.createReaderSession(connectionInfo));
		remoteSessionList.add(remoteSession);
		return remoteSession;
	}

	@Override
	public void deleteReaderSession(RemoteSession remoteSession)
			throws RemoteException {
		logger.debug("Remote Call: deleteReaderSession()");
		//TODO look if this is even working Session might be not available
		if(remoteSession instanceof RemoteSessionImpl)
		{
			ReaderConnection session = ((RemoteSessionImpl)remoteSession).getSession();
			if(session instanceof ReaderConnection)
			{
				remoteSessionList.remove(remoteSession);
				sessionRegistryService.deleteReaderSession(((ReaderConnection)session).getSessionID());
			}else
			{
				logger.error("RemoteSessionRegistry: Session Error... look this up");
			}
		}else
		{
			logger.error("RemoteSessionRegistry: Session Error... look this up");
		}	
	}

	@Override
	public List<String> getAvailableReaderAdapters() throws RemoteException {
		return readerAdapterRegistryService.getAvailableReaderAdapters();
	}

	@Inject
	public void setReaderAdapterRegistryService(
			ReaderAdapterRegistryService readerAdapterRegistryService) {
		this.readerAdapterRegistryService = readerAdapterRegistryService;
	}

	@Override
	public List<RemoteSession> getAllSessions() throws RemoteException {
		return new ArrayList<RemoteSession>(remoteSessionList);
	}
}

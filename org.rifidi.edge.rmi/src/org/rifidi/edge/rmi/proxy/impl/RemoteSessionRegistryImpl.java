package org.rifidi.edge.rmi.proxy.impl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.connection.ReaderConnection;
import org.rifidi.edge.core.connection.ReaderConnectionRegistryService;
import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;
import org.rifidi.edge.core.readerPluginService.ReaderPluginRegistryService;
import org.rifidi.edge.rmi.proxy.RemoteReaderConnection;
import org.rifidi.edge.rmi.proxy.RemoteReaderConnectionRegistry;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

public class RemoteSessionRegistryImpl implements RemoteReaderConnectionRegistry {

	private Log logger = LogFactory.getLog(RemoteSessionRegistryImpl.class);
	
	private ReaderConnectionRegistryService sessionRegistryService;
	private ReaderPluginRegistryService readerPluginRegistryService;

	private List<RemoteReaderConnection> remoteSessionList = new ArrayList<RemoteReaderConnection>();
	
	public RemoteSessionRegistryImpl(
			ReaderConnectionRegistryService sessionRegistryService) {
		this.sessionRegistryService = sessionRegistryService;
		ServiceRegistry.getInstance().service(this);
	}

	@Override
	public RemoteReaderConnection createReaderSession(
			AbstractReaderInfo connectionInfo) throws RemoteException {
		logger.debug("Remote Call: createReaderSession()");
		RemoteReaderConnection remoteReaderConnection = new RemoteSessionImpl(
				sessionRegistryService.createReaderConnection(connectionInfo));
		remoteSessionList.add(remoteReaderConnection);
		return remoteReaderConnection;
	}

	@Override
	public void deleteReaderSession(RemoteReaderConnection remoteReaderConnection)
			throws RemoteException {
		logger.debug("Remote Call: deleteReaderSession()");
		//TODO look if this is even working Session might be not available
		if(remoteReaderConnection instanceof RemoteSessionImpl)
		{
			ReaderConnection session = ((RemoteSessionImpl)remoteReaderConnection).getSession();
			if(session instanceof ReaderConnection)
			{
				remoteSessionList.remove(remoteReaderConnection);
				sessionRegistryService.deleteReaderConnection(((ReaderConnection)session).getSessionID());
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
		return readerPluginRegistryService.getAvailableReaderAdapters();
	}

	@Inject
	public void setReaderAdapterRegistryService(
			ReaderPluginRegistryService readerPluginRegistryService) {
		this.readerPluginRegistryService = readerPluginRegistryService;
	}

	@Override
	public List<RemoteReaderConnection> getAllSessions() throws RemoteException {
		return new ArrayList<RemoteReaderConnection>(remoteSessionList);
	}
}

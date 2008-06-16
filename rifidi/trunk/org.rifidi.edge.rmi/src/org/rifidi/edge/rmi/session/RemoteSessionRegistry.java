package org.rifidi.edge.rmi.session;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo;

public interface RemoteSessionRegistry extends Remote {

	public RemoteSession createReaderSession(
			AbstractConnectionInfo connectionInfo) throws RemoteException;

	public void deleteReaderSession(RemoteSession remoteSession)
			throws RemoteException;
	
	public List<RemoteSession> getAllSessions() throws RemoteException;
	
	public List<String> getAvailableReaderAdapters() throws RemoteException;

}

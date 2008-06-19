package org.rifidi.edge.rmi.ReaderConnection;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;

public interface RemoteReaderConnectionRegistry extends Remote {

	public RemoteReaderConnection createReaderConnection(
			AbstractReaderInfo connectionInfo) throws RemoteException;

	public void deleteReaderConnection(RemoteReaderConnection remoteReaderConnection)
			throws RemoteException;
	
	public List<RemoteReaderConnection> getAllSessions() throws RemoteException;
	
	public List<String> getAvailableReaderAdapters() throws RemoteException;

}

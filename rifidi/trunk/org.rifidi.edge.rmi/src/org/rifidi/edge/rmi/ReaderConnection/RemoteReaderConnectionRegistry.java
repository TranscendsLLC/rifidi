package org.rifidi.edge.rmi.ReaderConnection;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;

/**
 * This is the RemoteReaderConnectionRegistry. It's used for storing
 * RemoteReaderConnections and for creating and deleting them. It also provides
 * information about current ReaderConnections and available ReaderPlugins.
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public interface RemoteReaderConnectionRegistry extends Remote {

	/**
	 * Create a new ReaderConnectio
	 * 
	 * @param connectionInfo
	 * @return the RemoteReaderConnection
	 * @throws RemoteException
	 */
	public RemoteReaderConnection createReaderConnection(
			AbstractReaderInfo connectionInfo) throws RemoteException;

	/**
	 * Delete a previous made ReaderConnection
	 * 
	 * @param remoteReaderConnection
	 * @throws RemoteException
	 */
	public void deleteReaderConnection(
			RemoteReaderConnection remoteReaderConnection)
			throws RemoteException;

	/**
	 * Get all available ReaderConnections
	 * 
	 * @return a List of RemoteReaderConnections
	 * @throws RemoteException
	 */
	public List<RemoteReaderConnection> getAllSessions() throws RemoteException;

	/**
	 * Show the names of the registered ReaderPlugin ReaderInfos
	 * 
	 * @return List of Names of the registred ReaderPlugin ReaderInfos
	 * @throws RemoteException
	 */
	public List<String> getAvailableReaderAdapters() throws RemoteException;

}

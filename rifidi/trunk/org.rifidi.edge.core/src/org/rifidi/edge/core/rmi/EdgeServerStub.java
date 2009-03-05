package org.rifidi.edge.core.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import org.rifidi.edge.core.exceptions.NonExistentCommandFactoryException;
import org.rifidi.edge.core.exceptions.NonExistentReaderConfigurationException;

/**
 * This is the interface for the Edge Server RMI Stub.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface EdgeServerStub extends Remote {

	/**
	 * Saves the current configurations to a file
	 * 
	 * @throws RemoteException
	 */
	void save() throws RemoteException;

	/**
	 * This method gets all the Sessions currently available on the readerSession. The
	 * key is the ID of the session. The List has three elements. The first is
	 * the ID of the readerconfiguration on the session. The second is the ID of
	 * the commandconfiguration on the session. The third is the state of the
	 * session
	 * 
	 * @return A map whose key is the ID of the session and whose value contains
	 *         information about the session as defined above
	 */
	Map<String, List<String>> getReaderSessions() throws RemoteException;

	/**
	 * This method changes the readerConfiguration and/or commandConfiguration
	 * on a given readerSession
	 * 
	 * @param readerSessionID
	 *            the ID of the session to modify
	 * @param readerConfigurationID
	 *            the ID of the readerconfiguration to set on this session, or
	 *            null if it is not changing
	 * @param commandConfigurationID
	 *            the ID of the commandconfiguration to set on this session, or
	 *            null if it is not changing
	 * @return true if the set was successful, false if there was a problem
	 * @throws RemoteException
	 */
	boolean setSessionReaderConfiguration(String readerSessionID,
			String readerConfigurationID, String commandConfigurationID)
			throws RemoteException;

}

package org.rifidi.edge.api.rmi.services;

import java.rmi.RemoteException;

/**
 * This is the interface for the Edge Server RMI Stub.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public interface EdgeServerManagerService{

	/**
	 * Saves the current configurations to a file
	 * 
	 * @throws RemoteException
	 */
	void save();

	/**
	 * Returns the last time this server was started.  
	 * 
	 * @return The timestamp of the last time this server was started
	 * @throws RemoteException
	 */
	Long getStartupTime();
}

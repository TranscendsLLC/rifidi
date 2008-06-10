package org.rifidi.edge.rmi.session;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteSession extends Remote {
	
	// TODO Think about error handling
	public void startTagStream() throws RemoteException;

	// TODO Think about error handling
	public void stopTagStream() throws RemoteException;
	
	// TODO Think about error handling
	public void sendCustomCommand(Object o) throws RemoteException;
}

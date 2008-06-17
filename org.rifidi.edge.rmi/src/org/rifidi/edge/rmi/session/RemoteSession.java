package org.rifidi.edge.rmi.session;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.rifidi.edge.core.readerPlugin.commands.ICustomCommand;

public interface RemoteSession extends Remote {

	// TODO Think about error handling
	public void startTagStream() throws RemoteException;

	// TODO Think about error handling
	public void stopTagStream() throws RemoteException;

	// TODO Think about error handling
	public void sendCustomCommand(ICustomCommand o) throws RemoteException;

}

package org.rifidi.edge.rmi.ReaderConnection;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.rifidi.edge.core.readerPlugin.commands.ICustomCommand;

public interface RemoteReaderConnection extends Remote {

	// TODO Think about error handling
	public void startTagStream() throws RemoteException;

	// TODO Think about error handling
	public void stopTagStream() throws RemoteException;

	// TODO Think about error handling
	public void sendCustomCommand(ICustomCommand o) throws RemoteException;

}

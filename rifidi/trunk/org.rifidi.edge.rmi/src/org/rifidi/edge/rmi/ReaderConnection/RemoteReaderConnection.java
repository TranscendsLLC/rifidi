package org.rifidi.edge.rmi.ReaderConnection;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;
import org.rifidi.edge.core.readerPlugin.commands.ICustomCommand;

public interface RemoteReaderConnection extends Remote {

	// TODO Think about error handling
	public void connect() throws RemoteException;

	// TODO Think about error handling
	public void disconnect() throws RemoteException;

	// TODO Think about error handling
	public void startTagStream() throws RemoteException;

	// TODO Think about error handling
	public void stopTagStream() throws RemoteException;

	// TODO Think about error handling
	public void sendCustomCommand(ICustomCommand customCommand)
			throws RemoteException;

	public String getTagQueueName() throws RemoteException;
	
	public AbstractReaderInfo getReaderInfo() throws RemoteException;
	
	public String getReaderState() throws RemoteException;
}

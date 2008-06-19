package org.rifidi.edge.rmi.ReaderConnection;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;
import org.rifidi.edge.core.readerPlugin.commands.ICustomCommand;

/**
 * This is the RemoteReaderConnection for controlling ReaderConnections over
 * RMI. It supports all functions you can operate on a ReaderConnection.
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public interface RemoteReaderConnection extends Remote {

	// TODO Think about error handling
	/**
	 * Connect the ReaderPlugin with the Reader
	 * 
	 * @throws RemoteException
	 */
	public void connect() throws RemoteException;

	// TODO Think about error handling
	/**
	 * Disconnect the ReaderPlugin from the Reader
	 * 
	 * @throws RemoteException
	 */
	public void disconnect() throws RemoteException;

	// TODO Think about error handling
	/**
	 * Start streaming of tags at the JMS Queue
	 * 
	 * @throws RemoteException
	 */
	public void startTagStream() throws RemoteException;

	// TODO Think about error handling
	/**
	 * Stop streaming of tags
	 * 
	 * @throws RemoteException
	 */
	public void stopTagStream() throws RemoteException;

	// TODO Think about error handling
	/**
	 * Send a custom command to the Reader
	 * 
	 * @param customCommand
	 * @throws RemoteException
	 */
	public void sendCustomCommand(ICustomCommand customCommand)
			throws RemoteException;

	/**
	 * Get the name of the JMS Queue for TagReads
	 * 
	 * @return
	 * @throws RemoteException
	 */
	public String getTagQueueName() throws RemoteException;

	/**
	 * Get the ReaderInfo of this ReaderConnection
	 * 
	 * @return
	 * @throws RemoteException
	 */
	public AbstractReaderInfo getReaderInfo() throws RemoteException;

	/**
	 * Get the state of the Reader
	 * 
	 * @return
	 * @throws RemoteException
	 */
	public String getReaderState() throws RemoteException;
}

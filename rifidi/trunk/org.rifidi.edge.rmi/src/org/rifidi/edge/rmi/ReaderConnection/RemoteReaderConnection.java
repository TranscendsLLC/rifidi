/*
 *  RemoteReaderConnection.java
 *
 *  Created:	Jun 19, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.rmi.ReaderConnection;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.rmi.customcommand.CustomCommand;

/**
 * This is the RemoteReaderConnection for controlling ReaderConnections over
 * RMI. It supports all functions you can operate on a ReaderConnection.
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public interface RemoteReaderConnection extends Remote {

	/**
	 * Connect the ReaderPlugin with the Reader
	 * 
	 * @throws RemoteException
	 */
	public void connect() throws RemoteException;

	/**
	 * Disconnect the ReaderPlugin from the Reader
	 * 
	 * @throws RemoteException
	 */
	public void disconnect() throws RemoteException;

	/**
	 * Start streaming of tags at the JMS Queue
	 * 
	 * @throws RemoteException
	 */
	public void startTagStream() throws RemoteException;

	/**
	 * Stop streaming of tags
	 * 
	 * @throws RemoteException
	 */
	public void stopTagStream() throws RemoteException;

	/**
	 * Send a custom command to the Reader
	 * 
	 * @param customCommand
	 * @throws RemoteException
	 */
	public void sendCustomCommand(CustomCommand customCommand)
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
	public ReaderInfo getReaderInfo() throws RemoteException;

	/**
	 * Get the state of the Reader
	 * 
	 * @return
	 * @throws RemoteException
	 */
	public String getReaderState() throws RemoteException;
}

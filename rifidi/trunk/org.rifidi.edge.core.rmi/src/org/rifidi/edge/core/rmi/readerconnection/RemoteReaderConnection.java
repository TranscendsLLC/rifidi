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
package org.rifidi.edge.core.rmi.readerconnection;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import org.rifidi.edge.core.readerplugin.ReaderInfo;

/**
 * This is the RemoteReaderConnection for controlling ReaderConnections over
 * RMI. It supports all functions you can operate on a ReaderConnection.
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public interface RemoteReaderConnection extends Remote {

	/**
	 * @return
	 * @throws RemoteException
	 */
	public List<String> getAvailableCommands() throws RemoteException;

	/**
	 * @param groupName
	 * @return
	 * @throws RemoteException
	 */
	public List<String> getAvailableCommands(String groupName)
			throws RemoteException;

	/**
	 * @return
	 * @throws RemoteException
	 */
	public List<String> getAvailableCommandGroups() throws RemoteException;

	/**
	 * Send a custom command to the Reader
	 * 
	 * @param customCommand
	 * @throws RemoteException
	 */
	public long executeCommand(String command, String configuration) throws RemoteException;

	/**
	 * Start streaming of tags to the MessageQueue (This is a convenience method
	 * for the command "streamtags")
	 * 
	 * @throws RemoteException
	 */
	public long startTagStream(String configuration) throws RemoteException;

	/**
	 * Stop current executing command without checking if the command is a
	 * specific one by ID
	 * 
	 * @throws RemoteException
	 */
	public boolean stopCurCommand(boolean force) throws RemoteException;

	/**
	 * Stop current executing command with checking if the command is a specific
	 * one by ID
	 * 
	 * @param commandID
	 * @throws RemoteException
	 */
	public boolean stopCurCommand(boolean force, long commandID)
			throws RemoteException;

	/**
	 * @return the name of the currently executing command
	 */
	public String curExecutingCommand() throws RemoteException;

	/**
	 * @return 0 if no command is executed otherwise the id of the command
	 */
	public long curExecutingCommandID() throws RemoteException;

	/**
	 * This returns the status of the previously executed command
	 * 
	 * @param id
	 *            command id assigned at the execution
	 * @return String representing the status of the command execution
	 */
	public String commandStatus(long id);

	/**
	 * @return
	 */
	public String commandStatus();
	
	/**
	 * Get the name of the MessageQueue
	 * 
	 * @return
	 * @throws RemoteException
	 */
	public String getMessageQueueName() throws RemoteException;

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

	/**
	 * @throws RemoteException
	 */
	public void resetReaderConnection() throws RemoteException;
}

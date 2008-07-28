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
	 * Get a list of all available commands of this ReaderConnection
	 * 
	 * @return a list of Strings representing the available Commands
	 * @throws RemoteException
	 *             if a error occurs
	 */
	public List<String> getAvailableCommands() throws RemoteException;

	/**
	 * Get a list of available commands which are part of the given Group
	 * 
	 * @param groupName
	 *            Name of the Group the commands belong to
	 * @return a list of Commands in this group
	 * @throws RemoteException
	 *             if a error occurs
	 */
	public List<String> getAvailableCommands(String groupName)
			throws RemoteException;
	
	public List<String> getAvailableProperties() throws RemoteException;

	/**
	 * Get a list of available command groups
	 * 
	 * @return groups available for this type of RemoteConnection
	 * @throws RemoteException
	 *             if a error occurs
	 */
	public List<String> getAvailableCommandGroups() throws RemoteException;

	/**
	 * Execute a custom command on the ReaderConnection
	 * 
	 * @param configuration
	 *            xml describing the command configuration parameters
	 * @return an id under this command will be executed
	 * @throws RemoteException
	 *             if a error occurs
	 */
	public long executeCommand(String configuration) throws RemoteException;
	
	public String executeProperty(String configuration) throws RemoteException;

	/**
	 * Start streaming of tags to the MessageQueue (This is a convenience method
	 * for the command "streamtags")
	 * 
	 * @param configuration
	 *            xml describing the configuration parameters
	 * @return an id under this a command will be executed
	 * @throws RemoteException
	 *             if a error occurs
	 */
	public long startTagStream(String configuration) throws RemoteException;

	/**
	 * Stop current executing command without checking if the command is a
	 * specific one by ID
	 * 
	 * @param force
	 *            for the command to stop by killing it if neccessary
	 * @return true if successful, false otherwise
	 * @throws RemoteException
	 *             if a error occurs
	 */
	public boolean stopCurCommand(boolean force) throws RemoteException;

	/**
	 * Stop current executing command with checking if the command is a specific
	 * one by ID
	 * 
	 * @param commandID
	 *            the command to stop
	 * @param force
	 *            for the command to stop by killing it if neccessary
	 * @return true if successful, false otherwise
	 * @throws RemoteException
	 *             if a error occurs
	 */
	public boolean stopCurCommand(boolean force, long commandID)
			throws RemoteException;

	/**
	 * Get the command currently executing
	 * 
	 * @return the name of the command currently executing, or null if no
	 *         command is executing
	 * @throws RemoteException
	 *             if a error occurs
	 */
	public String curExecutingCommand() throws RemoteException;

	/**
	 * Get the ID of the current executing command
	 * 
	 * @return 0 if no command is executed otherwise the id of the command
	 */
	public long curExecutingCommandID() throws RemoteException;

	/**
	 * This returns the status of the previously executed command
	 * 
	 * @param id
	 *            command id assigned at the execution
	 * @return String representing the status of the command execution
	 * @throws RemoteException
	 *             if an error occurs
	 */
	public String commandStatus(long id) throws RemoteException;

	/**
	 * Get the status of the current executing command
	 * 
	 * @return the status of the current executing command
	 * @throws RemoteException
	 *             if an error occurs
	 */
	public String commandStatus() throws RemoteException;

	/**
	 * Get the name of the MessageQueue
	 * 
	 * @return name of the MessageQueue
	 * @throws RemoteException
	 *             if an error occurs
	 */
	public String getMessageQueueName() throws RemoteException;

	/**
	 * Get the ReaderInfo of this ReaderConnection
	 * 
	 * @return the ReaderInfo of this instance of the RemoteReaderConnection
	 * @throws RemoteException
	 *             if an error occurs
	 */
	public ReaderInfo getReaderInfo() throws RemoteException;

	/**
	 * Get the state of the Reader
	 * 
	 * @return the state the ReaderConnection is in (OK, BUSY, ERROR)
	 * @throws RemoteException
	 *             if an error occurs
	 */
	public String getReaderState() throws RemoteException;

	/**
	 * Reset the ReaderConnection if it is in a ERROR state. This will reset the
	 * connection, messageQueue as well as all buffers
	 * 
	 * @throws RemoteException
	 *             if an error occurs
	 */
	public void resetReaderConnection() throws RemoteException;
}

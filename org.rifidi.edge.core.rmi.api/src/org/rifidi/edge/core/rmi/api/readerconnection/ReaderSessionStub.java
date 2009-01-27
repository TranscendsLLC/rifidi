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
package org.rifidi.edge.core.rmi.api.readerconnection;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

import org.rifidi.edge.core.api.exceptions.RifidiCannotRestartCommandException;
import org.rifidi.edge.core.api.exceptions.RifidiCommandInterruptedException;
import org.rifidi.edge.core.api.exceptions.RifidiCommandNotFoundException;
import org.rifidi.edge.core.api.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.api.exceptions.RifidiInvalidConfigurationException;
import org.rifidi.edge.core.api.exceptions.RifidiReaderInfoNotFoundException;
import org.rifidi.edge.core.api.readerplugin.commands.CommandConfiguration;
import org.rifidi.edge.core.api.readerplugin.property.PropertyConfiguration;
import org.rifidi.edge.core.rmi.api.readerconnection.returnobjects.CommandInfo;
import org.rifidi.edge.core.rmi.api.readerconnection.returnobjects.ReaderSessionProperties;

/**
 * This is the RemoteReaderSession for controlling Reader Sessions over RMI. It
 * supports all functions you can operate on a ReaderConnection.
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface ReaderSessionStub extends Remote {

	/**
	 * Move the session from configured to OK
	 * 
	 * @throws RemoteException
	 *             If there was a network, serialization, or other RMI problem
	 */
	public void enable() throws RemoteException;

	/**
	 * Move the session to the configured state
	 * 
	 * @throws RemoteException
	 *             If there was a network, serialization, or other RMI problem
	 */
	public void disable() throws RemoteException;

	/**
	 * Get the state of the Reader
	 * 
	 * @return the state the ReaderConnection is in (CONFIGURED OK, BUSY, ERROR)
	 * @throws RemoteException
	 *             If there was a network, serialization, or other RMI problem
	 */
	public String getReaderState() throws RemoteException;

	/**
	 * Reset the ReaderConnection if it is in a ERROR state. This will reset the
	 * connection, messageQueue as well as all buffers
	 * 
	 * @throws RemoteException
	 *             If there was a network, serialization, or other RMI problem
	 */
	public void resetReaderConnection() throws RemoteException;

	/**
	 * Execute a custom command on the ReaderConnection
	 * 
	 * @param configuration
	 *            Command Configuration that supplies the command name and any
	 *            arguments
	 * @return an id under this command will be executed
	 * @throws RemoteException
	 *             If there was a network, serialization, or other RMI problem
	 */
	public long executeCommand(CommandConfiguration configuration)
			throws RemoteException, RifidiConnectionException,
			RifidiCommandInterruptedException, RifidiCommandNotFoundException,
			RifidiInvalidConfigurationException;

	/**
	 * Stop current executing command without checking if the command is a
	 * specific one by ID
	 * 
	 * @param force
	 *            for the command to stop by killing it if necessary
	 * @throws RemoteException
	 *             If there was a network, serialization, or other RMI problem
	 */
	public void stopCurCommand(boolean force) throws RemoteException;

	/**
	 * Stop current executing command with checking if the command is a specific
	 * one by ID
	 * 
	 * @param commandID
	 *            the command to stop
	 * @param force
	 *            for the command to stop by killing it if necessary
	 * @throws RemoteException
	 *             If there was a network, serialization, or other RMI problem
	 */
	public void stopCurCommand(boolean force, long commandID)
			throws RemoteException;

	/**
	 * Get the status of the current executing command
	 * 
	 * @return the status of the current executing command
	 * @throws RemoteException
	 *             If there was a network, serialization, or other RMI problem
	 */
	public CommandInfo commandStatus() throws RemoteException;

	/**
	 * This returns the status of the previously executed command
	 * 
	 * @param id
	 *            command id assigned at the execution
	 * @return the status of the requested command
	 * @throws RemoteException
	 *             If there was a network, serialization, or other RMI problem
	 */
	public CommandInfo commandStatus(long id) throws RemoteException;

	/**
	 * This method sets reader properties.
	 * 
	 * @param configuration
	 *            One or more properties (and their values) to set.
	 * @return The updated property values
	 * @throws RemoteException
	 *             If there was a network, serialization, or other RMI problem
	 * @throws RifidiConnectionException
	 *             If the properties could not be set due to a network failure
	 *             with the reader
	 * @throws RifidiCommandNotFoundException
	 *             If the property handler could not be found
	 * @throws RifidiCommandInterruptedException
	 *             If the property was interrupted when executing
	 * @throws RifidiCannotRestartCommandException
	 *             If a command that was executing previous to the property
	 *             could not be restarted
	 */
	public PropertyConfiguration setProperty(PropertyConfiguration configuration)
			throws RemoteException, RifidiConnectionException,
			RifidiCommandNotFoundException, RifidiCommandInterruptedException,
			RifidiCannotRestartCommandException;

	/**
	 * This method gets reader properties
	 * 
	 * @param propertyNames
	 *            One or more properties to get
	 * @return The updated property values
	 * @throws RemoteException
	 *             If there was a network, serialization, or other RMI problem
	 * @throws RifidiConnectionException
	 *             If the properties could not be set due to a network failure
	 *             with the reader
	 * @throws RifidiCommandNotFoundException
	 *             If the property handler could not be found
	 * @throws RifidiCommandInterruptedException
	 *             If the property was interrupted when executing
	 * @throws RifidiCannotRestartCommandException
	 *             If a command that was executing previous to the property
	 *             could not be restarted
	 */
	public PropertyConfiguration getProperty(Set<String> propertyNames)
			throws RemoteException, RifidiConnectionException,
			RifidiCommandNotFoundException, RifidiCommandInterruptedException,
			RifidiCannotRestartCommandException;

	/**
	 * List of static name-value pairs that includes readerInfoClassName,
	 * messageQueueName, and errorQueueName. The values shouldn't change in the
	 * life of the session
	 * 
	 * @return Properties of this session (as opposed to Reader Properties)
	 * @throws RemoteException
	 *             If there was a network, serialization, or other RMI problem
	 */
	public ReaderSessionProperties getSessionProperties()
			throws RemoteException;

	/**
	 * 
	 * @return The qualified class name of the ReaderInfo for the plugin of this
	 *         reader
	 * @throws RemoteException
	 *             If there was a network, serialization, or other RMI problem
	 * @throws RifidiReaderInfoNotFoundException
	 *             If the readerInfo could not be found for this session
	 */
	public String getReaderInfo() throws RemoteException,
			RifidiReaderInfoNotFoundException;

	/**
	 * Change the readerInfo for this session
	 * 
	 * @param readerInfo
	 *            The new reader Info
	 * @throws RemoteException
	 *             If there was a network, serialization, or other RMI problem
	 * @throws RifidiReaderInfoNotFoundException
	 *             If there was a problem when setting the ReaderInfo
	 */
	public void setReaderInfo(String readerInfo) throws RemoteException,
			RifidiReaderInfoNotFoundException;
}

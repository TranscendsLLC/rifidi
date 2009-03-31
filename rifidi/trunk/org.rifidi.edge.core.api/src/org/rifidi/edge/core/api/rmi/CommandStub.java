/**
 * 
 */
package org.rifidi.edge.core.api.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

import javax.management.AttributeList;
import javax.management.MBeanInfo;

import org.rifidi.edge.core.api.rmi.dto.CommandConfigFactoryDTO;
import org.rifidi.edge.core.api.rmi.dto.CommandConfigurationDTO;

/**
 * This is the interface for a stub that allows clients to interact with
 * commands on the edge server
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface CommandStub extends Remote {

	/**
	 * This method gets the available command types on the edge server and the
	 * ID of the ReaderFactory that the command type works with
	 * 
	 * @return A set of CommandConfigPluginDTOs
	 * @throws RemoteException
	 */
	Set<CommandConfigFactoryDTO> getCommandConfigFactories()
			throws RemoteException;

	/**
	 * Get the CommandConfigFactoryID associated with a readerFactoryID
	 * 
	 * @param readerFactoryID
	 * @return
	 */
	CommandConfigFactoryDTO getCommandConfigFactory(String readerFactoryID)
			throws RemoteException;

	/**
	 * Gets the DTOs for configured commands
	 * 
	 * @return a set of configured commands
	 * @throws RemoteException
	 */
	Set<CommandConfigurationDTO> getCommands() throws RemoteException;

	/**
	 * Gets the DTO for a given Command Configuration
	 * 
	 * @param commandConfigurationID
	 *            The ID of the commandConfiguration to get
	 * @return A DTO for the configured command, or null if no command
	 *         configuration is available for the given ID
	 * @throws RemoteException
	 */
	CommandConfigurationDTO getCommandConfiguration(
			String commandConfigurationID) throws RemoteException;

	/**
	 * Gets the meta information necessary to construct a new Command
	 * 
	 * @param commandType
	 *            the type of command to make
	 * @return an MBeanInfo object that describes how to make a new command
	 * @throws RemoteException
	 */
	MBeanInfo getCommandDescription(String commandType) throws RemoteException;

	/**
	 * Create a new Command
	 * 
	 * @param commandType
	 *            The type of the Command to make
	 * @param properties
	 *            the properties of a Command
	 * @return the ID of the newly created Command
	 * @throws RemoteException
	 */
	String createCommand(String commandType, AttributeList properties)
			throws RemoteException;

	/**
	 * Sets the properties of a Command
	 * 
	 * @param commandID
	 *            the ID of the command to set
	 * @param properties
	 *            the new properties of the command
	 * @return A list of name-value pairs of all properties of the command
	 * @throws RemoteException
	 */
	AttributeList setCommandProperties(String commandID,
			AttributeList properties) throws RemoteException;

	/**
	 * Delete a command configuration
	 * 
	 * @param commandConfigurationID
	 *            the ID of the commandConfiguration to delete
	 * @throws RemoteException
	 */
	void deleteCommand(String commandID) throws RemoteException;
}

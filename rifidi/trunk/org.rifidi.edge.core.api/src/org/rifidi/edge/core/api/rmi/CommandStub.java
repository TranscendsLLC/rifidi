/**
 * 
 */
package org.rifidi.edge.core.api.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

import javax.management.AttributeList;
import javax.management.MBeanInfo;

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
	 * for example, it return <Alien9800-GetTagList, Alien9800>
	 * 
	 * @return A map where the key is an ID of a command type and the value is
	 *         the ID of a ReaderFactory on which the command will run
	 * @throws RemoteException
	 */
	Map<String, String> getCommandConfigurationTypes() throws RemoteException;

	/**
	 * Gets the already configured commands
	 * 
	 * for example, it return <Alien9800-GetTagList-1, Alien9800-GetTagList>
	 * 
	 * @return a map where the key is the ID of the Command and the value is the
	 *         type of command.
	 * @throws RemoteException
	 */
	Map<String, String> getCommands() throws RemoteException;

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
	 * Get the properties of a currently configured Command
	 * 
	 * @param commandID
	 *            The ID of the Command
	 * @return An attributeList that contains the properties of the command
	 * @throws RemoteException
	 */
	AttributeList getCommandProperties(String commandID) throws RemoteException;

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
	void deleteCommand(String commandID)
			throws RemoteException;
}

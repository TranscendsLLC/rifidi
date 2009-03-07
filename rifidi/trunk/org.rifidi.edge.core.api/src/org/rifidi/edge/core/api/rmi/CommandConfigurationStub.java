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
 * To be clear: CommandConfigurationFactory: AlienCommandConfigurationFactory
 * CommandConfigurationType: AlienGetTagList, AlienGPIGetTagList
 * AbstractCommandConfiguration: configured AlienGetTagListCommandConfiguration
 * 
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface CommandConfigurationStub extends Remote {

	/**
	 * This method gets the available command types on the edge server and the
	 * ID of the readerSession factory that the command type works with
	 * 
	 * for example, it return <Alien9800-GetTagList, Alien9800>
	 * 
	 * @return A map where the key is an ID of a readerConfiguration type and
	 *         the value is the ID of a ReaderConfigurationFactory on which the
	 *         command will run
	 * @throws RemoteException
	 */
	Map<String, String> getCommandConfigurationTypes() throws RemoteException;

	/**
	 * Gets the already configured commands
	 * 
	 * @return a map where the key is the ID of the commandConfiguration and the
	 *         value is the ID of the AbstractCommandConfiguration type of the
	 *         AbstractCommandConfiguration
	 * @throws RemoteException
	 */
	Map<String, String> getCommandConfigurations() throws RemoteException;

	/**
	 * Gets the meta information necessary to construct a new
	 * CommandConfiguration
	 * 
	 * @param commandConfigurationType
	 *            the type of configuration to make
	 * @return an MBeanInfo object that describes how to make a new
	 *         commandConfiguration
	 * @throws RemoteException
	 */
	MBeanInfo getCommandConfigurationDescription(String commandConfigurationType)
			throws RemoteException;

	/**
	 * Create a new commandConfiguration
	 * 
	 * @param commandConfigurationType
	 *            The type of the configuration to make
	 * @param properties
	 *            the properties of a commandConfiguration
	 * @return the ID of the newly created command configuration
	 * @throws RemoteException
	 */
	String createCommandConfiguration(String commandConfigurationType,
			AttributeList properties) throws RemoteException;

	/**
	 * Get the properties of a currently configured command configuration
	 * 
	 * @param commandConfigurationID
	 *            The ID of the configuration
	 * @return An attributeList that contains the properties of the command
	 *         configuration
	 * @throws RemoteException
	 */
	AttributeList getCommandConfigurationProperties(
			String commandConfigurationID) throws RemoteException;

	/**
	 * Sets the properties of a command configuration
	 * 
	 * @param commandConfigurationID
	 *            the ID of the command configuration to set
	 * @param properties
	 *            the new properties of the command configuration
	 * @return A list of name-value pairs of all properties of the command
	 *         configuration
	 * @throws RemoteException
	 */
	AttributeList setCommandConfigurationProperties(
			String commandConfigurationID, AttributeList properties)
			throws RemoteException;

	/**
	 * Delete a command configuration
	 * 
	 * @param commandConfigurationID
	 *            the ID of the commandConfiguration to delete
	 * @throws RemoteException
	 */
	void deleteCommandConfiguration(String commandConfigurationID)
			throws RemoteException;
}

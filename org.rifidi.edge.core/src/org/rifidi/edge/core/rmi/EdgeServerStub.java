package org.rifidi.edge.core.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.AttributeList;
import javax.management.MBeanInfo;

/**
 * This is the interface for the Edge Server RMI Stub.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface EdgeServerStub extends Remote {

	/**
	 * These are the reader "plugins" that are available on the server
	 * 
	 * @return a list of IDs of Reader Configuration Factories that are
	 *         currently available
	 */
	Set<String> getAvailableReaderConfigurationFactories()
			throws RemoteException;

	/**
	 * This method gets the reader configurations that are currently available
	 * on the server
	 * 
	 * @return a map where the key is the ID of the available
	 *         ReaderConfigurations and the value is the ID of the
	 *         ConfiguraitonFactory that the particular configuration belongs to
	 * @throws RemoteException
	 */
	Map<String, String> getAvailableReaderConfigurations()
			throws RemoteException;

	/**
	 * Gets a description of a reader configuration. This will contain the
	 * information necessary to construct a ReaderConfiguration.
	 * 
	 * @param readerConfigurationFactoryID
	 *            the ID of the ReaderConfigurationFactory
	 * @return an MBeanInfo object whose attributes describe how to create a
	 *         ReaderConfiguration
	 * @throws RemoteException
	 */
	MBeanInfo getReaderConfigurationDescription(
			String readerConfigurationFactoryID) throws RemoteException;

	/**
	 * Creates a new ReaderConfiguration for the supplied configuration. The ID
	 * of the readerConfiguration can be used along with the ID of a command
	 * configuration to start a reader session
	 * 
	 * @param readerConfigurationFactoryID
	 *            the ID of the reader configuration factory
	 * @param readerConfigurationProperties
	 *            all the properties to set on the new ReaderConfiguration
	 * @return the ID of the newly created ReaderConfiguration
	 * @throws RemoteException
	 */
	String createReaderConfiguration(String readerConfigurationFactoryID,
			AttributeList readerConfigurationProperties) throws RemoteException;

	/**
	 * This method gets all the properties for all of the currently available
	 * Configuration Properties. It should be used with caution: if there are a
	 * large number of reader configurations available, this method call could
	 * be expensive
	 * 
	 * @return A map where the key is the ID of the reader configuration and the
	 *         value is an AttributeList that has all the name-value pairs for
	 *         all of the properties
	 * @throws RemoteException
	 */
	Map<String, AttributeList> getAllReaderConfigurationProperties()
			throws RemoteException;

	/**
	 * This method returns an AttributeList that contains the name-value pairs
	 * for all the properties of a currently available ReaderConfiguration.
	 * 
	 * @param readerConfigurationID
	 *            The ID of the ReaderConfiguration to get
	 * @return The current values for a reader configuration
	 * @throws RemoteException
	 */
	AttributeList getReaderConfigurationProperties(String readerConfigurationID)
			throws RemoteException;

	/**
	 * This method is used to update the properties of a ReaderConfiguration. It
	 * may contain only the properties that you intend to change, but may
	 * contain values that will stay the same
	 * 
	 * @param ReaderConfigurationID
	 *            the ID of the ReaderConfiguration to update
	 * @param readerConfigurationProperties
	 *            the new properties to set
	 * @return All the properties of the reader configuration
	 */
	AttributeList setReaderConfigurationProperties(
			String ReaderConfigurationID,
			AttributeList readerConfigurationProperties);

	/**
	 * Remove a Reader Configuration from the Edge Server
	 * 
	 * @param readerConfigurationID
	 *            the ID of the Reader Configuration
	 * @throws RemoteException
	 */
	void deleteReaderConfiguration(String readerConfigurationID)
			throws RemoteException;

	/**
	 * Get the available command configuration factories for a particular reader
	 * configuration factory (i.e. reader 'plugin'). This corresponds to all of
	 * the commands that may run.
	 * 
	 * @return a map where the key is an ID of a command configuration factory
	 *         and the value is the ID of a reader configuration factory to
	 *         which the command configuration belongs
	 * @throws RemoteException
	 */
	Map<String, String> getAvailableCommandConfigurationFactories()
			throws RemoteException;

	/**
	 * Get the currently defined command configurations that will run with
	 * commands produced by a given factory
	 * 
	 * @return a map where the key is the ID of a command configuration and the
	 *         value is the ID of the command configuration factory which
	 *         produced the command configuration
	 */
	Map<String, String> getAvailableCommandConfigurations(
			String readerConfigurationFactoryID) throws RemoteException;

	/**
	 * Gets a description of a Command configuration. This will return the
	 * information necessary to create a new command configuration.
	 * 
	 * @param commandConfigurationFactoryID
	 *            the ID of the commandFactory to get the description of
	 * @return an MBeanInfo whose attributes describe each property of the
	 *         command configuration
	 * @throws RemoteException
	 */
	MBeanInfo getCommandConfigurationDescription(
			String commandConfigurationFactoryID) throws RemoteException;

	/**
	 * Create a new command Configuration. The ID of the configuration can be
	 * used in conjunction with the ID of the readerConfiguration to start a
	 * reader session
	 * 
	 * @param commandConfigurationFactoryID
	 *            the ID of the commandConfigurationFactory to use
	 * @param commandConfigurationProperties
	 *            all the properties to set on the new command configuration
	 * @return the ID of the newly created CommandConfiguration
	 * @throws RemoteException
	 */
	String createCommandConfiguraion(String commandConfigurationFactoryID,
			AttributeList commandConfigurationProperties) throws RemoteException;

	/**
	 * This method returns an Attribute list that contains all the values for
	 * the properties of a CommandConfiguration
	 * 
	 * @param commandConfigurationID
	 *            the ID of the CommandConfiguration to get
	 * @return the current values for a command configuration
	 * @throws RemoteException
	 */
	AttributeList getCommandConfiguration(String commandConfigurationID)
			throws RemoteException;

	/**
	 * This method allows you to update the properties of a CommandConfiguration
	 * 
	 * @param CommandConfigurationID
	 *            The ID of the CommandConfiguration to update
	 * @param commandConfigurationProperties
	 *            The new properties for the command configuration. It must
	 *            contain the properties that are changing, and may contain the
	 *            properties that will stay the same
	 * @return The complete set of properties for the CommandConfiguration
	 */
	AttributeList setCommandConfigurationProperties(
			String CommandConfigurationID,
			AttributeList commandConfigurationProperties);

	/**
	 * Delete a command factory
	 * 
	 * @param commandConfigurationName
	 *            the name of the factory to delete
	 * @throws RemoteException
	 */
	void deleteCommandFactory(String commandConfigurationName)
			throws RemoteException;

	/**
	 * This method gets all the Sessions currently available on the reader. The
	 * key is the ID of the session. The List has three elements. The first is
	 * the ID of the readerconfiguration on the session. The second is the ID of
	 * the commandconfiguration on the session. The third is the state of the
	 * session
	 * 
	 * @return A map whose key is the ID of the session and whose value contains
	 *         information about the session as defined above
	 */
	Map<String, List<String>> getReaderSessions() throws RemoteException;

	/**
	 * This method changes the readerConfiguration and/or commandConfiguration
	 * on a given readerSession
	 * 
	 * @param readerSessionID
	 *            the ID of the session to modify
	 * @param readerConfigurationID
	 *            the ID of the readerconfiguration to set on this session, or
	 *            null if it is not changing
	 * @param commandConfigurationID
	 *            the ID of the commandconfiguration to set on this session, or
	 *            null if it is not changing
	 * @return true if the set was successful, false if there was a problem
	 * @throws RemoteException
	 */
	boolean setSessionReaderConfiguration(String readerSessionID,
			String readerConfigurationID, String commandConfigurationID)
			throws RemoteException;

	/**
	 * Start a session. This method will create a new reader session and start a
	 * new command on it.
	 * 
	 * @param readerConfigurationName
	 *            The readerConfiguration to use
	 * @param commandFactorynName
	 *            The name of the commandFactory that will create a new command
	 *            for the session to use
	 * @return the ID of the newly created session
	 * @throws RemoteException
	 */
	String startReaderSession(String readerConfigurationName,
			String commandFactoryName) throws RemoteException;

	/**
	 * Stop a currently executing reader session
	 * 
	 * @param readerSessionName
	 *            the name of the session to stop
	 * @throws RemoteException
	 */
	void stopReaderSession(String readerSessionName) throws RemoteException;

}

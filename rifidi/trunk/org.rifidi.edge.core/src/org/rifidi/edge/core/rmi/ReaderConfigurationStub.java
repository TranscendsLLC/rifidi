/**
 * 
 */
package org.rifidi.edge.core.rmi;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.Set;

import javax.management.AttributeList;
import javax.management.MBeanInfo;

/**
 * This is the interface for a stub that allows users to interact with reader
 * configurations on the edge server
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface ReaderConfigurationStub {

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
	 * information necessary to construct a AbstractReaderConfiguration.
	 * 
	 * @param readerConfigurationFactoryID
	 *            the ID of the ReaderConfigurationFactory
	 * @return an MBeanInfo object whose attributes describe how to create a
	 *         AbstractReaderConfiguration
	 * @throws RemoteException
	 */
	MBeanInfo getReaderConfigurationDescription(
			String readerConfigurationFactoryID) throws RemoteException;

	/**
	 * Creates a new AbstractReaderConfiguration for the supplied configuration. The ID
	 * of the readerConfiguration can be used along with the ID of a command
	 * configuration to start a reader session
	 * 
	 * @param readerConfigurationFactoryID
	 *            the ID of the reader configuration factory
	 * @param readerConfigurationProperties
	 *            all the properties to set on the new AbstractReaderConfiguration
	 * @return the ID of the newly created AbstractReaderConfiguration
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
	 * for all the properties of a currently available AbstractReaderConfiguration.
	 * 
	 * @param readerConfigurationID
	 *            The ID of the AbstractReaderConfiguration to get
	 * @return The current values for a reader configuration
	 * @throws RemoteException
	 */
	AttributeList getReaderConfigurationProperties(String readerConfigurationID)
			throws RemoteException;

	/**
	 * This method is used to update the properties of a AbstractReaderConfiguration. It
	 * may contain only the properties that you intend to change, but may
	 * contain values that will stay the same
	 * 
	 * @param readerConfigurationID
	 *            the ID of the AbstractReaderConfiguration to update
	 * @param readerConfigurationProperties
	 *            the new properties to set
	 * @return The properties of the reader that were set
	 */
	AttributeList setReaderConfigurationProperties(
			String readerConfigurationID,
			AttributeList readerConfigurationProperties) throws RemoteException;

	/**
	 * Remove a Reader Configuration from the Edge Server
	 * 
	 * @param readerConfigurationID
	 *            the ID of the Reader Configuration
	 * @throws RemoteException
	 */
	void deleteReaderConfiguration(String readerConfigurationID)
			throws RemoteException;

}

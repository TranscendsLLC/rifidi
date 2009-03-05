/**
 * 
 */
package org.rifidi.edge.core.rmi;

import java.io.Reader;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Set;

import javax.management.AttributeList;
import javax.management.MBeanInfo;

import org.rifidi.edge.core.readers.AbstractReader;

/**
 * This is the interface for a stub that allows users to interact with readerSession
 * configurations on the edge server
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface ReaderConfigurationStub extends Remote {

	/**
	 * These are the readerSession "plugins" that are available on the server
	 * 
	 * @return a list of IDs of ReaderSession Configuration Factories that are
	 *         currently available
	 */
	Set<String> getAvailableReaderConfigurationFactories()
			throws RemoteException;

	/**
	 * This method gets the readerSession configurations that are currently available
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
	 * Gets a description of a readerSession configuration. This will contain the
	 * information necessary to construct a AbstractReader.
	 * 
	 * @param readerConfigurationFactoryID
	 *            the ID of the ReaderConfigurationFactory
	 * @return an MBeanInfo object whose attributes describe how to create a
	 *         AbstractReader
	 * @throws RemoteException
	 */
	MBeanInfo getReaderConfigurationDescription(
			String readerConfigurationFactoryID) throws RemoteException;

	/**
	 * Creates a new AbstractReader for the supplied configuration.
	 * The ID of the readerConfiguration can be used along with the ID of a
	 * command configuration to start a readerSession session
	 * 
	 * @param readerConfigurationFactoryID
	 *            the ID of the readerSession configuration factory
	 * @param readerConfigurationProperties
	 *            all the properties to set on the new
	 *            AbstractReader
	 * @return the ID of the newly created AbstractReader
	 * @throws RemoteException
	 */
	String createReaderConfiguration(String readerConfigurationFactoryID,
			AttributeList readerConfigurationProperties) throws RemoteException;

	/**
	 * This method gets all the properties for all of the currently available
	 * Configuration Properties. It should be used with caution: if there are a
	 * large number of readerSession configurations available, this method call could
	 * be expensive
	 * 
	 * @return A map where the key is the ID of the readerSession configuration and the
	 *         value is an AttributeList that has all the name-value pairs for
	 *         all of the properties
	 * @throws RemoteException
	 */
	Map<String, AttributeList> getAllReaderConfigurationProperties()
			throws RemoteException;

	/**
	 * This method returns an AttributeList that contains the name-value pairs
	 * for all the properties of a currently available
	 * AbstractReader.
	 * 
	 * @param readerConfigurationID
	 *            The ID of the AbstractReader to get
	 * @return The current values for a readerSession configuration
	 * @throws RemoteException
	 */
	AttributeList getReaderConfigurationProperties(String readerConfigurationID)
			throws RemoteException;

	/**
	 * This method is used to update the properties of a
	 * AbstractReader. It may contain only the properties that you
	 * intend to change, but may contain values that will stay the same
	 * 
	 * @param readerConfigurationID
	 *            the ID of the AbstractReader to update
	 * @param readerConfigurationProperties
	 *            the new properties to set
	 * @return The properties of the readerSession that were set
	 */
	AttributeList setReaderConfigurationProperties(
			String readerConfigurationID,
			AttributeList readerConfigurationProperties) throws RemoteException;

	/**
	 * Remove a ReaderSession Configuration from the Edge Server
	 * 
	 * @param readerConfigurationID
	 *            the ID of the ReaderSession Configuration
	 * @throws RemoteException
	 */
	void deleteReaderConfiguration(String readerConfigurationID)
			throws RemoteException;
	
	/**
	 * TEMP METHOD SHOULD BE REFACTORED OUT
	 * @param ID
	 * @return
	 */
	AbstractReader<?> getReader(String ID);

}

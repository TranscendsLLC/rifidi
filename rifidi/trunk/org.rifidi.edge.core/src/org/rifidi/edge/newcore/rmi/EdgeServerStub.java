package org.rifidi.edge.newcore.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

import javax.management.AttributeList;
import javax.management.MBeanInfo;

/**
 * This is the interface for the Edge Server RMI Stub.  
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface EdgeServerStub extends Remote {

	/**
	 * 
	 * @return a list of names of reader plugins that are currently available
	 */
	Set<String> getAvailableReaderPlugins() throws RemoteException;

	/**
	 * Gets a description of a reader plugin. This will contain the information
	 * necessary to construct a ReaderConfiguration
	 * 
	 * @param readerPluginName
	 *            the name of the reader plugin.
	 * @return an MBeanInfo object whose attributes describe how to create a
	 *         ReaderConfiguration
	 * @throws RemoteException
	 */
	MBeanInfo getReaderConfigurationDescription(String readerPluginName)
			throws RemoteException;

	/**
	 * Creates a new ReaderConfiguration for the supplied configuration. The
	 * name of the readerConfiguration can be used along with the name of a
	 * command factory to start a reader session
	 * 
	 * @param readerConfiguration
	 *            all the properties to set on the new ReaderConfiguration
	 * @return the name of the readerConfiguration
	 * @throws RemoteException
	 */
	String createReaderConfiguration(AttributeList readerConfiguration)
			throws RemoteException;

	/**
	 * Remove a Reader Configuration from the Edge Server
	 * 
	 * @param readerConfigurationName
	 *            the name of the reader Configuration
	 * @throws RemoteException
	 */
	void deleteReaderConfiguration(String readerConfigurationName)
			throws RemoteException;

	/**
	 * Get the available commands for a reader plugin
	 * 
	 * @param readerPluginName
	 *            the name of the reader plugin
	 * @return a list of command names
	 * @throws RemoteException
	 */
	Set<String> getAvailableCommandFactories(String readerPluginName)
			throws RemoteException;

	/**
	 * Gets a description of a Command. This will return the information
	 * necessary to create a new commandFactory
	 * 
	 * @param commandFactoryName
	 *            the name of the commandFactory to get the description of
	 * @return an MBeanInfo whose attributes describe each property of the
	 *         command
	 * @throws RemoteException
	 */
	MBeanInfo getCommandConfigurationDescription(String commandFactoryName)
			throws RemoteException;

	/**
	 * Create a new command factory. The name of the factory can be used in
	 * conjunction with the name of the readerConfiguration to start a reader
	 * session
	 * 
	 * @param commandConfiguration
	 *            all the properties to set on the new command factory
	 * @return
	 * @throws RemoteException
	 */
	String createCommandFactory(AttributeList commandConfiguration)
			throws RemoteException;

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
	 * Start a session. This method will create a new reader session and start a
	 * new command on it.
	 * 
	 * @param readerConfigurationName
	 *            The readerConfiguration to use
	 * @param commandFactorynName
	 *            The name of the commandFactory that will create a new command
	 *            for the session to use
	 * @return the name of the newly created session
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

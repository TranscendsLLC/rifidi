/**
 * 
 */
package org.rifidi.edge.core.api.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.management.AttributeList;
import javax.management.MBeanInfo;

import org.rifidi.edge.core.api.rmi.dto.ReaderDTO;
import org.rifidi.edge.core.api.rmi.dto.SessionDTO;

/**
 * This is the interface for a stub that allows users to interact with readers
 * on the edge server
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface ReaderStub extends Remote {

	/**
	 * These are the ReaderFactories that are available on the server
	 * 
	 * @return a list of IDs of ReaderFactories that are currently available
	 */
	Set<String> getReaderFactories() throws RemoteException;

	/**
	 * This method gets the readers that are currently available on the server
	 * 
	 * @return a Data Transfer Object that contains all the information about a
	 *         reader for all the currently available readers
	 * @throws RemoteException
	 */
	Set<ReaderDTO> getReaders() throws RemoteException;

	/**
	 * Gets a description of a Reader. This will contain the information
	 * necessary to construct a new instance of the reader.
	 * 
	 * @param readerFactoryID
	 *            the ID of the ReaderFactory
	 * @return an MBeanInfo object whose attributes describe how to create a
	 *         Reader
	 * @throws RemoteException
	 */
	MBeanInfo getReaderDescription(String readerFactoryID)
			throws RemoteException;

	/**
	 * Creates a new Reader for the supplied configuration.
	 * 
	 * @param readerFactoryID
	 *            the ID of the ReaderRactory
	 * @param readerProperties
	 *            all the properties to set on the new Reader
	 * @return the ID of the newly created Reader
	 * @throws RemoteException
	 */
	String createReader(String readerFactoryID, AttributeList readerProperties)
			throws RemoteException;

	/**
	 * This method returns an AttributeList that contains the name-value pairs
	 * for all the properties of a currently available Reader.
	 * 
	 * @param readerID
	 *            The ID of the Reader to get
	 * @return The current values for a Reader
	 * @throws RemoteException
	 */
	AttributeList getReaderProperties(String readerID) throws RemoteException;

	/**
	 * This method is used to update the properties of a Reader. It may contain
	 * only the properties that you intend to change, but may contain values
	 * that will stay the same
	 * 
	 * @param readerID
	 *            the ID of the Reader to update
	 * @param readerProperties
	 *            the new properties to set
	 */
	void setReaderProperties(String readerID, AttributeList readerProperties)
			throws RemoteException;

	/**
	 * Remove a Reader from the Edge Server
	 * 
	 * @param readerID
	 *            the ID of the Reader
	 * @throws RemoteException
	 */
	void deleteReader(String readerID) throws RemoteException;

	/**
	 * Create a new session on the reader.
	 * 
	 * @param The
	 *            list of sessions currently on this reader
	 * @throws RemoteException
	 */
	List<SessionDTO> createSession(String readerID) throws RemoteException;

	/**
	 * Starts a session on the reader
	 * 
	 * @param readerID
	 *            The reader to use
	 * @param sessionIndex
	 *            The session to start
	 * @throws RemoteException
	 */
	void startSession(String readerID, Integer sessionIndex)
			throws RemoteException;

	/**
	 * Stops a session
	 * 
	 * @param readerID
	 *            The reader to use
	 * @param sessionIndex
	 *            The session to stop
	 * @throws RemoteException
	 */
	void stopSession(String readerID, Integer sessionIndex)
			throws RemoteException;

	/**
	 * Submit a command for execution on the reader
	 * 
	 * @param readerID
	 *            The reader to use
	 * @param sessionIndex
	 *            The session to use
	 * @param commandID
	 *            The command to use
	 * @param repeatInterval
	 *            How often the command should be repeated. If 0, this command
	 *            will only be executed once
	 * @param timeUnit
	 *            Which time unit to use for the repeatInterval. Ignored if
	 *            repeatInterval is set to 0
	 * @return The processID of the submitted command
	 */
	Integer submitCommand(String readerID, Integer sessionIndex,
			String commandID, Long repeatInterval, TimeUnit timeUnit);

	/**
	 * Stop a repeated command. The is removed from the executor and will not be
	 * repeatedly executed
	 * 
	 * @param readerID
	 *            The reader to use
	 * @param sessionIndex
	 *            The session to use
	 * @param processID
	 *            The command to kill
	 */
	void killCommand(String readerID, Integer sessionIndex, Integer processID);

}

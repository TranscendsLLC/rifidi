/**
 * 
 */
package org.rifidi.edge.client.sal.controller.edgeserver;

import java.util.Set;

import javax.management.AttributeList;

import org.rifidi.edge.client.model.sal.RemoteCommandConfiguration;
import org.rifidi.edge.client.model.sal.RemoteJob;
import org.rifidi.edge.client.model.sal.RemoteReaderFactory;
import org.rifidi.edge.client.model.sal.RemoteSession;

/**
 * An interface for controlling a RemoteEdgeServer
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface EdgeServerController {

	/**
	 * Update the edge server
	 */
	void update();

	/**
	 * Connect to the edge server
	 */
	void connect();

	/**
	 * Disconnect from the edge server
	 */
	void disconnect();

	/**
	 * Save configuration on server
	 */
	void saveConfiguration();

	/**
	 * @return The available reader factories. Returns a copy.
	 */
	Set<RemoteReaderFactory> getReaderfactories();

	/**
	 * Create a new reader
	 * 
	 * @param factory
	 *            The factory to use
	 * @param attributes
	 *            The attributes to set on the new reader
	 */
	void createReader(RemoteReaderFactory factory, AttributeList attributes);

	/**
	 * Delete a reader
	 * 
	 * @param readerID
	 */
	void deleteReader(String readerID);

	/**
	 * Create a session
	 * 
	 * @param readerID
	 */
	void createSession(String readerID);

	/**
	 * Delete a session
	 * 
	 * @param readerID
	 * @param sessionID
	 */
	void deleteSession(String readerID, String sessionID);

	/**
	 * Start a session
	 * 
	 * @param readerID
	 * @param sessionID
	 */
	void startSession(String readerID, String sessionID);

	/**
	 * Stop a session
	 * 
	 * @param readerID
	 * @param sessionID
	 */
	void stopSession(String readerID, String sessionID);

	/**
	 * Delete the Remote job
	 * 
	 * @param job
	 */
	void deleteRemoteJob(RemoteJob job);

	void scheduleJob(RemoteSession session,
			RemoteCommandConfiguration configuration, Long interval);
	
	void clearPropertyChanges(String readerID);
	
	void synchPropertyChanges(String readerID);

}

package org.rifidi.edge.core.readersession.service;

import java.util.List;

import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readersession.ReaderSession;

/**
 * The ReaderSessionService Interface describes the minimal set of a
 * ReaderSesssionService. This service is used for creating new ReaderSession,
 * deleting them again and monitor the process of creating and removal.
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public interface ReaderSessionService {

	/**
	 * Create a new ReaderSession
	 * 
	 * @param readerInfo
	 *            specific instance of a ReaderInfo for a specific ReaderPlugin
	 *            describing the properties of the ReaderSession to create
	 * @return a ReaderSession
	 */
	public ReaderSession createReaderSession(ReaderInfo readerInfo);

	/**
	 * Remove a previously created ReaderSession again
	 * 
	 * @param readerSession
	 *            the ReaderSession to remove
	 */
	public void destroyReaderSession(ReaderSession readerSession);

	/**
	 * Get a list containing all ReaderSessions know to this services
	 * 
	 * @return list of ReaderSessions
	 */
	public List<ReaderSession> getAllReaderSessions();

	/**
	 * Add a new Listener to this Service.
	 * 
	 * @param listener
	 *            the listener to add
	 */
	public void addReaderSessionListener(ReaderSessionListener listener);

	/**
	 * Remove a previous added Listener from this Service
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void removeReaderSessionListener(ReaderSessionListener listener);
	
	
	/**
	 * Add a new Listener to this Service.
	 * 
	 * @param listener
	 *            the listener to add
	 */
	public void addReaderSessionAutoUnloadListener(ReaderSessionAutoUnloadListener listener);

	/**
	 * Remove a previous added Listener from this Service
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void removeReaderSessionAutoUnloadListener(ReaderSessionAutoUnloadListener listener);
}

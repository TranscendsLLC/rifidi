package org.rifidi.edge.core.readersession.service;

import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readersession.ReaderSession;

/**
 * Listener for events in the ReaderSessionService. There are notifications for
 * adding and removing ReaderSessions.
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public interface ReaderSessionListener {

	/**
	 * New ReaderSession added event
	 * 
	 * @param readerSession
	 *            the ReaderSession being added
	 */
	public void addReaderSessionEvent(ReaderSession readerSession);

	/**
	 * Fires when a reader session is removed due to a user deleting it
	 * 
	 * @param readerSession
	 *            the ReaderSession being removed
	 */
	public void removeReaderSessionEvent(ReaderSession readerSession);

	/**
	 * Fires when a reader session is removed due to a reader plugin being
	 * unloaded
	 * 
	 * @param readerSession
	 */
	public void autoRemoveReaderSessionEvent(ReaderSession readerSession);

	/**
	 * This event is fired when a readerInfo is modified while the reader
	 * session is in the CONFIGURED state
	 * 
	 * @param readerSession
	 */
	public void readerInfoEditedEvent(ReaderInfo oldReaderInfo, ReaderInfo newReaderInfo);
}

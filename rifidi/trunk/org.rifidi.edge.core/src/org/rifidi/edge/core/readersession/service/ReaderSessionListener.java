package org.rifidi.edge.core.readersession.service;

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
	 * @param readerSession the ReaderSession being added
	 */
	public void addEvent(ReaderSession readerSession);

	/**
	 * Remove ReaderSession event
	 * 
	 * @param readerSession the ReaderSession being removed
	 */
	public void removeEvent(ReaderSession readerSession);
}

package org.rifidi.edge.core.readersession.service;

import java.util.List;

import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readersession.ReaderSession;

/**
 * @author Andreas Huebner - andreas@pramari.com
 *
 */
public interface ReaderSessionService {

	/**
	 * @param readerInfo
	 * @return
	 */
	public ReaderSession createReaderSession(ReaderInfo readerInfo);

	/**
	 * @param readerSession
	 */
	public void destroyReaderSession(ReaderSession readerSession);

	/**
	 * @return
	 */
	public List<ReaderSession> getAllReaderSessions();

	/**
	 * @param listener
	 */
	public void addReaderSessionListener(ReaderSessionListener listener);

	/**
	 * @param listener
	 */
	public void removeReaderSessionListener(ReaderSessionListener listener);
}

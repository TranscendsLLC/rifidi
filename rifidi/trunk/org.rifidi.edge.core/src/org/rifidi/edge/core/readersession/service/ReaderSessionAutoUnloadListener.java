package org.rifidi.edge.core.readersession.service;

import org.rifidi.edge.core.readersession.ReaderSession;

/**
 * @author Jerry Maine - jerry@pramari.com
 *
 */
public interface ReaderSessionAutoUnloadListener {
	public void autoRemoveEvent(ReaderSession readerSession);
}

package org.rifidi.edge.core.readersession.service;

import org.rifidi.edge.core.readersession.ReaderSession;

public interface ReaderSessionListener {
	public void addEvent(ReaderSession readerSession);
	
	public void removeEvent(ReaderSession readerSession);
}

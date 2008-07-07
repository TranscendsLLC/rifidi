package org.rifidi.edge.core.readersession.service;

import java.util.List;

import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readersession.ReaderSession;

public interface ReaderSessionService {

	public ReaderSession createReaderSesssion(ReaderInfo readerInfo);

	public void destroyReaderSession(ReaderSession readerSession);

	public List<ReaderSession> getAllReaderSessions();

	public void addReaderSessionListener(ReaderSessionListener listener);

	public void removeReaderSessionListener(ReaderSessionListener listener);
}

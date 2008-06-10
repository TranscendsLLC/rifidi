package org.rifidi.edge.core.session;

import java.util.List;

import org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo;
import org.rifidi.edge.core.readerAdapter.IReaderAdapter;

public interface SessionRegistryService {

	public void initialize();

	public Session createReaderSession(AbstractConnectionInfo abstractConnectionInfo);

	public Session getReaderSession(int sessionID);
	
	public boolean containsReaderSession(int sessionID);

	public void deleteReaderSession(int sessionID);

	public List<IReaderAdapter> getAllReaderSessions();
	
	public int sessionCount();

}
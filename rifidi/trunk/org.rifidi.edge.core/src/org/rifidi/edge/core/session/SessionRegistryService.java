package org.rifidi.edge.core.session;

import java.util.List;

import org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo;
import org.rifidi.edge.core.readerAdapter.IReaderAdapter;

public interface SessionRegistryService {

	public abstract void initialize();

	public abstract int createReaderSession(AbstractConnectionInfo abstractConnectionInfo);

	public abstract Session getReaderSession(int sessionID);

	public abstract void deleteReaderSession(int sessionID);

	public abstract List<IReaderAdapter> getAllReaderSessions();

}
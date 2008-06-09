package org.rifidi.edge.core.session;

import java.util.List;

import org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo;
import org.rifidi.edge.core.readerAdapter.IReaderAdapter;

public interface SessionRegistryService {

	public abstract void intalize();

	public abstract int createReaderSession(AbstractConnectionInfo pattern);

	public abstract void getReaderSession(int apapterID);

	public abstract void deleteReaderSession(int apapterID);

	public abstract List<IReaderAdapter> getAllReaderSessions();

}
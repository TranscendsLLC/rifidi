package org.rifidi.edge.core.session;

import org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo;


public interface IRemoteSessionRegistryService {
	public ISession createReaderSession(AbstractConnectionInfo abstractConnectionInfo);
	public ISession getReaderSession(int sessionID);
	//public Session getReaderSession (int adapterID);
}

package org.rifidi.edge.core.connection;

import java.util.List;

import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;
import org.rifidi.edge.core.readerPlugin.IReaderPlugin;

public interface ReaderConnectionRegistryService {

	public void initialize();

	public ReaderConnection createReaderSession(AbstractReaderInfo abstractConnectionInfo);

	public ReaderConnection getReaderSession(int sessionID);
	
	public boolean containsReaderSession(int sessionID);

	public void deleteReaderSession(int sessionID);

	public List<IReaderPlugin> getAllReaderSessions();
	
	public int sessionCount();

}
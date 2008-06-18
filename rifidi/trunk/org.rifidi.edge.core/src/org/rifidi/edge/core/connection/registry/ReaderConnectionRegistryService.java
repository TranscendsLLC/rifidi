package org.rifidi.edge.core.connection.registry;

import java.util.List;

import org.rifidi.edge.core.connection.IReaderConnection;
import org.rifidi.edge.core.connection.ReaderConnection;
import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;

public interface ReaderConnectionRegistryService {

	public void initialize();

	public IReaderConnection createReaderConnection(AbstractReaderInfo abstractConnectionInfo);

	public IReaderConnection getReaderConnection(int readerConnectionID);
	
	public void deleteReaderConnection(int readerConnectionID);
	
	public void deleteReaderConnection(IReaderConnection readerConnection);

	public List<ReaderConnection> getAllReaderConnections();

}
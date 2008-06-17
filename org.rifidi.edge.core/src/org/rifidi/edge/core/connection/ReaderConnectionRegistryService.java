package org.rifidi.edge.core.connection;

import java.util.List;

import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;

public interface ReaderConnectionRegistryService {

	public void initialize();

	public ReaderConnection createReaderConnection(AbstractReaderInfo abstractConnectionInfo);

	public ReaderConnection getReaderConnection(int readerConnectionID);
	
	public void deleteReaderConnection(int readerConnectionID);
	
	public void deleteReaderConnection(ReaderConnection readerConnection);

	public List<ReaderConnection> getAllReaderConnections();

}
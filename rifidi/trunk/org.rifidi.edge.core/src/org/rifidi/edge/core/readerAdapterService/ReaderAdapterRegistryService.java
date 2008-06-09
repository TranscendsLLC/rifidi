package org.rifidi.edge.core.readerAdapterService;

import org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo;
import org.rifidi.edge.core.readerAdapter.ISpecificReaderAdapterFactory;

public interface ReaderAdapterRegistryService {

	public void registerReaderAdapter(Class<?> specConnectionInfo,
			ISpecificReaderAdapterFactory pattern2);

	public void unregisterReaderAdapter(Class<?> specConnnectionInfo);
	
	public void getSpecReaderAdapterFactory(
			AbstractConnectionInfo pattern);

	public void registerListener();

	public void unregisterListener();

}
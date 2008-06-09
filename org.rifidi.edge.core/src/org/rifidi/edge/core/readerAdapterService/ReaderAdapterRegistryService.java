package org.rifidi.edge.core.readerAdapterService;

import org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo;
import org.rifidi.edge.core.readerAdapter.ISpecificReaderAdapterFactory;

public interface ReaderAdapterRegistryService {

	public void registerReaderAdapter(Class<?> specificConnectionInfo,
			ISpecificReaderAdapterFactory specificReaderAdapterFactory);

	public void unregisterReaderAdapter(Class<?> specificConnnectionInfo);

	public void getSpecReaderAdapterFactory(
			AbstractConnectionInfo specificConnectionInfo);

	public void registerListener();

	public void unregisterListener();

}
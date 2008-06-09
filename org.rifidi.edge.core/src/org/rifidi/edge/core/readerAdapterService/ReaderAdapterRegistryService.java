package org.rifidi.edge.core.readerAdapterService;

import org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo;
import org.rifidi.edge.core.readerAdapter.ISpecificReaderAdapterFactory;

public interface ReaderAdapterRegistryService {

	public abstract void registerReaderAdapter(AbstractConnectionInfo pattern1,
			ISpecificReaderAdapterFactory pattern2);

	public abstract void getSpecReaderAdapterFactory(
			AbstractConnectionInfo pattern);

	public abstract void registerListener();

	public abstract void unregisterListener();

}
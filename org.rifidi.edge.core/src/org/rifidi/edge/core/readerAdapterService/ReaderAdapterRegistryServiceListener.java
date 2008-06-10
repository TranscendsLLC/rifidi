package org.rifidi.edge.core.readerAdapterService;

import org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo;
import org.rifidi.edge.core.readerAdapter.ISpecificReaderAdapterFactory;

public interface ReaderAdapterRegistryServiceListener {
	public void readerAdapterRegistryServiceListener(Class<? extends AbstractConnectionInfo> specificConnectionInfo,
			ISpecificReaderAdapterFactory specificReaderAdapterFactory);
}
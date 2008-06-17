package org.rifidi.edge.core.readerPluginService;

import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;
import org.rifidi.edge.core.readerPlugin.ISpecificReaderPluginFactory;

public interface ReaderAdapterRegistryServiceListener {
	public void readerAdapterRegistryServiceListener(Class<? extends AbstractReaderInfo> specificConnectionInfo,
			ISpecificReaderPluginFactory specificReaderAdapterFactory);
}
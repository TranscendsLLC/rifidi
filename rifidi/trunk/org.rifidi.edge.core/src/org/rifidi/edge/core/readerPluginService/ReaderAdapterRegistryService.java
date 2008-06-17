package org.rifidi.edge.core.readerPluginService;

import java.util.List;

import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;
import org.rifidi.edge.core.readerPlugin.ISpecificReaderPluginFactory;

public interface ReaderAdapterRegistryService {

	public void registerReaderAdapter(Class<? extends AbstractReaderInfo> specificConnectionInfo,
			ISpecificReaderPluginFactory specificReaderAdapterFactory);

	public void unregisterReaderAdapter(Class<? extends AbstractReaderInfo> specificConnectionInfo);

	public ISpecificReaderPluginFactory getSpecReaderAdapterFactory(
			AbstractReaderInfo specificConnectionInfo);
	
	public List<String> getAvailableReaderAdapters();

}
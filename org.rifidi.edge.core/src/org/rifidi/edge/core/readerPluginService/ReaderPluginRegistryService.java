package org.rifidi.edge.core.readerPluginService;

import java.util.List;

import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;
import org.rifidi.edge.core.readerPlugin.ISpecificReaderPluginFactory;

public interface ReaderPluginRegistryService {

	/**
	 * Register a reader adapter
	 * @param specificConnectionInfo The reader adapter to register
	 * @param specificReaderAdapterFactory Its associated factory
	 */
	public void registerReaderAdapter(Class<? extends AbstractReaderInfo> specificConnectionInfo,
			ISpecificReaderPluginFactory specificReaderAdapterFactory);

	/**
	 * Unregister a reader adapter
	 * @param specificConnectionInfo The class of the reader connection info to unregister
	 */
	public void unregisterReaderAdapter(Class<? extends AbstractReaderInfo> specificConnectionInfo);

	/**
	 * Look up a registered reader adapter's associated factory
	 * @param specificConnectionInfo The reader adapter's connection info
	 * @return
	 */
	public ISpecificReaderPluginFactory getSpecReaderAdapterFactory(
			AbstractReaderInfo specificConnectionInfo);
	
	
	/**
	 * @return The list of available reader adapters
	 */
	public List<String> getAvailableReaderAdapters();

}
package org.rifidi.edge.core.readerPluginService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;
import org.rifidi.edge.core.readerPlugin.ISpecificReaderPluginFactory;

public class ReaderPluginRegistryServiceImpl implements
		ReaderPluginRegistryService {

	private Log logger = LogFactory
			.getLog(ReaderPluginRegistryServiceImpl.class);

	private HashMap<Class<? extends AbstractReaderInfo>, ISpecificReaderPluginFactory> registry = new HashMap<Class<? extends AbstractReaderInfo>, ISpecificReaderPluginFactory>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerAdapterService.ReaderAdapterRegistryService#registerReaderAdapter(java.lang.Class,
	 *      org.rifidi.edge.core.readerAdapter.ISpecificReaderAdapterFactory)
	 */
	@Override
	public void registerReaderAdapter(
			Class<? extends AbstractReaderInfo> specificConnectionInfo,
			ISpecificReaderPluginFactory specificReaderAdapterFactory) {
		logger.debug("ReaderAdapter " + specificConnectionInfo.getName()
				+ " registered");
		if (!registry.containsKey(specificConnectionInfo)) {
			registry.put(specificConnectionInfo, specificReaderAdapterFactory);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerAdapterService.ReaderAdapterRegistryService#unregisterReaderAdapter(java.lang.Class)
	 */
	@Override
	public void unregisterReaderAdapter(
			Class<? extends AbstractReaderInfo> specificConnectionInfo) {
		registry.remove(specificConnectionInfo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerAdapterService.ReaderAdapterRegistryService#getSpecReaderAdapterFactory(org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo)
	 */
	@Override
	public ISpecificReaderPluginFactory getSpecReaderAdapterFactory(
			AbstractReaderInfo specificConnectionInfo) {
		return registry.get(specificConnectionInfo.getClass());
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerPluginService.ReaderPluginRegistryService#getAvailableReaderAdapters()
	 */
	@Override
	public List<String> getAvailableReaderAdapters() {
		ArrayList<String> availableAdapters = new ArrayList<String>();
		for (Class<?> clazz : registry.keySet()) {
			availableAdapters.add(clazz.getName());
		}
		return availableAdapters;
	}

}

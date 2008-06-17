package org.rifidi.edge.core.readerPlugin;

import org.rifidi.edge.core.exception.readerConnection.RifidiReaderPluginCreationException;
import org.rifidi.edge.core.readerPluginService.ReaderPluginRegistryService;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

public class ReaderPluginFactory {

	private ReaderPluginRegistryService readerPluginRegistryService;

	public ReaderPluginFactory() {
		ServiceRegistry.getInstance().service(this);
	}

	public IReaderPlugin createReaderAdapter(
			AbstractReaderInfo abstractConnnectionInfo) {
		IReaderPlugin readerAdapter = null;

		if (readerPluginRegistryService != null) {
			ISpecificReaderPluginFactory factory = readerPluginRegistryService
					.getSpecReaderAdapterFactory(abstractConnnectionInfo);
			try {
				readerAdapter = factory
						.createSpecificReaderAdapter(abstractConnnectionInfo);
			} catch (RifidiReaderPluginCreationException e) {
				// TODO Catch or toss it up the call stack...
				e.printStackTrace();
			}
		}

		return readerAdapter;
	}

	/**
	 * @return the readerAdapterRegistryService
	 */
	public ReaderPluginRegistryService getReaderAdapterRegistryService() {
		return readerPluginRegistryService;
	}

	/**
	 * @param readerPluginRegistryService
	 *            the readerAdapterRegistryService to set
	 */
	@Inject
	public void setReaderAdapterRegistryService(
			ReaderPluginRegistryService readerPluginRegistryService) {
		this.readerPluginRegistryService = readerPluginRegistryService;
	}

}

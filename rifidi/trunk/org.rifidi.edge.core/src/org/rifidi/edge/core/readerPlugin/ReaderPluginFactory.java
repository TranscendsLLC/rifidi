package org.rifidi.edge.core.readerPlugin;

import org.rifidi.edge.core.exception.readerPlugin.RifidiReaderAdapterCreationException;
import org.rifidi.edge.core.readerPluginService.ReaderAdapterRegistryService;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;


public class ReaderPluginFactory {
	
	private static ReaderPluginFactory INSTANCE = new ReaderPluginFactory();
	
	private ReaderAdapterRegistryService readerAdapterRegistryService;
	
	public ReaderPluginFactory(){
		ServiceRegistry.getInstance().service(this);
	}
	
	

	public IReaderPlugin createReaderAdapter(
			AbstractReaderInfo abstractConnnectionInfo) {
		IReaderPlugin readerAdapter = null;
		
		if (readerAdapterRegistryService != null ){
			ISpecificReaderPluginFactory factory = readerAdapterRegistryService.getSpecReaderAdapterFactory(abstractConnnectionInfo);
			try {
				readerAdapter = factory.createSpecificReaderAdapter(abstractConnnectionInfo);
			} catch (RifidiReaderAdapterCreationException e) {
				// TODO Catch or toss it up the call stack...
				e.printStackTrace();
			}
		} 
			
		return readerAdapter;
	}

	public static ReaderPluginFactory getInstance()
	{
		return INSTANCE;
	}

	/**
	 * @return the readerAdapterRegistryService
	 */
	public ReaderAdapterRegistryService getReaderAdapterRegistryService() {
		return readerAdapterRegistryService;
	}



	/**
	 * @param readerAdapterRegistryService the readerAdapterRegistryService to set
	 */
	@Inject
	public void setReaderAdapterRegistryService(
			ReaderAdapterRegistryService readerAdapterRegistryService) {
		this.readerAdapterRegistryService = readerAdapterRegistryService;
	}

	
	
}

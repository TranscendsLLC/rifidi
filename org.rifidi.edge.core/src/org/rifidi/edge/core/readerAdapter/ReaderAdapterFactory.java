package org.rifidi.edge.core.readerAdapter;

import org.rifidi.edge.core.readerAdapterService.ReaderAdapterRegistryService;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;


public class ReaderAdapterFactory {
	
	private static ReaderAdapterFactory INSTANCE = new ReaderAdapterFactory();
	
	private ReaderAdapterRegistryService readerAdapterRegistryService;
	
	public ReaderAdapterFactory(){
		ServiceRegistry.getInstance().service(this);
	}
	
	

	public IReaderAdapter createReaderAdapter(
			AbstractConnectionInfo abstractConnnectionInfo) {
		IReaderAdapter readerAdapter = null;
		
		if (readerAdapterRegistryService != null ){
			ISpecificReaderAdapterFactory factory = readerAdapterRegistryService.getSpecReaderAdapterFactory(abstractConnnectionInfo);
			readerAdapter = factory.createSpecificReaderAdapter(abstractConnnectionInfo);
		} 
			
		return readerAdapter;
	}

	public static ReaderAdapterFactory getInstance()
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

package org.rifidi.edge.core.readerAdapter;

import org.rifidi.edge.core.exception.adapter.RifidiReaderAdapterCreationException;
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
			try {
				readerAdapter = factory.createSpecificReaderAdapter(abstractConnnectionInfo);
			} catch (RifidiReaderAdapterCreationException e) {
				// TODO Catch or toss it up the call stack...
				e.printStackTrace();
			}
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

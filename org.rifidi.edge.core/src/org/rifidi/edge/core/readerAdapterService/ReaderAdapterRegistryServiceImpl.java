package org.rifidi.edge.core.readerAdapterService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo;
import org.rifidi.edge.core.readerAdapter.ISpecificReaderAdapterFactory;

public class ReaderAdapterRegistryServiceImpl implements
		ReaderAdapterRegistryService {

	private Log logger = LogFactory.getLog(ReaderAdapterRegistryServiceImpl.class);
	
	private HashMap<Class<? extends AbstractConnectionInfo>, ISpecificReaderAdapterFactory> registry = new HashMap<Class<? extends AbstractConnectionInfo>, ISpecificReaderAdapterFactory>();
	
	private List<ReaderAdapterRegistryServiceListener> listeners = new ArrayList<ReaderAdapterRegistryServiceListener>();

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerAdapterService.ReaderAdapterRegistryService#registerReaderAdapter(java.lang.Class, org.rifidi.edge.core.readerAdapter.ISpecificReaderAdapterFactory)
	 */
	@Override
	public void registerReaderAdapter(Class<? extends AbstractConnectionInfo> specificConnectionInfo,
			ISpecificReaderAdapterFactory specificReaderAdapterFactory) {
		logger.debug("ReaderAdapter " + specificConnectionInfo.getName() + " registered");
		if(! registry.containsKey(specificConnectionInfo))
		{
			registry.put(specificConnectionInfo, specificReaderAdapterFactory);
		}
		for (ReaderAdapterRegistryServiceListener listener: listeners ){
			listener.readerAdapterRegistryServiceListener(specificConnectionInfo, specificReaderAdapterFactory);
		}
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerAdapterService.ReaderAdapterRegistryService#unregisterReaderAdapter(java.lang.Class)
	 */
	@Override
	public void unregisterReaderAdapter(Class<? extends AbstractConnectionInfo> specificConnectionInfo) {
		
		ISpecificReaderAdapterFactory specificReaderAdapterFactory = registry.get(specificConnectionInfo);
		
		for (ReaderAdapterRegistryServiceListener listener: listeners ){
			listener.readerAdapterRegistryServiceListener(specificConnectionInfo, specificReaderAdapterFactory);
		}
		
		registry.remove(specificConnectionInfo);
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerAdapterService.ReaderAdapterRegistryService#getSpecReaderAdapterFactory(org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo)
	 */
	@Override
	public ISpecificReaderAdapterFactory getSpecReaderAdapterFactory(AbstractConnectionInfo specificConnectionInfo) {
		return registry.get(specificConnectionInfo.getClass());
	}

	@Override
	public List<String> getAvailableReaderAdapters() {
		ArrayList<String> availableAdapters = new ArrayList<String>();
		for(Class<?> clazz : registry.keySet())
		{
			availableAdapters.add(clazz.getName());
		}
		return availableAdapters;
	}
	
	@Override
	public boolean registerListener(ReaderAdapterRegistryServiceListener listener){
		//TODO not implemented yet
		if (listeners.contains(listener)){
			return false; // return false because it was not added.
		} else {
			listeners.add(listener);
			return true; //return true because we successfully added it.
		}
	}

	@Override
	public boolean unregisterListener(ReaderAdapterRegistryServiceListener listener) {
		//TODO not implemented yet
		if (listeners.contains(listener)) {
			listeners.remove(listener);
			return true; // return true because we successfully removed the listener
		} else {
			return false;
		}
	}

}

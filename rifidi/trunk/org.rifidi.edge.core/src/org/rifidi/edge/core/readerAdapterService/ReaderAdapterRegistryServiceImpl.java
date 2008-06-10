package org.rifidi.edge.core.readerAdapterService;

import java.util.HashMap;

import org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo;
import org.rifidi.edge.core.readerAdapter.ISpecificReaderAdapterFactory;

public class ReaderAdapterRegistryServiceImpl implements
		ReaderAdapterRegistryService {

	private HashMap<Class<? extends AbstractConnectionInfo>, ISpecificReaderAdapterFactory> registry = new HashMap<Class<? extends AbstractConnectionInfo>, ISpecificReaderAdapterFactory>();

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerAdapterService.ReaderAdapterRegistryService#registerReaderAdapter(java.lang.Class, org.rifidi.edge.core.readerAdapter.ISpecificReaderAdapterFactory)
	 */
	@Override
	public void registerReaderAdapter(Class<? extends AbstractConnectionInfo> specificConnectionInfo,
			ISpecificReaderAdapterFactory specificReaderAdapterFactory) {
		if(! registry.containsKey(specificConnectionInfo))
		{
			registry.put(specificConnectionInfo, specificReaderAdapterFactory);
		}

	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerAdapterService.ReaderAdapterRegistryService#unregisterReaderAdapter(java.lang.Class)
	 */
	@Override
	public void unregisterReaderAdapter(Class<? extends AbstractConnectionInfo> specificConnnectionInfo) {
		registry.remove(specificConnnectionInfo);
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerAdapterService.ReaderAdapterRegistryService#getSpecReaderAdapterFactory(org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo)
	 */
	@Override
	public ISpecificReaderAdapterFactory getSpecReaderAdapterFactory(AbstractConnectionInfo specificConnectionInfo) {
		return registry.get(specificConnectionInfo.getClass());
	}

	@Override
	public void registerListener() {
		//TODO not implemented yet
	}

	@Override
	public void unregisterListener() {
		//TODO not implemented yet
	}

}

package org.rifidi.edge.core.readerAdapterService;

import org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo;
import org.rifidi.edge.core.readerAdapter.ISpecificReaderAdapterFactory;

public class ReaderAdapterRegistryServiceImpl implements ReaderAdapterRegistryService {


	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerAdapterService.ReaderAdapterRegistryService#getSpecReaderAdapterFactory(org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo)
	 */
	public void getSpecReaderAdapterFactory(AbstractConnectionInfo pattern) {

	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerAdapterService.ReaderAdapterRegistryService#registerListener()
	 */
	public void registerListener() {

	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerAdapterService.ReaderAdapterRegistryService#unregisterListener()
	 */
	public void unregisterListener() {

	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerAdapterService.ReaderAdapterRegistryService#registerReaderAdapter(java.lang.Class, org.rifidi.edge.core.readerAdapter.ISpecificReaderAdapterFactory)
	 */
	public void registerReaderAdapter(Class<?> specConnectionInfo,
			ISpecificReaderAdapterFactory pattern2) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerAdapterService.ReaderAdapterRegistryService#unregisterReaderAdapter(java.lang.Class)
	 */
	@Override
	public void unregisterReaderAdapter(Class<?> specConnnectionInfo) {
		// TODO Auto-generated method stub
		
	}
}

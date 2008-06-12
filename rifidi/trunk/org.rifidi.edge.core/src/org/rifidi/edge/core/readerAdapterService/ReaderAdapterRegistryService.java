package org.rifidi.edge.core.readerAdapterService;

import java.util.List;

import org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo;
import org.rifidi.edge.core.readerAdapter.ISpecificReaderAdapterFactory;

public interface ReaderAdapterRegistryService {

	public void registerReaderAdapter(Class<? extends AbstractConnectionInfo> specificConnectionInfo,
			ISpecificReaderAdapterFactory specificReaderAdapterFactory);

	public void unregisterReaderAdapter(Class<? extends AbstractConnectionInfo> specificConnectionInfo);

	public ISpecificReaderAdapterFactory getSpecReaderAdapterFactory(
			AbstractConnectionInfo specificConnectionInfo);
	
	public List<String> getAvailableReaderAdapters();

	public boolean registerListener(ReaderAdapterRegistryServiceListener listener);

	public boolean unregisterListener(ReaderAdapterRegistryServiceListener listener);


}
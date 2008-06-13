package org.rifidi.edge.core.adapter.dummyadapter;

import org.rifidi.edge.core.exception.adapter.RifidiReaderAdapterCreationException;
import org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo;
import org.rifidi.edge.core.readerAdapter.IReaderAdapter;
import org.rifidi.edge.core.readerAdapter.ISpecificReaderAdapterFactory;

public class DummyReaderAdapterFactory implements ISpecificReaderAdapterFactory {

	@Override
	public IReaderAdapter createSpecificReaderAdapter(
			AbstractConnectionInfo abstractConnectionInfo) throws RifidiReaderAdapterCreationException{
		return new DummyReaderAdapter();
	}

}

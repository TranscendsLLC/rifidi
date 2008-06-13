package org.rifidi.edge.core.readerAdapter;

import org.rifidi.edge.core.exception.adapter.RifidiReaderAdapterCreationException;

public interface ISpecificReaderAdapterFactory {

	public IReaderAdapter createSpecificReaderAdapter(AbstractConnectionInfo abstractConnectionInfo)
		throws RifidiReaderAdapterCreationException;

}

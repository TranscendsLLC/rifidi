package org.rifidi.edge.core.readerPlugin;

import org.rifidi.edge.core.exception.RifidiReaderAdapterCreationException;

public interface ISpecificReaderPluginFactory {

	public IReaderPlugin createSpecificReaderAdapter(AbstractReaderInfo abstractConnectionInfo)
		throws RifidiReaderAdapterCreationException;

}

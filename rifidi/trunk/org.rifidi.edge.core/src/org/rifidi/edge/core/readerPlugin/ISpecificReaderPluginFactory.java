package org.rifidi.edge.core.readerPlugin;

import org.rifidi.edge.core.exception.readerPlugin.RifidiReaderAdapterCreationException;

public interface ISpecificReaderPluginFactory {

	public IReaderPlugin createSpecificReaderAdapter(AbstractReaderInfo abstractConnectionInfo)
		throws RifidiReaderAdapterCreationException;

}

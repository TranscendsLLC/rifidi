package org.rifidi.edge.core.readerPlugin;

import org.rifidi.edge.core.exception.readerConnection.RifidiReaderPluginCreationException;

public interface ISpecificReaderPluginFactory {

	public IReaderPlugin createSpecificReaderAdapter(AbstractReaderInfo abstractConnectionInfo)
		throws RifidiReaderPluginCreationException;

}

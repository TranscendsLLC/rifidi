package org.rifidi.edge.core.readerPlugin;

import org.rifidi.edge.core.exception.readerConnection.RifidiReaderPluginCreationException;

public interface ISpecificReaderPluginFactory {

	/**
	 * Factory for reader plugins
	 * @param abstractConnectionInfo The connection info for the reader
	 * @return The reader plugin
	 * @throws RifidiReaderPluginCreationException
	 */
	public IReaderPlugin createSpecificReaderAdapter(AbstractReaderInfo abstractConnectionInfo)
		throws RifidiReaderPluginCreationException;

}

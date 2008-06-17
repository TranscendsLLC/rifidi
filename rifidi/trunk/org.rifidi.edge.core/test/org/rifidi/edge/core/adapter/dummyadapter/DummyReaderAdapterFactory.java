package org.rifidi.edge.core.adapter.dummyadapter;

import org.rifidi.edge.core.exception.readerConnection.RifidiReaderPluginCreationException;
import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;
import org.rifidi.edge.core.readerPlugin.IReaderPlugin;
import org.rifidi.edge.core.readerPlugin.ISpecificReaderPluginFactory;

public class DummyReaderAdapterFactory implements ISpecificReaderPluginFactory {

	@Override
	public IReaderPlugin createSpecificReaderAdapter(
			AbstractReaderInfo abstractConnectionInfo) throws RifidiReaderPluginCreationException{
		if (!(abstractConnectionInfo instanceof DummyConnectionInfo) )
			throw new RifidiReaderPluginCreationException();
		return new DummyReaderAdapter((DummyConnectionInfo)abstractConnectionInfo);
	}

}

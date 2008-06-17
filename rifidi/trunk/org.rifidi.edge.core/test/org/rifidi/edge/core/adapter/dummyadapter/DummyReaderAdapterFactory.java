package org.rifidi.edge.core.adapter.dummyadapter;

import org.rifidi.edge.core.exception.RifidiReaderAdapterCreationException;
import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;
import org.rifidi.edge.core.readerPlugin.IReaderPlugin;
import org.rifidi.edge.core.readerPlugin.ISpecificReaderPluginFactory;

public class DummyReaderAdapterFactory implements ISpecificReaderPluginFactory {

	@Override
	public IReaderPlugin createSpecificReaderAdapter(
			AbstractReaderInfo abstractConnectionInfo) throws RifidiReaderAdapterCreationException{
		if (!(abstractConnectionInfo instanceof DummyConnectionInfo) )
			throw new RifidiReaderAdapterCreationException();
		return new DummyReaderAdapter((DummyConnectionInfo)abstractConnectionInfo);
	}

}

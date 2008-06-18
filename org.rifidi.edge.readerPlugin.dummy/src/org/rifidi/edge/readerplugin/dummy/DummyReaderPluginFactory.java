package org.rifidi.edge.readerplugin.dummy;

import org.rifidi.edge.core.exception.readerConnection.RifidiReaderPluginCreationException;
import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;
import org.rifidi.edge.core.readerPlugin.IReaderPlugin;
import org.rifidi.edge.core.readerPlugin.ISpecificReaderPluginFactory;

public class DummyReaderPluginFactory implements ISpecificReaderPluginFactory {

	@Override
	public IReaderPlugin createSpecificReaderAdapter(
			AbstractReaderInfo abstractConnectionInfo) throws RifidiReaderPluginCreationException{
		if (!(abstractConnectionInfo instanceof DummyReaderInfo) )
			throw new RifidiReaderPluginCreationException();
		return new DummyReaderPlugin((DummyReaderInfo)abstractConnectionInfo);
	}

}

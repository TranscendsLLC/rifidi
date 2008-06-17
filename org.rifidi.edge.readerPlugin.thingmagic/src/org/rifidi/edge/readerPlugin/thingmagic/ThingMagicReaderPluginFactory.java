package org.rifidi.edge.readerPlugin.thingmagic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.exception.readerConnection.RifidiReaderPluginCreationException;
import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;
import org.rifidi.edge.core.readerPlugin.IReaderPlugin;
import org.rifidi.edge.core.readerPlugin.ISpecificReaderPluginFactory;

public class ThingMagicReaderPluginFactory implements ISpecificReaderPluginFactory {
	private static final Log logger = LogFactory.getLog(ThingMagicReaderPluginFactory.class);	
	@Override
	public IReaderPlugin createSpecificReaderAdapter(
			AbstractReaderInfo abstractConnectionInfo) throws RifidiReaderPluginCreationException{
		logger.debug("createSpecificReaderAdapter called.");
		return new ThingMagicReaderPlugin((ThingMagicReaderInfo) abstractConnectionInfo);
	}

}

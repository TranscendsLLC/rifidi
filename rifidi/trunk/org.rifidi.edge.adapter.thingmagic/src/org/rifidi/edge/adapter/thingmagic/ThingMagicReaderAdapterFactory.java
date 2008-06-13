package org.rifidi.edge.adapter.thingmagic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.exception.adapter.RifidiReaderAdapterCreationException;
import org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo;
import org.rifidi.edge.core.readerAdapter.IReaderAdapter;
import org.rifidi.edge.core.readerAdapter.ISpecificReaderAdapterFactory;

public class ThingMagicReaderAdapterFactory implements ISpecificReaderAdapterFactory {
	private static final Log logger = LogFactory.getLog(ThingMagicReaderAdapterFactory.class);	
	@Override
	public IReaderAdapter createSpecificReaderAdapter(
			AbstractConnectionInfo abstractConnectionInfo) throws RifidiReaderAdapterCreationException{
		logger.debug("createSpecificReaderAdapter called.");
		return new ThingMagicReaderAdapter((ThingMagicConnectionInfo) abstractConnectionInfo);
	}

}

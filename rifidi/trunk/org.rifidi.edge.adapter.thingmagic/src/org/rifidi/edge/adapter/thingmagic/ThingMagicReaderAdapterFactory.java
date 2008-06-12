package org.rifidi.edge.adapter.thingmagic;

import org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo;
import org.rifidi.edge.core.readerAdapter.IReaderAdapter;
import org.rifidi.edge.core.readerAdapter.ISpecificReaderAdapterFactory;

public class ThingMagicReaderAdapterFactory implements ISpecificReaderAdapterFactory {

	@Override
	public IReaderAdapter createSpecificReaderAdapter(
			AbstractConnectionInfo abstractConnectionInfo) {
		return new ThingMagicReaderAdapter((ThingMagicConnectionInfo) abstractConnectionInfo);
	}

}

package org.rifidi.edge.adapter.thingmagic;

import org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo;

public class ThingMagicConnectionInfo extends AbstractConnectionInfo {

	@Override
	public Class<? extends AbstractConnectionInfo> getReaderAdapterType() {
		return ThingMagicConnectionInfo.class;
	}

}

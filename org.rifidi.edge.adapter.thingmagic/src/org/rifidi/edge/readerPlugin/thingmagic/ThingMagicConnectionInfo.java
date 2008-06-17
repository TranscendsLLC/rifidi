package org.rifidi.edge.readerPlugin.thingmagic;

import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;

public class ThingMagicConnectionInfo extends AbstractReaderInfo {

	@Override
	public Class<? extends AbstractReaderInfo> getReaderAdapterType() {
		return ThingMagicConnectionInfo.class;
	}

}

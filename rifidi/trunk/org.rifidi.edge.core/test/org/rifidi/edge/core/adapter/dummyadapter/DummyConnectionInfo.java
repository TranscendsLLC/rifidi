package org.rifidi.edge.core.adapter.dummyadapter;

import org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo;

public class DummyConnectionInfo extends AbstractConnectionInfo {

	@Override
	public Class<? extends AbstractConnectionInfo> getReaderAdapterType() {
		return DummyConnectionInfo.class;
	}

}

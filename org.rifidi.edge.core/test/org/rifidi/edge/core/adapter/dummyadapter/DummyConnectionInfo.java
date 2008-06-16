package org.rifidi.edge.core.adapter.dummyadapter;

import org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo;

public class DummyConnectionInfo extends AbstractConnectionInfo {
	
	private EDummyError errorToSet = EDummyError.NONE;

	@Override
	public Class<? extends AbstractConnectionInfo> getReaderAdapterType() {
		return DummyConnectionInfo.class;
	}

	/**
	 * @return the errorToSet
	 */
	public EDummyError getErrorToSet() {
		return errorToSet;
	}

	/**
	 * @param errorToSet the errorToSet to set
	 */
	public void setErrorToSet(EDummyError errorToSet) {
		this.errorToSet = errorToSet;
	}

	
}

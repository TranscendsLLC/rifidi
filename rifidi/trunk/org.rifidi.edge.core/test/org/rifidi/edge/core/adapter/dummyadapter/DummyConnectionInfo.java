package org.rifidi.edge.core.adapter.dummyadapter;

import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;

public class DummyConnectionInfo extends AbstractReaderInfo {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1851026024777912992L;

	private EDummyError errorToSet = EDummyError.NONE;

	/**
	 * @return the errorToSet
	 */
	public EDummyError getErrorToSet() {
		return errorToSet;
	}

	/**
	 * @param errorToSet
	 *            the errorToSet to set
	 */
	public void setErrorToSet(EDummyError errorToSet) {
		this.errorToSet = errorToSet;
	}

}

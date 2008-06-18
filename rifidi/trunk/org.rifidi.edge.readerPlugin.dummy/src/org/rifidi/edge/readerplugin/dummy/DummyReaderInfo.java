package org.rifidi.edge.readerplugin.dummy;

import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;

public class DummyReaderInfo extends AbstractReaderInfo {

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

	@Override
	public String getReaderType() {
		return "DummyReaderAdapter";
	}

}

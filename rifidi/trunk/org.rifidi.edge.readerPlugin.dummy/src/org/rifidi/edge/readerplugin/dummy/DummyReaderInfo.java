package org.rifidi.edge.readerplugin.dummy;

import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;

public class DummyReaderInfo extends AbstractReaderInfo {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1851026024777912992L;

	private EDummyError errorToSet = EDummyError.NONE;

	/* number from 0 (inclusive) to 1 (exclusive)*/
	double randomErrorProbibility = 0.10;
	
	/* number from 0 (inclusive) to 1 (exclusive)*/
	double probiblityOfErrorsBeingRuntimeExceptions = 0.25;

	public DummyReaderInfo(){
		
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerPlugin.AbstractReaderInfo#getReaderType()
	 */
	@Override
	public String getReaderType() {
		return "DummyReaderAdapter";
	}
	
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
	
	/**
	 * @return the randomErrorProbibility
	 */
	public double getRandomErrorProbibility() {
		return randomErrorProbibility;
	}

	/**
	 * @param randomErrorProbibility the randomErrorProbibility to set
	 */
	public void setRandomErrorProbibility(double randomErrorProbibility) {
		this.randomErrorProbibility = randomErrorProbibility;
	}

	/**
	 * @return the probiblityOfErrorsBeingRuntimeExceptions
	 */
	public double getProbiblityOfErrorsBeingRuntimeExceptions() {
		return probiblityOfErrorsBeingRuntimeExceptions;
	}

	/**
	 * @param probiblityOfErrorsBeingRuntimeExceptions the probiblityOfErrorsBeingRuntimeExceptions to set
	 */
	public void setProbiblityOfErrorsBeingRuntimeExceptions(
			double probiblityOfErrorsBeingRuntimeExceptions) {
		this.probiblityOfErrorsBeingRuntimeExceptions = probiblityOfErrorsBeingRuntimeExceptions;
	}
}

package org.rifidi.edge.readerplugin.dummy;

import org.rifidi.edge.core.communication.enums.CommunicationType;
import org.rifidi.edge.core.communication.protocol.Protocol;
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
	public void setErrorToSet(String errorToSet) {
		errorToSet = errorToSet.trim().toUpperCase();
		this.errorToSet = EDummyError.valueOf(errorToSet);
		if (this.errorToSet == null) {
			String msg = "Not a valid enum type. Please pick from this list: ";
			EDummyError[] errorTypes = EDummyError.values();
			for (int x = 0;x < errorTypes.length; x++) {
				msg = msg + errorTypes[x].toString();
				if (x < errorTypes.length-1)
					msg = msg + ", ";
			}
			msg = msg + ".";
			throw new IllegalArgumentException(msg);
		}
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
		if ( (randomErrorProbibility < 0) || (randomErrorProbibility > 1) )
			throw new IllegalArgumentException();
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
		if ( (probiblityOfErrorsBeingRuntimeExceptions < 0) || (probiblityOfErrorsBeingRuntimeExceptions > 1) )
			throw new IllegalArgumentException();
		this.probiblityOfErrorsBeingRuntimeExceptions = probiblityOfErrorsBeingRuntimeExceptions;
	}

	@Override
	public CommunicationType getCommunicationType() {
		return CommunicationType.SYNCHRONOUS;
	}

	@Override
	public Protocol getProtocol() {
		return new DummyProtocol();
	}
}

package org.rifidi.edge.readerplugin.dummy;

import java.util.Random;

import org.rifidi.edge.core.readerplugin.ReaderInfo;

public class DummyReaderInfo extends ReaderInfo {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5486038928636364398L;

	private EDummyError errorToSet = EDummyError.NONE;

	/* number from 0 (inclusive) to 1 (exclusive)*/
	double randomErrorProbibility = 0.10;
	
	/* number from 0 (inclusive) to 1 (exclusive)*/
	double probiblityOfErrorsBeingRuntimeExceptions = 0.25;
	
	/* used only when the dummy adapter is set to random errors */
	Random random;

	public DummyReaderInfo(){
		random = new Random();
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



	/**
	 * @return the random
	 */
	public Random getRandom() {
		return random;
	}



	/**
	 * @param random the random to set
	 */
	public void setRandom(Random random) {
		this.random = random;
	}


}

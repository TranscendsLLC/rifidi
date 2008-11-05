package org.rifidi.edge.readerplugin.dummy.plugin;

import java.util.Random;

import javax.xml.bind.annotation.XmlRootElement;

import org.rifidi.dynamicswtforms.xml.annotaions.Form;
import org.rifidi.dynamicswtforms.xml.annotaions.FormElement;
import org.rifidi.dynamicswtforms.xml.constants.FormElementType;
import org.rifidi.edge.core.readerplugin.ReaderInfo;

@XmlRootElement(name = "DummyReaderInfo")
@Form(name = "DummyReaderInfo", formElements = {
		@FormElement(type = FormElementType.STRING, elementName = "ipAddress", displayName = "IP Address", defaultValue = "localhost", regex="(.)*"),
		@FormElement(type = FormElementType.INTEGER, elementName = "port", displayName = "Port", defaultValue = "12345", min = 0, max = 65535),
		@FormElement(type = FormElementType.INTEGER, elementName = "reconnectionInterval", displayName = "Reconnect Interval", defaultValue = "1000", min = 0, max = Integer.MAX_VALUE),
		@FormElement(type = FormElementType.INTEGER, elementName = "maxNumConnectionsAttempts", displayName = "Connection Attempts", defaultValue = "3", min = -1, max = Integer.MAX_VALUE),
		@FormElement(type= FormElementType.FLOAT, elementName="randomErrorProbibility", displayName="Random Error Probibility", defaultValue="0", max=1, min=0, decimalPlaces=2),
		@FormElement(type= FormElementType.FLOAT, elementName="probiblityOfErrorsBeingRuntimeExceptions", displayName="Probibility that Errors \nare Runtime Exceptions", defaultValue="0", max=1, min=0, decimalPlaces=2), 
		@FormElement(type = FormElementType.CHOICE, elementName = "errorToSet", displayName = "Error Type", defaultValue ="NONE", enumClass="org.rifidi.edge.readerplugin.dummy.plugin.EDummyError" )})
public class DummyReaderInfo extends ReaderInfo {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5486038928636364398L;

	private EDummyError errorToSet = EDummyError.NONE;

	/* number from 0 (inclusive) to 1 (exclusive)*/
	private double randomErrorProbibility = 0;
	
	/* number from 0 (inclusive) to 1 (exclusive)*/
	//@DoubleMetadata(defaultValue=.25, displayName="Probablility that errors are runtime exceptions", editable=true, maxValue=1, minValue=0, name="probiblityOfErrorsBeingRuntimeExceptions")
	private double probiblityOfErrorsBeingRuntimeExceptions = 0;
	
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
	
	public void setErrorToSet(EDummyError errorToSet){
		this.errorToSet = errorToSet;
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

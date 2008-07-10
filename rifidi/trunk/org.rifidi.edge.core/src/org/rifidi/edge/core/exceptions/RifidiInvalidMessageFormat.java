package org.rifidi.edge.core.exceptions;

/**
 * This exception should be thrown by a Communication Protocol when the message
 * that it is trying to convert bytes->object or object->bytes
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class RifidiInvalidMessageFormat extends RifidiException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8367649195678157160L;
	
	public RifidiInvalidMessageFormat() {
		// TODO Auto-generated constructor stub
		super();
	}

	public RifidiInvalidMessageFormat(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public RifidiInvalidMessageFormat(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public RifidiInvalidMessageFormat(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
}

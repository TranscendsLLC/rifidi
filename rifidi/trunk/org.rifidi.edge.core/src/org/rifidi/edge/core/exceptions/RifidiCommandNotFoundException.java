package org.rifidi.edge.core.exceptions;

/**
 * Exception thrown if a Command could not be found
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public class RifidiCommandNotFoundException extends RifidiException {

	/**
	 * Serial Version ID
	 */
	private static final long serialVersionUID = -294639183459374350L;

	/**
	 * Create a new RifidiCommandNotFoundException
	 */
	public RifidiCommandNotFoundException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Create a new RifidiCommandNotFoundException
	 * 
	 * @param arg0
	 *            Message
	 * @param arg1
	 *            previous Exception
	 */
	public RifidiCommandNotFoundException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Create a new RifidiCommandNotFoundException
	 * 
	 * @param arg0
	 *            Message
	 */
	public RifidiCommandNotFoundException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Create a new RifidiCommandNotFoundException
	 * 
	 * @param arg0
	 *            previous Exception
	 */
	public RifidiCommandNotFoundException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

}

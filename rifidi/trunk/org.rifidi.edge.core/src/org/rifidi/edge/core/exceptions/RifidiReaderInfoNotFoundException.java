package org.rifidi.edge.core.exceptions;

/**
 * Exception thrown if the specified ReaderInfo could not be found
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public class RifidiReaderInfoNotFoundException extends RifidiException {

	/**
	 * Serial Version ID
	 */
	private static final long serialVersionUID = -527446022853305071L;

	/**
	 * Create a new RifidiReaderInfoNotFoundException
	 */
	public RifidiReaderInfoNotFoundException() {

		super();
	}

	/**
	 * Create a new RifidiReaderInfoNotFoundException
	 * 
	 * @param arg0
	 *            Message
	 * @param arg1
	 *            previous Exception
	 */
	public RifidiReaderInfoNotFoundException(String arg0, Throwable arg1) {
		super(arg0, arg1);

	}

	/**
	 * Create a new RifidiReaderInfoNotFoundException
	 * 
	 * @param arg0
	 *            Message
	 */
	public RifidiReaderInfoNotFoundException(String arg0) {
		super(arg0);

	}

	/**
	 * Create a new RifidiReaderInfoNotFoundException
	 * 
	 * @param arg0
	 *            previous Exception
	 */
	public RifidiReaderInfoNotFoundException(Throwable arg0) {
		super(arg0);

	}

}

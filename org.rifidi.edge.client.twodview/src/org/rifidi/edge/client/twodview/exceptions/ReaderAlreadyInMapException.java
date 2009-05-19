package org.rifidi.edge.client.twodview.exceptions;

/**
 * This exception is thrown when someone is trying to add a reader to the map
 * that is already present on the map.
 * 
 * @author Tobias Hoppenthaler - tobias@pramari.com
 */
public class ReaderAlreadyInMapException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3458800281736583664L;

	/**
	 * Constructor.
	 */
	public ReaderAlreadyInMapException() {
		super();
		System.err.println("Reader already in map");
	}

	/**
	 * Constructor.  
	 * 
	 * @param arg0
	 * @param arg1
	 */
	public ReaderAlreadyInMapException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		System.err.println("Reader already in map: " + arg0);
	}

	/**
	 * Constructor.  
	 * 
	 * @param arg0
	 */
	public ReaderAlreadyInMapException(String arg0) {
		super(arg0);
		System.err.println("Reader already in map: " + arg0);
	}

	/**
	 * Constructor.  
	 * 
	 * @param arg0
	 */
	public ReaderAlreadyInMapException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

}

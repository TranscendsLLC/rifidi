
package org.rifidi.edge.client.twodview.exceptions;

/**
 * This exception is thrown when a reader you are attempting to add or do an operation on is not found.  
 * 
 * @author Tobias Hoppenthaler - tobias@pramari.com
 */
public class RemoteReaderNotFoundException extends Exception {

	/**
	 * 
	 */
	public RemoteReaderNotFoundException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructor.  
	 * 
	 * @param arg0
	 * @param arg1
	 */
	public RemoteReaderNotFoundException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructor.  
	 * 
	 * @param arg0
	 */
	public RemoteReaderNotFoundException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructor.  
	 * 
	 * @param arg0
	 */
	public RemoteReaderNotFoundException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

}

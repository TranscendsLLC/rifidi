/**
 * 
 */
package org.rifidi.edge.readerplugin.alien.commandobject;
//TODO: Comments
/**
 * @author Jochen Mader - jochen@pramari.com
 *
 */
public class AlienException extends Exception {

	/** SerialVersion ID*/
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public AlienException() {
	}

	/**
	 * @param arg0
	 */
	public AlienException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public AlienException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public AlienException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}

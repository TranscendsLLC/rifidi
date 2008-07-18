package org.rifidi.edge.core.readerplugin.connectionmanager;

/**
 * ConnectionExceptionListener for listening to Exception events on a Connection
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public interface ConnectionExceptionListener {

	/**
	 * The Exception occurred on the Connection
	 * 
	 * @param exception
	 *            Exception
	 */
	public void connectionExceptionEvent(Exception exception);

}

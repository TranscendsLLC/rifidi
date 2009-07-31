/**
 * 
 */
package org.rifidi.edge.core.sensors.base.threads;

/**
 * This interface allows sensor plugins to implement the functionality to
 * determine what to do with a logical message once it has been read from the
 * socket
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface MessageProcessingStrategy {

	/**
	 * A hook to allow implementations to do do something with the message when
	 * it has been completely received from the socket
	 * 
	 * @param message
	 *            The complete message
	 */
	abstract void processMessage(byte[] message);

}

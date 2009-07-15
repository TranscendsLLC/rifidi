/**
 * 
 */
package org.rifidi.edge.core.sensors.base.threads;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 *
 */
public interface MessageParser {
	
	/**
	 * This method is called each time a new byte is read. It will return the
	 * full message if a complete message has arrived, other wise null.
	 * 
	 * @param message
	 * @return the message or null
	 */
	public abstract byte[] isMessage(byte message);

}

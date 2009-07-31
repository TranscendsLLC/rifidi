/**
 * 
 */
package org.rifidi.edge.core.sensors.base.threads;

/**
 * This interface allows sensor plugins to implement the functionality to
 * determine what constitutes a logical message from the sensor
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface MessageParsingStrategy {

	/**
	 * This method is called each time a new byte is read. It will return the
	 * full message if a complete message has arrived, other wise null.
	 * 
	 * @param message
	 * @return the message or null
	 */
	public abstract byte[] isMessage(byte message);

}

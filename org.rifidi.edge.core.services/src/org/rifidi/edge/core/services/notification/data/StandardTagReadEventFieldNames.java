/**
 * 
 */
package org.rifidi.edge.core.services.notification.data;

/**
 * This interface contains several fields that it is common for RFID readers to
 * collect. These names should be used when inserting into the extraInformation
 * hashmap in TagReadEvent.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface StandardTagReadEventFieldNames {

	/** Return Signal Strength Indicator. Expressed as a Float */
	public static final String RSSI = "RSSI";
	/** Speed tag was moving. Expressed as Float */
	public static final String SPEED = "Speed";
	/** Direction tag was moving. Expressed as String */
	public static final String DIRECTION = "Direction";

}

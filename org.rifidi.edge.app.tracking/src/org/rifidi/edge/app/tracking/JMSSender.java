/**
 * 
 */
package org.rifidi.edge.app.tracking;

import org.rifidi.edge.app.tracking.domain.RFIDEvent;

/**
 * This interface allows us to send messages on the JMS Destination
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface JMSSender {

	/***
	 * Call this method to send out a new message over JMS when an RFID event
	 * has arrived
	 * 
	 * @param event
	 */
	public void arrvied(RFIDEvent event);

	/**
	 * Call this method to send out a new message over JMS when an RFID event
	 * has departed.
	 * 
	 * @param event
	 */
	public void departed(RFIDEvent event);

}

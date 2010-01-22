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
	 * A new tag has arrived
	 * 
	 * @param event
	 */
	public void arrvied(RFIDEvent event);

	/**
	 * A tag has departed
	 * 
	 * @param event
	 */
	public void departed(RFIDEvent event);

}

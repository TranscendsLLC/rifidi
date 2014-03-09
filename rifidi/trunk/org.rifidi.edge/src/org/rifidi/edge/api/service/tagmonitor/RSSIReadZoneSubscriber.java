/**
 * 
 */
package org.rifidi.edge.api.service.tagmonitor;

import java.util.HashMap;
import java.util.List;

import org.rifidi.edge.api.service.RifidiAppSubscriber;
import org.rifidi.edge.notification.RSSITagReadEvent;

/**
 * @author rifidi
 *
 */
public interface RSSIReadZoneSubscriber extends RifidiAppSubscriber {
	/**
	 * The method that will be called when an arrival event has happened.
	 * 
	 * @param event
	 *            The Tag that arrived
	 */
	public void tagArrived(List<RSSITagReadEvent> tag);

	/**
	 * The method that will be called when a departed event has happened.
	 * 
	 * @param event
	 *            The tag that departed.
	 */
	public void tagDeparted(List<RSSITagReadEvent> tag);
}

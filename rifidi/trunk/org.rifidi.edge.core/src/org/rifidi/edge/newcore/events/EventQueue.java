/**
 * 
 */
package org.rifidi.edge.newcore.events;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface EventQueue {

	/**
	 * Add an event to the event queue
	 * 
	 * @param event
	 *            the event to add
	 */
	void addEvent(Object event);
}

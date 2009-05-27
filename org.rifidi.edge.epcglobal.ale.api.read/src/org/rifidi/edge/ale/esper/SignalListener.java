/**
 * 
 */
package org.rifidi.edge.ale.esper;

import java.util.List;

import org.rifidi.edge.core.messages.TagReadEvent;

/**
 * This listener can be used on a SignalStatement and will be called when a
 * certain event condition has arrived.
 * 
 * @author Jochen Mader
 * 
 */
public interface SignalListener {
	/**
	 * Called when a starter has triggered.
	 * 
	 * @param type
	 * @param cause
	 *            the event causing the signal
	 */
	void startSignal(int type, Object cause);

	/**
	 * Called when a stopper has triggered.
	 * 
	 * @param type
	 * @param cause
	 * @param events
	 */
	void stopSignal(int type, Object cause, List<TagReadEvent> events);
}

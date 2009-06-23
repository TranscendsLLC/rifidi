/**
 * 
 */
package org.rifidi.edge.ale.esper;

import java.util.List;

import org.rifidi.edge.core.services.notification.data.TagReadEvent;
import org.rifidi.edge.epcglobal.aleread.ALEReadAPI;

/**
 * This listener can be used on a SignalStatement and will be called when a
 * certain event condition has arrived.
 * 
 * @author Jochen Mader - jochen@pramari.com
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
	void startSignal(ALEReadAPI.TriggerCondition type, Object cause);

	/**
	 * Called when a stopper has triggered.
	 * 
	 * @param type
	 * @param cause
	 * @param events
	 */
	void stopSignal(ALEReadAPI.TriggerCondition type, Object cause, List<TagReadEvent> events);
}

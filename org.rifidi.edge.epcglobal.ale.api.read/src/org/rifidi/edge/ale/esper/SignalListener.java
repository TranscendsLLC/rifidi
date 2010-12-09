/*
 * 
 * SignalListener.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
/**
 * 
 */
package org.rifidi.edge.ale.esper;

import java.util.List;

import org.rifidi.edge.epcglobal.aleread.ALEReadAPI;
import org.rifidi.edge.notification.TagReadEvent;

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

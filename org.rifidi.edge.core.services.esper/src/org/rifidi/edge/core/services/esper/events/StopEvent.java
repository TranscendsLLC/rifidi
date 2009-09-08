/*
 * 
 * StopEvent.java
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
package org.rifidi.edge.core.services.esper.events;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class StopEvent {
	/** Name of the associated ec spec. */
	private String name;
	/** Trigger that created this event. */
	private String trigger = null;

	/**
	 * @param name
	 */
	public StopEvent(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the trigger
	 */
	public String getTrigger() {
		return trigger;
	}

	/**
	 * @param trigger the trigger to set
	 */
	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}
}

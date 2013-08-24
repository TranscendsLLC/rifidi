/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
/**
 * 
 */
package org.rifidi.edge.services;

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

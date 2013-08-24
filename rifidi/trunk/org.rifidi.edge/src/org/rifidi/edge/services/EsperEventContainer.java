/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.services;

import java.util.HashSet;
import java.util.Set;

import org.rifidi.edge.notification.ReadCycle;

/**
 * This is a bean for storing events that should be given to esper from the
 * Sensor.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class EsperEventContainer {

	/** The ReadCycle event */
	private ReadCycle readCycle;
	/** Any other events */
	private Set<Object> otherEvents;

	/**
	 * @return the readCycle
	 */
	public ReadCycle getReadCycle() {
		return readCycle;
	}

	/**
	 * @return the otherEvents
	 */
	public Set<Object> getOtherEvents() {
		if (otherEvents == null) {
			otherEvents = new HashSet<Object>();
		}
		return otherEvents;
	}

	/**
	 * @param readCycle
	 *            the readCycle to set
	 */
	public void setReadCycle(ReadCycle readCycle) {
		this.readCycle = readCycle;
	}

	/**
	 * @param otherEvents
	 *            the otherEvents to set
	 */
	public void setOtherEvents(Set<Object> otherEvents) {
		this.otherEvents = otherEvents;
	}

}

/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
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

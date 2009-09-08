/*
 * 
 * DestroyEvent.java
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
public class DestroyEvent {
	/** Name of the associated ec spec. */
	private String name;

	/**
	 * @param name
	 *            name of the associated ec spec
	 */
	public DestroyEvent(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}

/*
 *  WeighStationArrivedEvent.java
 *
 *  Created:	Aug 10, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.northwind.events;

import org.rifidi.edge.core.services.notification.data.TagReadEvent;

/**
 * This event occurs whenever a tag arrives at the weigh station.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class WeighStationArrivedEvent {
	/** The tag that arrived */
	private final TagReadEvent tag;

	/**
	 * @param tag
	 */
	public WeighStationArrivedEvent(TagReadEvent tag) {
		super();
		this.tag = tag;
	}

	/**
	 * @return the tag
	 */
	public TagReadEvent getTag() {
		return tag;
	}
}

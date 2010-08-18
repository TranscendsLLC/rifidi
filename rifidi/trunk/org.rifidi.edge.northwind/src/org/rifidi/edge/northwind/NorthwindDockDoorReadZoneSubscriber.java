/*
 *  NorthwindDockDoorReadZoneSubscriber.java
 *
 *  Created:	Aug 11, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.northwind;

import org.rifidi.edge.core.app.api.service.tagmonitor.ReadZoneSubscriber;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;
import org.rifidi.edge.northwind.events.DockDoorArrivedEvent;
import org.rifidi.edge.northwind.events.DockDoorDepartedEvent;

/**
 * This subscriber will monitor the dock door and create an event whenever a tag
 * is seen or leaves the view.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class NorthwindDockDoorReadZoneSubscriber implements ReadZoneSubscriber {
	// The application class. We need this so we can insert any events we create
	// into Esper.
	private NorthwindApp app;

	private String location = null;

	/**
	 * @param app
	 * @param location
	 */
	public NorthwindDockDoorReadZoneSubscriber(NorthwindApp app, String location) {
		this.location = location;
		this.app = app;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.app.api.service.tagmonitor.ReadZoneSubscriber#tagArrived
	 * (org.rifidi.edge.core.services.notification.data.TagReadEvent)
	 */
	@Override
	public void tagArrived(TagReadEvent tag) {
		System.out.println("Tag arrived at location " + location + ": "
				+ tag.getTag().getFormattedID());
		this.app.sendNorthwindEvent(new DockDoorArrivedEvent(tag));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.app.api.service.tagmonitor.ReadZoneSubscriber#
	 * tagDeparted(org.rifidi.edge.core.services.notification.data.TagReadEvent)
	 */
	@Override
	public void tagDeparted(TagReadEvent tag) {
		System.out.println("Tag departed at location " + location + ": "
				+ tag.getTag().getFormattedID());
		this.app.sendNorthwindEvent(new DockDoorDepartedEvent(tag));
	}

}
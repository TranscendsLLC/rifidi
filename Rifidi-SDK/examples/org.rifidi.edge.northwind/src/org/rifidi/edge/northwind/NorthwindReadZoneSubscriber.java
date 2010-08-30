/*
 *  NorthwindReadZoneSubscriber.java
 *
 *  Created:	Aug 20, 2010
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
import org.rifidi.edge.northwind.events.WeighStationArrivedEvent;
import org.rifidi.edge.northwind.events.WeighStationDepartedEvent;

/**
 * This subscriber will monitor a read zone and create an event whenever a tag
 * is seen or leaves the view.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class NorthwindReadZoneSubscriber implements ReadZoneSubscriber {

	// The application class. We need this so we can insert any events we create
	// into Esper.
	private NorthwindApp app;
	// The location this subscriber is monitoring.
	private String location = null;
	// Is the location the dock door? If not, it is the weigh station.
	private Boolean isDockDoor = null;

	/**
	 * Constructor.
	 * 
	 * @param app
	 *            The a0pplication which we need to send any events generated.
	 * @param location
	 *            A string representation of the location this subscriber is
	 *            listening for events on.
	 * @param isDockDoor
	 *            Is this location the dock door? If not, it is the weigh
	 *            station. This is used for determining the type of event to
	 *            send out.
	 */
	public NorthwindReadZoneSubscriber(NorthwindApp app, String location,
			boolean isDockDoor) {
		this.app = app;
		this.location = location;
		this.isDockDoor = isDockDoor;
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
		if (this.isDockDoor) {
			this.app.sendNorthwindEvent(new DockDoorArrivedEvent(tag));
		} else {
			this.app.sendNorthwindEvent(new WeighStationArrivedEvent(tag));
		}
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
		if (this.isDockDoor) {
			this.app.sendNorthwindEvent(new DockDoorDepartedEvent(tag));
		} else {
			this.app.sendNorthwindEvent(new WeighStationDepartedEvent(tag));
		}
	}

}

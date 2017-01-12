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
package org.rifidi.edge.northwind;

import org.rifidi.edge.api.service.tagmonitor.ReadZoneSubscriber;
import org.rifidi.edge.northwind.events.DockDoorArrivedEvent;
import org.rifidi.edge.northwind.events.DockDoorDepartedEvent;
import org.rifidi.edge.northwind.events.WeighStationArrivedEvent;
import org.rifidi.edge.northwind.events.WeighStationDepartedEvent;
import org.rifidi.edge.notification.TagReadEvent;

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

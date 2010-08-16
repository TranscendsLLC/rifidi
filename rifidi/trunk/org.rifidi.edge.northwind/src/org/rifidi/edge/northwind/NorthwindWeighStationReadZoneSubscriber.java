/*
 *  NorthwindWeighStationReadZoneSubscriber.java
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
import org.rifidi.edge.northwind.events.WeighStationArrivedEvent;
import org.rifidi.edge.northwind.events.WeighStationDepartedEvent;

/**
 * @author Matthew Dean - matt@pramari.com
 *
 */
public class NorthwindWeighStationReadZoneSubscriber implements
		ReadZoneSubscriber {

	private NorthwindApp app;

	private String location = null;

	/**
	 * 
	 * @param app
	 * @param location
	 */
	public NorthwindWeighStationReadZoneSubscriber(NorthwindApp app, String location) {
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
		this.app.sendNorthwindEvent(new WeighStationArrivedEvent(tag));
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
		this.app.sendNorthwindEvent(new WeighStationDepartedEvent(tag));
	}


}

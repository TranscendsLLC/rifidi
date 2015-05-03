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
package org.rifidi.app.thingworx;

import org.rifidi.edge.api.service.tagmonitor.ReadZoneSubscriber;
import org.rifidi.edge.notification.TagReadEvent;

import com.thingworx.communications.client.ConnectedThingClient;
import com.thingworx.communications.client.things.VirtualThing;
import com.thingworx.metadata.PropertyDefinition;
import com.thingworx.types.BaseTypes;

/**
 * The subscriber class. This class monitors the arrived and departed events for
 * all readers.
 * 
 * @author Matthew Dean - matt@transcends.co
 */
public class ThingworxSubscriber implements ReadZoneSubscriber {

	private ConnectedThingClient client;

	/**
	 * Constructor
	 * 
	 * @param conn
	 *            The database connection
	 */
	public ThingworxSubscriber(ConnectedThingClient client) {
		this.client = client;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.api.service.tagmonitor.ReadZoneSubscriber#tagArrived(
	 * org.rifidi.edge.notification.TagReadEvent)
	 */
	@Override
	public void tagArrived(TagReadEvent tag) {
		System.out.println("TAG ARRIVED: " + tag.getTag().getFormattedID());
		try {
			// Create instance of VirtualThing that will have properties
			VirtualThing virtualThing = new VirtualThing("firstThingName",
					"firstThingDescription", "myJavaSDKThingID1", client);

			// add a property to the VirtualThing you created
			// first create a PropertyDefinition
			PropertyDefinition epcProperty = new PropertyDefinition("epc", "The ID of a tag", BaseTypes.STRING);
			PropertyDefinition readerIDProperty = new PropertyDefinition("readerID", "The ID of the reader", BaseTypes.STRING);
			PropertyDefinition timestampProperty = new PropertyDefinition("timestamp", "", BaseTypes.NUMBER);
			PropertyDefinition antennaProperties = new PropertyDefinition("antenna", "The antenna the tag was last seen on", BaseTypes.NUMBER);
			// set the property on your thing
			virtualThing.defineProperty(epcProperty);
			virtualThing.defineProperty(readerIDProperty);
			virtualThing.defineProperty(timestampProperty);
			virtualThing.defineProperty(antennaProperties);

			// now we can set the property and update the platform
			virtualThing.setProperty("epc", tag.getTag().getFormattedID());
			virtualThing.setProperty("readerID", tag.getReaderID());
			virtualThing.setProperty("timestamp", tag.getTimestamp());
			virtualThing.setProperty("antenna", tag.getAntennaID());

			// bind the virtual thing to the client
			client.bindThing(virtualThing);
			// Start the client
			client.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.api.service.tagmonitor.ReadZoneSubscriber#tagDeparted
	 * (org.rifidi.edge.notification.TagReadEvent)
	 */
	@Override
	public void tagDeparted(TagReadEvent tag) {
		System.out.println("TAG DEPARTED: " + tag.getTag().getFormattedID());
	}
}

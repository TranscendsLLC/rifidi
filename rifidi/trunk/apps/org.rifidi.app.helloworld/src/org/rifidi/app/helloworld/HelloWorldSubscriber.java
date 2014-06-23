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
package org.rifidi.app.helloworld;

import org.rifidi.edge.api.service.tagmonitor.ReadZoneSubscriber;
import org.rifidi.edge.notification.TagReadEvent;

/**
 * The subscriber class. This class monitors the arrived and departed events for
 * all readers.
 * 
 * @author Matthew Dean - matt@transcends.co
 */
public class HelloWorldSubscriber implements ReadZoneSubscriber {

	/**
	 * Constructor
	 * 
	 * @param conn
	 *            The database connection
	 */
	public HelloWorldSubscriber() {
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

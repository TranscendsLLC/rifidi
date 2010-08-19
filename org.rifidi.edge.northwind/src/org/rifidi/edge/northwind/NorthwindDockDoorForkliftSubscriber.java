/*
 *  NorthwindDockDoorForkliftSubscriber.java
 *
 *  Created:	Aug 19, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.northwind;

import java.util.Set;

import org.rifidi.edge.core.app.api.service.tagmonitor.StableSetSubscriber;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;

/**
 * This class checks to see if a forklift is present when a group of tags has
 * been confirmed.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class NorthwindDockDoorForkliftSubscriber extends
		NorthwindForkliftSubscriber implements StableSetSubscriber {
	// String to show the location this subscriber is listening to.
	public static String DOCK_DOOR = "dock_door";

	// The prefix we expect for a forklift tag. As an example, it might be "35",
	// indicating a GID tag.
	private String forklift_prefix = null;

	/**
	 * 
	 */
	public NorthwindDockDoorForkliftSubscriber(String forklift_prefix) {
		this.forklift_prefix = forklift_prefix;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.app.api.service.tagmonitor.StableSetSubscriber#
	 * stableSetReached(java.util.Set)
	 */
	@Override
	public void stableSetReached(Set<TagReadEvent> stableSet) {
		this.checkForForklift(stableSet, forklift_prefix, DOCK_DOOR);
	}
}

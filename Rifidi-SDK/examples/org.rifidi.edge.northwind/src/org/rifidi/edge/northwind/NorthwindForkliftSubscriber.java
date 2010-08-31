/*
 *  NorthwindForkliftSubscriber.java
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
 * This class has a helper method to check if there is a tag that contains the
 * prefix to a forklift tag on the stable set. Generally speaking, there should
 * not be items present on the field of view without an accompanying forklift
 * reader.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class NorthwindForkliftSubscriber implements
		StableSetSubscriber {
	// The prefix we expect for a forklift tag. As an example, it might be "35",
	// indicating a GID tag.
	private String forklift_prefix;
	
	// String to show the location this subscriber is listening to.
	private String location;

	public NorthwindForkliftSubscriber(String forklift_prefix, String location) {
		this.forklift_prefix = forklift_prefix;

		this.location = location;
	}

	/*
	 * This method checks the existence of the forklift prefix in any of the
	 * tags in the given set. If one is found, true is returned. If the prefix
	 * is not present, false is returned.
	 */
	private boolean forklift_present(String forklift_prefix,
			Set<TagReadEvent> tags) {
		for (TagReadEvent tag : tags) {
			if (tag.getTag().getFormattedID().startsWith(forklift_prefix)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * Checks to see if the forklift is present.  
	 */
	private void checkForForklift(Set<TagReadEvent> stableSet) {
		if (this.forklift_present(forklift_prefix, stableSet)) {
			System.out.println("Forklift is present, there are "
					+ stableSet.size() + " items on the field at location "
					+ location + ".");
		} else {
			System.out.println("Forklift is not present, there are "
					+ stableSet.size() + " items present at location "
					+ location + ".");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.app.api.service.tagmonitor.StableSetSubscriber#
	 * stableSetReached(java.util.Set)
	 */
	@Override
	public void stableSetReached(Set<TagReadEvent> stableSet) {
		this.checkForForklift(stableSet);
	}
}

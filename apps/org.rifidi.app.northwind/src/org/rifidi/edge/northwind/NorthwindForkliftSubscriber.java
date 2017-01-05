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

import java.util.Set;

import org.rifidi.edge.api.service.tagmonitor.StableSetSubscriber;
import org.rifidi.edge.notification.TagReadEvent;

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

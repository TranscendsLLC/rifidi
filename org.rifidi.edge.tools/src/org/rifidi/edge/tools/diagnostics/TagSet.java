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
package org.rifidi.edge.tools.diagnostics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.rifidi.edge.notification.ReadCycle;
import org.rifidi.edge.notification.TagReadEvent;

/**
 * A TagSet is an object that represents the set of tags an ExposureRunner sends
 * to esper. TagSets should not be reused across multiple TagRunners (i.e. each
 * tag runner should have it's own instance of a TagSet), even if the Tag sets
 * contain the same list of TagReadData objects. This is because a TagSet also
 * keeps track of state. For instance, this first call to getNextGroup(10) will
 * return tags 0 to 9, while the next call to getNextGroup(10) will return tags
 * 10-19.
 * 
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class TagSet {

	/** The tags to be shown */
	private final List<AbstractReadData<?>> tagList;
	/** The tag index */
	private int currentIndex;

	/**
	 * @param tagList
	 */
	public TagSet(List<AbstractReadData<?>> tagList) {
		super();
		if (tagList == null) {
			throw new NullPointerException("TagList cannot be null");
		}
		if (tagList.size() < 1) {
			throw new IllegalArgumentException(
					"TagList must have at least one tag");
		}
		this.tagList = Collections.unmodifiableList(tagList);
	}

	/***
	 * Get a random group
	 * 
	 * @param groupSize
	 *            The size of the group
	 * @return
	 */
	public List<ReadCycle> getRandomGroup(int groupSize) {
		List<AbstractReadData<?>> tempList = new ArrayList<AbstractReadData<?>>(tagList);
		Collections.shuffle(tempList);
		return getGroup(tempList, groupSize, 0);
	}

	/**
	 * Get the next n tags. If the end of the list is reached, it will start
	 * over from 0.
	 * 
	 * @param groupSize
	 *            The number of tags to get
	 * @return
	 */
	public synchronized List<ReadCycle> getNextGroup(int groupSize) {
		List<ReadCycle> tempList = getGroup(tagList, groupSize, currentIndex);


		// calculates the new index value based on modular division
		currentIndex = (currentIndex + groupSize) % tagList.size();
		return tempList;
	}

	/**
	 * A helper method to return the tags from the group
	 * 
	 * @param tags
	 * @param groupSize
	 * @param firstIndex
	 * @return
	 */
	private List<ReadCycle> getGroup(List<AbstractReadData<?>> tags, int groupSize,
			int firstIndex) {
		List<AbstractReadData<?>> tempList = new ArrayList<AbstractReadData<?>>();
		int index = new Integer(firstIndex);
		while (tempList.size() < groupSize) {
			index++;
			if (index >= tags.size()) {
				index = 0;
			}
			tempList.add(tags.get(index));
		}
		return buildReadCycle(tempList);

	}

	/**
	 * A helper method to build a list of ReadCycle objects from TagReadData
	 * 
	 * @param tags
	 * @return
	 */
	private List<ReadCycle> buildReadCycle(List<AbstractReadData<?>> tags) {
		List<ReadCycle> readCycles = new ArrayList<ReadCycle>();
		for (AbstractReadData<?> tag : tags) {
			Set<TagReadEvent> tagReadEvents = new HashSet<TagReadEvent>();
			tagReadEvents.add(tag.getTagReadEvent());
			readCycles.add(new ReadCycle(tagReadEvents, tag.getReaderID(),
					System.currentTimeMillis()));
		}
		return readCycles;
	}
}

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
package org.rifidi.app.performance;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.api.service.tagmonitor.RawTagSubscriber;
import org.rifidi.edge.notification.TagReadEvent;

/**
 * The subscriber class. This class monitors the arrived and departed events for
 * all readers.
 * 
 * @author Matthew Dean - matt@transcends.co
 */
public class PerformanceSubscriber implements RawTagSubscriber {
	
	private static final Log logger = LogFactory.getLog(PerformanceSubscriber.class);

	private Long startTime = null;
	private Long endTime = null;
	private int currentCount = 0;
	private static final int TOTAL_COUNT = 10000;

	/**
	 * Constructor
	 * 
	 * @param conn
	 *            The database connection
	 */
	public PerformanceSubscriber() {
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
		if (startTime == null) {
			startTime = System.currentTimeMillis();
		}
		currentCount++;
		if (currentCount == TOTAL_COUNT) {
			endTime = System.currentTimeMillis();
			long seconds = ((endTime-startTime)/1000);
			long tagsPerSecond = TOTAL_COUNT/seconds;
			logger.info(TOTAL_COUNT + " tags read, number of seconds elapsed is " + seconds + ", tags per second is: " + tagsPerSecond);
		}
	}
}

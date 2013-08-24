/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.api.service.tagmonitor;

import java.util.Set;

import org.rifidi.edge.api.service.RifidiAppSubscriber;
import org.rifidi.edge.notification.TagReadEvent;

/**
 * This interface should be implemented by an object that subscribes to the
 * UniqueTagBatchInterval Service
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface UniqueTagBatchIntervalSubscriber extends RifidiAppSubscriber {

	/**
	 * This method is called by the UniqueTagBatchIntervalService at regular
	 * intervals with the unique tags seen during that interval
	 * 
	 * @param tags
	 */
	void tagBatchSeen(Set<TagReadEvent> tags);

}

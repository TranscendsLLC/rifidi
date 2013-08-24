/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.api.service.tagmonitor;

import org.rifidi.edge.api.service.RifidiAppSubscriber;
import org.rifidi.edge.notification.TagReadEvent;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 *
 */
public interface UniqueTagIntervalSubscriber extends RifidiAppSubscriber{
	
	void tagSeen(TagReadEvent tag);

}

/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.adapter.awid.awid2010.communication;

import org.rifidi.edge.notification.TagReadEvent;

/**
 * This is an interfaces for classes that extend AbstractAwidMessage which
 * provide TagData to read. It provides a common interface to extract tag data
 * from an AbstractAwidMessage.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface TagResponseMessage {

	/**
	 * Parse the data held in this awid message into a tagReadEvent object.
	 * 
	 * @return A TagReadEvent that represents tag data held in this message.
	 */
	public TagReadEvent getTagReadEvent();

}

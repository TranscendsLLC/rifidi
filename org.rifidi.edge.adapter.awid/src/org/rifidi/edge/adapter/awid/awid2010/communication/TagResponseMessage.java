/*
 * AwidTagResponseMessage.java
 * 
 * Created:     Oct 20th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:     The software in this package is published under the terms of the EPL License
 *                   A copy of the license is included in this distribution under Rifidi-License.txt 
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

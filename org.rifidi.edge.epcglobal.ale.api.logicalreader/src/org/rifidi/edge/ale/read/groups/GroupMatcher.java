/*
 * 
 * GroupMatcher.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
/**
 * 
 */
package org.rifidi.edge.ale.read.groups;

import java.util.List;
import java.util.Map;

import org.rifidi.edge.notification.DatacontainerEvent;

/**
 * @author jochen
 * 
 */
public interface GroupMatcher {
	/**
	 * Try to put the given tag into the group. Returns true on success.
	 * 
	 * @param matchees
	 */
	public boolean match(String input, DatacontainerEvent event);

	/**
	 * Get a map containing the group name as key and a list of tags as value.
	 * 
	 * @return
	 */
	public Map<String, List<DatacontainerEvent>> getGrouped();
}

/*
 *  CommandList.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.core.rmi.api.readerconnection.returnobjects;

import java.io.Serializable;
import java.util.Set;

/**
 * 
 * This is a Value object that stores commands and the groups that the command
 * belongs to. It exists to make RMI calls more efficient
 * 
 * @author kyle
 * 
 */
public class CommandGroupMap implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;

	private Set<String> groups;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<String> getGroups() {
		return groups;
	}

	public void setGroups(Set<String> groups) {
		this.groups = groups;
	}

}

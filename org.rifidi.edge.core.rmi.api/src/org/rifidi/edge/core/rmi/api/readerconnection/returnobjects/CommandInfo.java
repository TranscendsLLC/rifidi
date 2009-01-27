/*
 *  CommandInfo.java
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

/**
 * This is a value object that combines information about currently executing
 * commands for the purpose of making the RMI interface more efficient
 * 
 * @author kyle
 * 
 */
public class CommandInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String commandName;
	private long commandID;
	private String commandStatus;
	
	public CommandInfo(String commandName, long commandID, String commandStatus){
		this.commandID = commandID;
		this.commandName = commandName;
		this.commandStatus = commandStatus;
	}

	public String getCommandName() {
		return commandName;
	}

	public long getCommandID() {
		return commandID;
	}

	public String getCommandStatus() {
		return commandStatus;
	}

}

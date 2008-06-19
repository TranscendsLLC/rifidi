/*
 *  ThingMagicCustomCommand.java
 *
 *  Created:	Jun 19, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.readerPlugin.thingmagic.commands;

import org.rifidi.edge.core.readerPlugin.commands.ICustomCommand;

public class ThingMagicCustomCommand implements ICustomCommand {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6632279831996948561L;
	
	private String customCommand;

	/**
	 * @return the customCommand
	 */
	public String getCustomCommand() {
		return customCommand;
	}

	/**
	 * @param customCommand the customCommand to set
	 */
	public void setCustomCommand(String customCommand) {
		this.customCommand = customCommand;
	}
	
	
	
}

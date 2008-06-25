/*
 *  ThingMagicCustomCommandResult.java
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

import org.rifidi.edge.core.readerPlugin.commands.ICustomCommandResult;

/**
 * @author Jerry Maine - jerry@pramari.com
 *
 */
public class ThingMagicCustomCommandResult implements ICustomCommandResult {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2905063122064512338L;
	
	String result;

	public ThingMagicCustomCommandResult(String result){
		setResult(result);
	}
	
	/**
	 * @return the result
	 */
	public String getResult() {
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(String result) {
		this.result = result;
	}
	

}

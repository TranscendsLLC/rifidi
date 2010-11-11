/*
 *  ThingmagicException.java
 *
 *  Created:	Sep 23, 2009
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.adapter.thingmagic.commandobject;

/**
 * 
 * 
 * @author Matthew Dean
 */
public class ThingmagicException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4277561381127258240L;

	/**
	 * 
	 * 
	 * @param message
	 */
	public ThingmagicException(String message) {
		super(message);
	}

}

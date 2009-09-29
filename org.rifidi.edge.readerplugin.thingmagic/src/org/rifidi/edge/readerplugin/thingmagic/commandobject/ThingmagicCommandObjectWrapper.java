/*
 *  ThingmagicCommandObjectWrapper.java
 *
 *  Created:	Sep 29, 2009
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.thingmagic.commandobject;


/**
 * 
 * 
 * @author Matthew Dean
 */
public class ThingmagicCommandObjectWrapper {
	/** The name of the property that this command get/sets */
	private String propertyName;
	/** The command to be executed */
	private ThingmagicCommandObject commandObject;

	/**
	 * @param propertyName
	 *            The name of the property that will be get/set
	 * @param commandObject
	 *            The command to be executed
	 */
	public ThingmagicCommandObjectWrapper(String propertyName,
			ThingmagicCommandObject commandObject) {
		this.propertyName = propertyName;
		this.commandObject = commandObject;
	}

	/**
	 * @return the propertyName
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * @return the commandObject
	 */
	public ThingmagicCommandObject getCommandObject() {
		return commandObject;
	}
}

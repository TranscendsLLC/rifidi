/*
 *  AlienCommandObjectWrapper.java
 *
 *  Created:	Mar 9, 2009
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.alien.commandobject;

/**
 * This is an object to wrap CommandObjects along with the name of the property
 * that the command object will get/set
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class AlienCommandObjectWrapper {

	/** The name of the property that this command get/sets */
	private String propertyName;
	/** The command to be executed */
	private AlienCommandObject commandObject;

	/**
	 * Constructor for AlienCommandObjectWraper.  
	 * 
	 * @param propertyName
	 *            The name of the property that will be get/set
	 * @param commandObject
	 *            The command to be executed
	 */
	public AlienCommandObjectWrapper(String propertyName,
			AlienCommandObject commandObject) {
		this.propertyName = propertyName;
		this.commandObject = commandObject;
	}

	/**
	 * Returns the property name.  
	 * 
	 * @return the propertyName
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * Returns the command object.  
	 * 
	 * @return the commandObject
	 */
	public AlienCommandObject getCommandObject() {
		return commandObject;
	}
}

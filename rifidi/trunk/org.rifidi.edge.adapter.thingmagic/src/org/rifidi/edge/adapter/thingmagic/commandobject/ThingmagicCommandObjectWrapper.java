/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.adapter.thingmagic.commandobject;


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

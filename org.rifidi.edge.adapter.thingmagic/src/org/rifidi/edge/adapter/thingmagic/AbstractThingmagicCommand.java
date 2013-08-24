/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.adapter.thingmagic;

import org.rifidi.edge.sensors.Command;

/**
 * A superclass for use by commands for the Thingmagic Reader
 * 
 * @author Matthew Dean
 */
public abstract class AbstractThingmagicCommand extends Command {

	/**
	 * Default constructor.  
	 * 
	 * @param commandID
	 */
	public AbstractThingmagicCommand(String commandID) {
		super(commandID);
	}
}

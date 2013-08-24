/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
/**
 * 
 */
package org.rifidi.edge.adapter.alien;

import org.rifidi.edge.sensors.TimeoutCommand;

/**
 * A superclass for use by commands for the Alien9800 Reader
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public abstract class AbstractAlien9800Command extends TimeoutCommand {

	/**
	 * Default Constructor
	 * 
	 * @param commandID
	 *            The FACTORY_ID of the command.
	 */
	public AbstractAlien9800Command(String commandID) {
		super(commandID);
	}
}

/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.adapter.thingmagic6.commands;

import java.util.concurrent.TimeoutException;

import org.rifidi.edge.adapter.thingmagic6.Thingmagic6SensorSession;
import org.rifidi.edge.sensors.TimeoutCommand;

/**
 * 
 * 
 * @author Matthew Dean (matt@pramari.com)
 */
public class Thingmagic6PushCommand extends TimeoutCommand {

	Thingmagic6SensorSession session = null;
	
	public Thingmagic6PushCommand(String commandID) {
		super(commandID);
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.sensors.TimeoutCommand#execute()
	 */
	@Override
	protected void execute() throws TimeoutException {
		this.session = (Thingmagic6SensorSession) this.sensorSession;
		System.out.println("Start reading!");
		session.startReading();
	}

}

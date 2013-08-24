/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.adapter.thinkify50;

import java.util.concurrent.TimeoutException;

import org.rifidi.edge.sensors.TimeoutCommand;

/**
 * @author matt
 * 
 */
public class Thinkify50PushCommand extends TimeoutCommand {

	Thinkify50SensorSession session = null;

	public Thinkify50PushCommand(String commandID) {
		super(commandID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.TimeoutCommand#execute()
	 */
	@Override
	protected void execute() throws TimeoutException {
		this.session = (Thinkify50SensorSession) this.sensorSession;
		session.startReading();
	}

}

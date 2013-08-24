/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.adapter.alien.commands;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.adapter.alien.AbstractAlien9800Command;
import org.rifidi.edge.adapter.alien.Alien9800ReaderSession;
import org.rifidi.edge.adapter.alien.commandobject.AlienCommandObject;
import org.rifidi.edge.adapter.alien.commandobject.AlienException;
import org.rifidi.edge.adapter.alien.commandobject.AlienSetCommandObject;

/**
 * A command to send to the Alien Reader that turns autonomous mode off
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AlienAutonomousModeStopCommand extends AbstractAlien9800Command {

	private static final Log logger = LogFactory
			.getLog(AlienAutonomousModeStopCommand.class);

	public AlienAutonomousModeStopCommand(String commandID) {
		super(commandID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void execute() throws TimeoutException {
		AlienCommandObject setNotifyMode = new AlienSetCommandObject(
				"notifyMode", "off",
				(Alien9800ReaderSession) this.sensorSession);
		AlienCommandObject setAutoMode = new AlienSetCommandObject("automode",
				"off", (Alien9800ReaderSession) this.sensorSession);
		try {
			setNotifyMode.execute();
			setAutoMode.execute();
		} catch (IOException e) {
			logger.warn("Exception while executing command: " + e.getMessage());
		} catch (AlienException e) {
			logger.warn("Exception while executing command: " + e.getMessage());
		} catch (Exception e) {
			logger.warn("Exception while executing command: " + e.getMessage());
		}

	}

}

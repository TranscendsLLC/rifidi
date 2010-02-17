/*
 * Awid2010StopCommand.java
 * 
 * Created:     Oct 20th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:     The software in this package is published under the terms of the EPL License
 *                   A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.readerplugin.awid.commands.awid2010;

import java.io.IOException;

import org.rifidi.edge.core.sensors.commands.Command;
import org.rifidi.edge.readerplugin.awid.awid2010.AwidSession;
import org.rifidi.edge.readerplugin.awid.awid2010.communication.commands.StopCommand;

/**
 * A command to stop commands running on the Awid
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AwidStopCommand extends Command {

	public AwidStopCommand(String commandID) {
		super(commandID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		StopCommand command = new StopCommand();
		try {
			((AwidSession) super.sensorSession).sendMessage(command);
		} catch (IOException e) {
		}

	}

}

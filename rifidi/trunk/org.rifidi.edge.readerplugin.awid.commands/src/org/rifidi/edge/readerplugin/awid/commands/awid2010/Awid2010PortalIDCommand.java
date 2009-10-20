/*
 * Awid2010PortalIDCommand.java
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
import org.rifidi.edge.readerplugin.awid.awid2010.Awid2010Session;
import org.rifidi.edge.readerplugin.awid.awid2010.communication.commands.AntennaSourceCommand;
import org.rifidi.edge.readerplugin.awid.awid2010.communication.commands.Gen2PortalIDCommand;

/**
 * A command to start reading Gen2Tags. It sends two awid commands. The first
 * turns on antenna reporting. The second is a Gen2PortalID
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class Awid2010PortalIDCommand extends Command {

	public Awid2010PortalIDCommand(String commandID) {
		super(commandID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		AntennaSourceCommand antennaCommand = new AntennaSourceCommand();
		Gen2PortalIDCommand command = new Gen2PortalIDCommand();
		try {
			((Awid2010Session) super.sensorSession).sendMessage(antennaCommand);
			((Awid2010Session) super.sensorSession).sendMessage(command);
		} catch (IOException e) {
		}

	}

}

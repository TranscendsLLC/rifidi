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
import org.rifidi.edge.readerplugin.awid.awid2010.AwidSession;
import org.rifidi.edge.readerplugin.awid.awid2010.communication.commands.AntennaSourceCommand;
import org.rifidi.edge.readerplugin.awid.awid2010.communication.commands.AntennaSwitchCommand;
import org.rifidi.edge.readerplugin.awid.awid2010.communication.commands.Gen2ReadBlockDataCommand;

/**
 * A command to start reading Gen2Tags. It sends three awid commands. The first two
 * commands turn on antenna reporting. The third is a Gen2 Read Block Data.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * @author Daniel Gómez - dgomez@idlinksolutions.com
 * 
 */
public class AwidReadBlockDataCommand extends Command {

	/** Memory bank id. */
	private byte memorybank;
	
	
	/**
	 * Create a new Awid2010ReadBlockDataCommand command
	 * @param commandID
	 *            The ID of the commandconfiguration that produced this
	 *            command(The RifidiService)	
	 *            	
	 * @param memoryBank
	 * 			0x00 Reserved bank
	 * 			0x01 EPC bank
	 * 			0x02 TID bank
	 * 			0x03 User bank
	 * 
	 * Command: 06 20 0D 02 XX XX
	 * Where:
	 * 02 – to retrieve TID
	 * ACK: 00 – Command accepted for execution
	 * FF – Command received in error
	 * Response23: 12 20 0D 71 00 20 00 80 0F 5A 60 BB 73 66 5B 00 xx xx
	 * or
	 * 0E 20 0D 71 00 08 20 05 DE A7 90 80 xx xx.
	 */
	public AwidReadBlockDataCommand(String commandID, byte memorybank) {
		super(commandID);
		this.memorybank=memorybank;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		AntennaSwitchCommand antennaSwitchCommand = new AntennaSwitchCommand(true);
		AntennaSourceCommand antennaCommand = new AntennaSourceCommand();
		Gen2ReadBlockDataCommand command = new Gen2ReadBlockDataCommand(this.memorybank);
		//Gen2PortalIDWithMaskCommand command = new Gen2PortalIDWithMaskCommand();
		try {
			((AwidSession) super.sensorSession).sendMessage(antennaSwitchCommand);
			((AwidSession) super.sensorSession).sendMessage(antennaCommand);
			((AwidSession) super.sensorSession).sendMessage(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
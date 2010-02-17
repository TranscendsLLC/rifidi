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
import org.rifidi.edge.readerplugin.awid.awid2010.communication.commands.Gen2PortalIDWithMaskCommand;

/**
 * A command to start reading Gen2Tags. It sends two awid commands. The first
 * turns on antenna reporting. The second is a Gen2PortalID
 * @author Kyle Neumeier - kyle@pramari.com
 * @author Daniel Gómez - dgomez@idlinksolutions.com
 * 
 */
public class AwidPortalIDWithMaskCommand extends Command {

	private byte packetLength;
	private byte memoryBank;
	private byte startingBit;
	private byte maskLength;
	private byte maskValue;
	private byte qValue;
	private byte timeout;
	private byte repeat;
	
	/**
	 * Create a new Awid2010PortalIDWithMaskCommand command
	 * @param commandID
	 *            The ID of the commandconfiguration that produced this
	 *            command(The RifidiService)		
	 * 
	 * @param packetLength
	 * 			1-byte packet length, value depending on how long the mask is or simply MaskLength plus
	 * 			fourteen
	 * @param memoryBank
	 * 			0x00 Reserved bank
	 * 			0x01 EPC bank
	 * 			0x02 TID bank
	 * 			0x03 User bank
	 * 
	 * @param startingBit
	 * 			starting bit position in memory bank
	 * 
	 * @param maskLength
	 * 			Mask length ex:
	 * 			0x06	6 bits
	 * 
	 * @param maskValue
	 *  		Mask value for bit mask ex:
	 *  		0xFC	"11111100"	
	 * 
	 * @param qValue
	 * 			For example, if there are about 20 tags to be read, then a Q Value of 4 should be 
	 * 			used for reader to have 15 (2^4-1) time slots employed by its searching algorithm and 5
	 * 			for 35 tags, 7 for 131 tags and so on.	
	 * 
	 * @param timeout
	 *            Execute this command for timeout*100 ms. If set to 0x00,
	 *            execute until stop command is sent
	 * @param repeat
	 *            Return results every repeat*100 ms. If set to 0x00
	 *            continuously return tags.
	 */
	public AwidPortalIDWithMaskCommand(String commandID, byte packetLength,
			byte memoryBank, byte startingBit, byte maskLength, byte maskValue,
			byte qValue, byte timeout, byte repeat) {
		super(commandID);
		this.packetLength = packetLength;
		this.memoryBank = memoryBank;
		this.startingBit = startingBit;
		this.maskLength = maskLength;
		this.maskValue = maskValue;
		this.qValue = qValue;
		this.timeout = timeout;
		this.repeat = repeat;
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
		Gen2PortalIDWithMaskCommand command = new Gen2PortalIDWithMaskCommand(this.packetLength,
				this.memoryBank, this.startingBit, this.maskLength, this.maskValue, this.qValue, 
				this.timeout, this.repeat);
		try {
			((AwidSession) super.sensorSession).sendMessage(antennaSwitchCommand);
			((AwidSession) super.sensorSession).sendMessage(antennaCommand);
			((AwidSession) super.sensorSession).sendMessage(command);
		} catch (IOException e) {
		}
	}

}

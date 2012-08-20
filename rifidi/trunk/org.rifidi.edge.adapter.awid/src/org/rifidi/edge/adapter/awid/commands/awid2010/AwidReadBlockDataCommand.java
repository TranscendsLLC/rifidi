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
package org.rifidi.edge.adapter.awid.commands.awid2010;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.adapter.awid.awid2010.AwidSession;
import org.rifidi.edge.adapter.awid.awid2010.communication.commands.AntennaSourceCommand;
import org.rifidi.edge.adapter.awid.awid2010.communication.commands.AntennaSwitchCommand;
import org.rifidi.edge.adapter.awid.awid2010.communication.commands.Gen2ReadBlockDataCommand;
import org.rifidi.edge.adapter.awid.awid2010.communication.commands.StopCommand;
import org.rifidi.edge.adapter.awid.awid2010.communication.messages.AckMessage;
import org.rifidi.edge.adapter.awid.awid2010.communication.messages.Gen2ReadBlockDataResponse;
import org.rifidi.edge.sensors.ByteMessage;
import org.rifidi.edge.sensors.TimeoutCommand;

/**
 * A command to start reading Gen2Tags. It sends three awid commands. The first
 * two commands turn on antenna reporting. The third is a Gen2 Read Block Data.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * @author Daniel G�mez - dgomez@idlinksolutions.com
 * 
 */
public class AwidReadBlockDataCommand extends TimeoutCommand {

	/** Memory bank id. */
	private byte memorybank;
	private static final Log logger = LogFactory
			.getLog(AwidReadBlockDataCommand.class);

	/**
	 * Create a new Awid2010ReadBlockDataCommand command
	 * 
	 * @param commandID
	 *            The ID of the commandconfiguration that produced this
	 *            command(The RifidiService)
	 * 
	 * @param memoryBank
	 *            0x00 Reserved bank 0x01 EPC bank 0x02 TID bank 0x03 User bank
	 * 
	 *            Command: 06 20 0D 02 XX XX Where: 02 � to retrieve TID ACK: 00
	 *            � Command accepted for execution FF � Command received in
	 *            error Response23: 12 20 0D 71 00 20 00 80 0F 5A 60 BB 73 66 5B
	 *            00 xx xx or 0E 20 0D 71 00 08 20 05 DE A7 90 80 xx xx.
	 */
	public AwidReadBlockDataCommand(String commandID, byte memorybank) {
		super(commandID);
		this.memorybank = memorybank;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.sensors.commands.TimeoutCommand#execute()
	 */
	@Override
	protected void execute() throws TimeoutException {
		AntennaSwitchCommand antennaSwitchCommand = new AntennaSwitchCommand(
				true);
		AntennaSourceCommand antennaCommand = new AntennaSourceCommand();
		Gen2ReadBlockDataCommand command = new Gen2ReadBlockDataCommand(
				this.memorybank);
		AwidSession session = (AwidSession) super.sensorSession;
		try {
			session.sendMessage(antennaSwitchCommand);
			ByteMessage response = session.getEndpoint().receiveMessage();
			AckMessage ack = new AckMessage(response.message);
			if (!ack.isSuccessful()) {
				logger.warn(antennaSwitchCommand + " was not successful");
			}

			session.sendMessage(antennaCommand);
			response = session.getEndpoint().receiveMessage();
			ack = new AckMessage(response.message);
			if (!ack.isSuccessful()) {
				logger.warn(antennaCommand + " was not successful");
			}
			session.sendMessage(command);
			ack = new AckMessage(response.message);
			if (!ack.isSuccessful()) {
				logger.warn(antennaCommand + " was not successful");
			}
			try {
				response = session.getEndpoint().receiveMessage();
				Gen2ReadBlockDataResponse data = new Gen2ReadBlockDataResponse(
						response.message, memorybank, super.sensorSession
								.getSensor().getID());
				// TODO: put data in Esper
			} catch (TimeoutException e) {
				// Ignore timeout exceptions from this command since it only
				// returns once it has seen something.
			}
			session.sendMessage(new StopCommand());
			response = session.getEndpoint().receiveMessage();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
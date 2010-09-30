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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.sensors.commands.TimeoutCommand;
import org.rifidi.edge.core.sensors.messages.ByteMessage;
import org.rifidi.edge.core.services.notification.ReadCycle;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;
import org.rifidi.edge.readerplugin.awid.awid2010.AwidSession;
import org.rifidi.edge.readerplugin.awid.awid2010.communication.commands.AbstractAwidCommand;
import org.rifidi.edge.readerplugin.awid.awid2010.communication.commands.AntennaSourceCommand;
import org.rifidi.edge.readerplugin.awid.awid2010.communication.commands.AntennaSwitchCommand;
import org.rifidi.edge.readerplugin.awid.awid2010.communication.commands.AntennaSwitchRateCommand;
import org.rifidi.edge.readerplugin.awid.awid2010.communication.commands.Gen2PortalIDCommand;
import org.rifidi.edge.readerplugin.awid.awid2010.communication.commands.Gen2PortalIDWithMaskCommand;
import org.rifidi.edge.readerplugin.awid.awid2010.communication.messages.AckMessage;
import org.rifidi.edge.readerplugin.awid.awid2010.communication.messages.Gen2PortalIDResponse;

/**
 * A command to start reading Gen2Tags. It sends two awid commands. The first
 * turns on antenna reporting. The second is a Gen2PortalID.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * @author Daniel G�mez - dgomez@idlinksolutions.com
 * 
 */
public class AwidPortalIDCommand extends TimeoutCommand {

	/** The logger for this class */
	private static final Log logger = LogFactory
			.getLog(AwidPortalIDCommand.class);
	private byte packetLength;
	private byte memoryBank;
	private byte startingBit;
	private byte maskLength;
	private byte maskValue;
	private byte qValue;
	private byte timeout;
	private byte repeat;
	private boolean useMask;

	/**
	 * 
	 * @param commandID
	 * @param timeout
	 *            Execute this command for timeout*100 ms. The timeout cannot be
	 *            set to 0x00, since this command would never return
	 * 
	 */
	public AwidPortalIDCommand(String commandID, byte timeout) {
		super(commandID);
		this.timeout = timeout;
		this.useMask = false;
	}

	/**
	 * Create a new Awid2010PortalIDWithMaskCommand command
	 * 
	 * @param commandID
	 *            The ID of the commandconfiguration that produced this
	 *            command(The RifidiService)
	 * 
	 * @param packetLength
	 *            1-byte packet length, value depending on how long the mask is
	 *            or simply MaskLength plus fourteen
	 * @param memoryBank
	 *            0x00 Reserved bank 0x01 EPC bank 0x02 TID bank 0x03 User bank
	 * 
	 * @param startingBit
	 *            starting bit position in memory bank
	 * 
	 * @param maskLength
	 *            Mask length ex: 0x06 6 bits
	 * 
	 * @param maskValue
	 *            Mask value for bit mask ex: 0xFC "11111100"
	 * 
	 * @param qValue
	 *            For example, if there are about 20 tags to be read, then a Q
	 *            Value of 4 should be used for reader to have 15 (2^4-1) time
	 *            slots employed by its searching algorithm and 5 for 35 tags, 7
	 *            for 131 tags and so on.
	 * 
	 * @param timeout
	 *            Execute this command for timeout*100 ms. The timeout cannot be
	 *            set to 0x00, since this command would never return
	 * @param repeat
	 *            Return results every repeat*100 ms. If set to 0x00
	 *            continuously return tags.
	 */
	public AwidPortalIDCommand(String commandID, byte packetLength,
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
		this.useMask = true;
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
		AntennaSwitchRateCommand switchRateCommand = new AntennaSwitchRateCommand(
				(byte) 3, (byte) 3);
		AwidSession session = (AwidSession) super.sensorSession;
		AbstractAwidCommand portalIDCommand;
		if (useMask) {
			portalIDCommand = new Gen2PortalIDWithMaskCommand(
					this.packetLength, this.memoryBank, this.startingBit,
					this.maskLength, this.maskValue, this.qValue, this.timeout,
					this.repeat);
		} else {
			portalIDCommand = new Gen2PortalIDCommand(calculateTimeOutByte(),
					(byte) 0x00);
		}
		session.getEndpoint().clearUndeliveredMessages();
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

			session.sendMessage(switchRateCommand);
			response = session.getEndpoint().receiveMessage();
			ack = new AckMessage(response.message);
			if (!ack.isSuccessful()) {
				logger.warn(antennaCommand + " was not successful");
			}

			session.sendMessage(portalIDCommand);
			response = session.getEndpoint().receiveMessage();
			ack = new AckMessage(response.message);
			if (!ack.isSuccessful()) {
				logger.warn(portalIDCommand + " was not successful");
			}

			//receive tag reads
			response = session.getEndpoint().receiveMessage(
					calculateSessionTimeout());
			List<Gen2PortalIDResponse> responses = new LinkedList<Gen2PortalIDResponse>();
			
			//receive tag messages until we get a timeout
			while (!isCommandDone(response)) {
				Gen2PortalIDResponse tagResponse = new Gen2PortalIDResponse(
						response.message, session.getSensor().getID(), true);
				responses.add(tagResponse);
				try {
					response = session.getEndpoint().receiveMessage(
							calculateSessionTimeout());
				} catch (TimeoutException e) {
					// Ignore this so we we are sure to collect the data we
					// already have.
					break;
				}
			}
			//put all tag reads into a single ReadCycle
			handleTags(responses);

		} catch (IOException e) {
			e.printStackTrace();
			logger.warn("PortalID Command did not complete because "
					+ "there was a problem with the session: " + session);
		}

	}

	/**
	 * A private helper method to
	 * 
	 * @return
	 */
	private int calculateSessionTimeout() {
		return Math.max((timeout * 100) + 1000, super.sensorSession
				.getTimeout());
	}

	/**
	 * A private method used for calculating the timeout byte from an integer
	 * that specifies the time to wait in millisconds.
	 * 
	 * @return
	 */
	private byte calculateTimeOutByte() {
		byte timeoutByte;
		if (timeout > 254) {
			timeoutByte = (byte) 0xFE;
		} else if (timeout <= 0) {
			timeoutByte = (byte) 0x01;
		} else {
			timeoutByte = (byte) timeout;
		}
		return timeoutByte;
	}

	private boolean isCommandDone(ByteMessage bm) {
		return bm.message[1] == (byte) 0xFF && bm.message[2] == (byte) 0x1E;
	}

	private void handleTags(List<Gen2PortalIDResponse> tagResponses) {
		Set<TagReadEvent> tags = new HashSet<TagReadEvent>();
		for (Gen2PortalIDResponse tagResponse : tagResponses) {
			tags.add(tagResponse.getTagReadEvent());
		}
		ReadCycle readCycle = new ReadCycle(tags, super.sensorSession
				.getSensor().getID(), System.currentTimeMillis());
		super.sensorSession.getSensor().send(readCycle);
		//TODO: SEND TAGS
		//template.send(destination, new ReadCycleMessageCreator(readCycle));
	}

}

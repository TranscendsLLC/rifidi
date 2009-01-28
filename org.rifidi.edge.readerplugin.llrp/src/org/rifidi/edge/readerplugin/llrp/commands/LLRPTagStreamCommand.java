/* 
 * LLRPTagStreamCommand.java
 *  Created:	Jul 7, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.readerplugin.llrp.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.llrp.ltk.generated.enumerations.AISpecStopTriggerType;
import org.llrp.ltk.generated.enumerations.AccessReportTriggerType;
import org.llrp.ltk.generated.enumerations.AirProtocols;
import org.llrp.ltk.generated.enumerations.NotificationEventType;
import org.llrp.ltk.generated.enumerations.ROReportTriggerType;
import org.llrp.ltk.generated.enumerations.ROSpecStartTriggerType;
import org.llrp.ltk.generated.enumerations.ROSpecState;
import org.llrp.ltk.generated.enumerations.ROSpecStopTriggerType;
import org.llrp.ltk.generated.enumerations.StatusCode;
import org.llrp.ltk.generated.messages.ADD_ROSPEC;
import org.llrp.ltk.generated.messages.ADD_ROSPEC_RESPONSE;
import org.llrp.ltk.generated.messages.DELETE_ROSPEC;
import org.llrp.ltk.generated.messages.DELETE_ROSPEC_RESPONSE;
import org.llrp.ltk.generated.messages.DISABLE_ROSPEC;
import org.llrp.ltk.generated.messages.DISABLE_ROSPEC_RESPONSE;
import org.llrp.ltk.generated.messages.ENABLE_ROSPEC;
import org.llrp.ltk.generated.messages.ENABLE_ROSPEC_RESPONSE;
import org.llrp.ltk.generated.messages.GET_REPORT;
import org.llrp.ltk.generated.messages.GET_ROSPECS_RESPONSE;
import org.llrp.ltk.generated.messages.RO_ACCESS_REPORT;
import org.llrp.ltk.generated.messages.SET_READER_CONFIG;
import org.llrp.ltk.generated.messages.SET_READER_CONFIG_RESPONSE;
import org.llrp.ltk.generated.messages.START_ROSPEC;
import org.llrp.ltk.generated.messages.START_ROSPEC_RESPONSE;
import org.llrp.ltk.generated.parameters.AISpec;
import org.llrp.ltk.generated.parameters.AISpecStopTrigger;
import org.llrp.ltk.generated.parameters.AccessReportSpec;
import org.llrp.ltk.generated.parameters.C1G2EPCMemorySelector;
import org.llrp.ltk.generated.parameters.EPC_96;
import org.llrp.ltk.generated.parameters.EventNotificationState;
import org.llrp.ltk.generated.parameters.InventoryParameterSpec;
import org.llrp.ltk.generated.parameters.ROBoundarySpec;
import org.llrp.ltk.generated.parameters.ROReportSpec;
import org.llrp.ltk.generated.parameters.ROSpec;
import org.llrp.ltk.generated.parameters.ROSpecStartTrigger;
import org.llrp.ltk.generated.parameters.ROSpecStopTrigger;
import org.llrp.ltk.generated.parameters.ReaderEventNotificationSpec;
import org.llrp.ltk.generated.parameters.TagReportContentSelector;
import org.llrp.ltk.generated.parameters.TagReportData;
import org.llrp.ltk.types.Bit;
import org.llrp.ltk.types.LLRPInteger;
import org.llrp.ltk.types.LLRPMessage;
import org.llrp.ltk.types.UnsignedInteger;
import org.llrp.ltk.types.UnsignedShort;
import org.llrp.ltk.types.UnsignedShortArray;
import org.rifidi.edge.common.utilities.converter.ByteAndHexConvertingUtility;
import org.rifidi.edge.core.api.communication.Connection;
import org.rifidi.edge.core.api.exceptions.RifidiMessageQueueException;
import org.rifidi.edge.core.api.messageQueue.MessageQueue;
import org.rifidi.edge.core.api.readerplugin.commands.Command;
import org.rifidi.edge.core.api.readerplugin.commands.CommandConfiguration;
import org.rifidi.edge.core.api.readerplugin.commands.CommandReturnStatus;
import org.rifidi.edge.core.api.readerplugin.messages.impl.TagMessage;

/**
 * 
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class LLRPTagStreamCommand implements Command {

	private boolean running = false;

	/**
	 * The ID of the ROSpec that is used.
	 */
	private static int ROSPEC_ID = 1;

	private Log logger = LogFactory.getLog(LLRPTagStreamCommand.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.commands.Command#start(org.rifidi.edge.core.communication.Connection,
	 *      org.rifidi.edge.core.messageQueue.MessageQueue)
	 */
	@Override
	public CommandReturnStatus start(Connection connection,
			MessageQueue messageQueue, MessageQueue errorQueue,
			CommandConfiguration configuration, long commandID) {
		running = true;
		int messageID = 1;
		CommandReturnStatus retVal = null;
		try {
			SET_READER_CONFIG config = createSetReaderConfig();
			config.setMessageID(new UnsignedInteger(messageID++));
			logger.debug("before send config.  Connection: "
					+ connection.toString());
			connection.sendMessage(config);
			logger.debug("after send config");

			SET_READER_CONFIG_RESPONSE setReaderConfigResponse = (SET_READER_CONFIG_RESPONSE) connection
					.receiveMessage();

			StatusCode sc = setReaderConfigResponse.getLLRPStatus()
					.getStatusCode();
			if (sc.intValue() != StatusCode.M_Success) {
				logger.debug("SET_READER_CONFIG returned with status code "
						+ sc.intValue());
				return CommandReturnStatus.UNSUCCESSFUL;
			}

			// TODO: //check to make sure this ROSpec is not already being used.

			// CREATE an ADD_ROSPEC Message and send it to the reader
			ADD_ROSPEC addROSpec = new ADD_ROSPEC();
			addROSpec.setROSpec(createROSpec());
			addROSpec.setMessageID(new UnsignedInteger(messageID++));
			connection.sendMessage(addROSpec);

			ADD_ROSPEC_RESPONSE addROSpecResponse = (ADD_ROSPEC_RESPONSE) connection
					.receiveMessage();
			sc = addROSpecResponse.getLLRPStatus().getStatusCode();
			if (sc.intValue() != StatusCode.M_Success) {
				logger.debug("ADD_ROSPEC_RESPONSE returned with status code "
						+ sc.intValue());
				return CommandReturnStatus.UNSUCCESSFUL;
			}

			// Create an ENABLE_ROSPEC message and send it to the reader
			ENABLE_ROSPEC enableROSpec = new ENABLE_ROSPEC();
			enableROSpec.setROSpecID(new UnsignedInteger(ROSPEC_ID));
			enableROSpec.setMessageID(new UnsignedInteger(messageID++));
			connection.sendMessage(enableROSpec);

			ENABLE_ROSPEC_RESPONSE enableROSpecResponse = (ENABLE_ROSPEC_RESPONSE) connection
					.receiveMessage();
			sc = enableROSpecResponse.getLLRPStatus().getStatusCode();
			if (sc.intValue() != StatusCode.M_Success) {
				logger
						.debug("ENABLE_ROSPEC_RESPONSE returned with status code "
								+ sc.intValue());
				return CommandReturnStatus.UNSUCCESSFUL;
			}

			// Create a START_ROSPEC message and send it to the reader
			START_ROSPEC startROSpec = new START_ROSPEC();
			startROSpec.setROSpecID(new UnsignedInteger(ROSPEC_ID));
			startROSpec.setMessageID(new UnsignedInteger(messageID++));
			connection.sendMessage(startROSpec);

			START_ROSPEC_RESPONSE startROSpecResponse = (START_ROSPEC_RESPONSE) connection
					.receiveMessage();
			sc = startROSpecResponse.getLLRPStatus().getStatusCode();
			if (sc.intValue() != StatusCode.M_Success) {
				logger.debug("START_ROSPEC_RESPONSE returned with status code "
						+ sc.intValue());
				return CommandReturnStatus.UNSUCCESSFUL;
			}

			while (running) {
				GET_REPORT getReport = new GET_REPORT();
				getReport.setMessageID(new UnsignedInteger(messageID++));

				connection.sendMessage(getReport);

				LLRPMessage message = (LLRPMessage) connection.receiveMessage();

				if (message instanceof RO_ACCESS_REPORT) {
					for (TagMessage tm : parseTags(message)) {
						try {
							messageQueue.addMessage(tm);
						} catch (RifidiMessageQueueException e) {
							retVal = CommandReturnStatus.INTERRUPTED;
							running = false;
						}
					}
				}

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}

			// Create a DISABLE_ROSPEC message and send it to the reader
			DISABLE_ROSPEC disableROSpec = new DISABLE_ROSPEC();
			disableROSpec.setROSpecID(new UnsignedInteger(ROSPEC_ID));
			disableROSpec.setMessageID(new UnsignedInteger(messageID++));
			connection.sendMessage(disableROSpec);

			DISABLE_ROSPEC_RESPONSE disableROSpecResponse = (DISABLE_ROSPEC_RESPONSE) connection
					.receiveMessage();
			sc = disableROSpecResponse.getLLRPStatus().getStatusCode();
			if (sc.intValue() != StatusCode.M_Success) {
				logger
						.debug("DISABLE_ROSPEC_RESPONSE returned with status code "
								+ sc.intValue());
				return CommandReturnStatus.UNSUCCESSFUL;
			}

			// Create a DELTE_ROSPEC message and send it to the reader
			DELETE_ROSPEC deleteROSpec = new DELETE_ROSPEC();
			deleteROSpec.setROSpecID(new UnsignedInteger(ROSPEC_ID));
			deleteROSpec.setMessageID(new UnsignedInteger(messageID++));
			connection.sendMessage(deleteROSpec);

			DELETE_ROSPEC_RESPONSE deleteROSpecResponse = (DELETE_ROSPEC_RESPONSE) connection
					.receiveMessage();
			sc = deleteROSpecResponse.getLLRPStatus().getStatusCode();
			if (sc.intValue() != StatusCode.M_Success) {
				logger
						.debug("DELETE_ROSPEC_RESPONSE returned with status code "
								+ sc.intValue());
				return CommandReturnStatus.UNSUCCESSFUL;
			}
		} catch (ClassCastException ex) {
			logger.error(ex.getMessage());
			return CommandReturnStatus.UNSUCCESSFUL;

		} catch (IOException e) {
			logger.error(e.getMessage());
			// TODO: remove this
			e.printStackTrace();
			return CommandReturnStatus.UNSUCCESSFUL;
		}

		if (retVal == null) {
			retVal = CommandReturnStatus.SUCCESSFUL;
		}

		return retVal;

	}

	/**
	 * Parses the tags from xml.
	 * 
	 * @param msg
	 *            The tag message to parse.
	 * @return A list of TagRead objects representing tags.
	 */
	private List<TagMessage> parseTags(LLRPMessage msg) {

		List<TagMessage> retVal = new ArrayList<TagMessage>();

		if (msg == null) {
			return retVal;
		}

		RO_ACCESS_REPORT rar = (RO_ACCESS_REPORT) msg;

		// TagReportData list parse
		for (TagReportData t : rar.getTagReportDataList()) {
			TagMessage newTag = new TagMessage();
			EPC_96 id = (EPC_96) t.getEPCParameter();
			newTag.setId(ByteAndHexConvertingUtility.fromHexString(id.getEPC()
					.toString()));
			// TODO: Either our classes or the Toolkit
			// handles this in the wrong way.
			// newTag.setLastSeenTime(t.getLastSeenTimestampUTC().getMicroseconds().toLong());
			newTag.setLastSeenTime(System.nanoTime());
			retVal.add(newTag);
		}

		return retVal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.commands.Command#stop()
	 */
	@Override
	public void stop() {
		running = false;
	}

	/**
	 * Finds the next acceptable ROSpec number.
	 * 
	 * @param grr
	 *            The GET_ROSPECS_RESPONSE, which will list all ROSpec IDs
	 *            already in use.
	 */
	@SuppressWarnings("unused")
	private void findNextInt(GET_ROSPECS_RESPONSE grr) {
		ROSPEC_ID++;
		boolean isDone = false;
		while (!isDone) {
			isDone = true;
			for (ROSpec r : grr.getROSpecList()) {
				if (r.getROSpecID().intValue() == ROSPEC_ID) {
					isDone = false;
					ROSPEC_ID++;
				}
			}
		}
	}

	/**
	 * Does the ROSpec exist?
	 * 
	 * @param grr
	 *            The GET_ROSPECS_RESPONSE search through.
	 * @return Returns true if the default ROSpec exists, false if it does not
	 *         exist.
	 */
	@SuppressWarnings("unused")
	private boolean doesRoSpecExist(GET_ROSPECS_RESPONSE grr) {
		boolean retVal = false;

		for (ROSpec r : grr.getROSpecList()) {
			if (r.getROSpecID().toInteger() == ROSPEC_ID) {
				retVal = true;
			}
		}

		return retVal;
	}

	/**
	 * This method creates a SET_READER_CONFIG method.
	 * 
	 * @return The SET_READER_CONFIG object.
	 */
	private SET_READER_CONFIG createSetReaderConfig() {
		SET_READER_CONFIG setReaderConfig = new SET_READER_CONFIG();

		// Create a default RoReportSpec so that reports are sent at the end of
		// ROSpecs
		ROReportSpec roReportSpec = new ROReportSpec();
		roReportSpec.setN(new UnsignedShort(0));
		roReportSpec.setROReportTrigger(new ROReportTriggerType(
				ROReportTriggerType.Upon_N_Tags_Or_End_Of_ROSpec));
		TagReportContentSelector tagReportContentSelector = new TagReportContentSelector();
		tagReportContentSelector.setEnableAccessSpecID(new Bit(0));
		tagReportContentSelector.setEnableAntennaID(new Bit(1));
		tagReportContentSelector.setEnableChannelIndex(new Bit(0));
		tagReportContentSelector.setEnableFirstSeenTimestamp(new Bit(0));
		tagReportContentSelector.setEnableInventoryParameterSpecID(new Bit(0));
		tagReportContentSelector.setEnableLastSeenTimestamp(new Bit(0));
		tagReportContentSelector.setEnablePeakRSSI(new Bit(0));
		tagReportContentSelector.setEnableROSpecID(new Bit(1));
		tagReportContentSelector.setEnableSpecIndex(new Bit(0));
		tagReportContentSelector.setEnableTagSeenCount(new Bit(0));
		C1G2EPCMemorySelector epcMemSel = new C1G2EPCMemorySelector();
		epcMemSel.setEnableCRC(new Bit(0));
		epcMemSel.setEnablePCBits(new Bit(0));
		tagReportContentSelector
				.addToAirProtocolEPCMemorySelectorList(epcMemSel);
		roReportSpec.setTagReportContentSelector(tagReportContentSelector);
		setReaderConfig.setROReportSpec(roReportSpec);

		// Set default AccessReportSpec

		AccessReportSpec accessReportSpec = new AccessReportSpec();
		accessReportSpec.setAccessReportTrigger(new AccessReportTriggerType(
				AccessReportTriggerType.End_Of_AccessSpec));
		setReaderConfig.setAccessReportSpec(accessReportSpec);

		// Set up reporting for AISpec events, ROSpec events, and GPI Events

		ReaderEventNotificationSpec eventNoteSpec = new ReaderEventNotificationSpec();
		EventNotificationState noteState = new EventNotificationState();
		noteState.setEventType(new NotificationEventType(
				NotificationEventType.AISpec_Event));
		noteState.setNotificationState(new Bit(0));
		eventNoteSpec.addToEventNotificationStateList(noteState);
		noteState = new EventNotificationState();
		noteState.setEventType(new NotificationEventType(
				NotificationEventType.ROSpec_Event));
		noteState.setNotificationState(new Bit(0));
		eventNoteSpec.addToEventNotificationStateList(noteState);
		noteState = new EventNotificationState();
		noteState.setEventType(new NotificationEventType(
				NotificationEventType.GPI_Event));
		noteState.setNotificationState(new Bit(0));
		eventNoteSpec.addToEventNotificationStateList(noteState);
		setReaderConfig.setReaderEventNotificationSpec(eventNoteSpec);

		setReaderConfig.setResetToFactoryDefault(new Bit(0));

		return setReaderConfig;
	}

	/**
	 * This method creates a ROSpec with null start and stop triggers
	 * 
	 * @return A ROSpec object.
	 */
	private ROSpec createROSpec() {
		// create a new rospec
		ROSpec roSpec = new ROSpec();
		roSpec.setPriority(new LLRPInteger(0));
		roSpec.setCurrentState(new ROSpecState(ROSpecState.Disabled));
		roSpec.setROSpecID(new UnsignedInteger(ROSPEC_ID));

		// set up ROBoundary (start and stop triggers)
		ROBoundarySpec roBoundarySpec = new ROBoundarySpec();

		ROSpecStartTrigger startTrig = new ROSpecStartTrigger();
		startTrig.setROSpecStartTriggerType(new ROSpecStartTriggerType(
				ROSpecStartTriggerType.Null));
		roBoundarySpec.setROSpecStartTrigger(startTrig);

		ROSpecStopTrigger stopTrig = new ROSpecStopTrigger();
		stopTrig.setDurationTriggerValue(new UnsignedInteger(100));
		stopTrig.setROSpecStopTriggerType(new ROSpecStopTriggerType(
				ROSpecStopTriggerType.Null));
		roBoundarySpec.setROSpecStopTrigger(stopTrig);

		roSpec.setROBoundarySpec(roBoundarySpec);

		// Add an AISpec
		AISpec aispec = new AISpec();

		// set AI Stop trigger to null
		AISpecStopTrigger aiStopTrigger = new AISpecStopTrigger();
		aiStopTrigger.setAISpecStopTriggerType(new AISpecStopTriggerType(
				AISpecStopTriggerType.Null));
		aiStopTrigger.setDurationTrigger(new UnsignedInteger(0));
		aispec.setAISpecStopTrigger(aiStopTrigger);

		UnsignedShortArray antennaIDs = new UnsignedShortArray();
		antennaIDs.add(new UnsignedShort(0));
		aispec.setAntennaIDs(antennaIDs);

		InventoryParameterSpec inventoryParam = new InventoryParameterSpec();
		inventoryParam.setProtocolID(new AirProtocols(
				AirProtocols.EPCGlobalClass1Gen2));
		inventoryParam.setInventoryParameterSpecID(new UnsignedShort(1));
		aispec.addToInventoryParameterSpecList(inventoryParam);

		roSpec.addToSpecParameterList(aispec);

		return roSpec;
	}

}

/*
 *  LLRPReaderPlugin.java
 *
 *  Created:	Jun 19, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.readerPlugin.llrp;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.llrp.ltk.exceptions.InvalidLLRPMessageException;
import org.llrp.ltk.generated.enumerations.AISpecStopTriggerType;
import org.llrp.ltk.generated.enumerations.AccessReportTriggerType;
import org.llrp.ltk.generated.enumerations.AirProtocols;
import org.llrp.ltk.generated.enumerations.NotificationEventType;
import org.llrp.ltk.generated.enumerations.ROReportTriggerType;
import org.llrp.ltk.generated.enumerations.ROSpecStartTriggerType;
import org.llrp.ltk.generated.enumerations.ROSpecState;
import org.llrp.ltk.generated.enumerations.ROSpecStopTriggerType;
import org.llrp.ltk.generated.messages.ADD_ROSPEC;
import org.llrp.ltk.generated.messages.CLOSE_CONNECTION;
import org.llrp.ltk.generated.messages.DELETE_ROSPEC;
import org.llrp.ltk.generated.messages.DISABLE_ROSPEC;
import org.llrp.ltk.generated.messages.ENABLE_ROSPEC;
import org.llrp.ltk.generated.messages.GET_ROSPECS;
import org.llrp.ltk.generated.messages.GET_ROSPECS_RESPONSE;
import org.llrp.ltk.generated.messages.RO_ACCESS_REPORT;
import org.llrp.ltk.generated.messages.SET_READER_CONFIG;
import org.llrp.ltk.generated.messages.START_ROSPEC;
import org.llrp.ltk.generated.messages.STOP_ROSPEC;
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
import org.rifidi.edge.core.communication.CommunicationService;
import org.rifidi.edge.core.communication.ICommunicationConnection;
import org.rifidi.edge.core.exception.readerConnection.RifidiConnectionException;
import org.rifidi.edge.core.readerPlugin.IReaderPlugin;
import org.rifidi.edge.core.readerPlugin.commands.ICustomCommand;
import org.rifidi.edge.core.readerPlugin.commands.ICustomCommandResult;
import org.rifidi.edge.core.tag.TagRead;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * This class represents a plugin which will talk with an LLRP Reader.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class LLRPReaderPlugin implements IReaderPlugin {

	/**
	 * The connection info for this reader
	 */
	private LLRPReaderInfo llrpri;

	/**
	 * The communication connection
	 */
	private ICommunicationConnection communicationConnection;

	/**
	 * The communication service for this reader.
	 */
	private CommunicationService communicationService;

	/**
	 * The log4j logger.
	 */
	private static Log logger = LogFactory.getLog(LLRPReaderPlugin.class);

	/**
	 * The ID of the ROSpec that is used.
	 */
	private static int ROSPEC_ID = 1;

	/**
	 * The LLRPReadThread that will read from the connection queue.
	 */
	private LLRPReadThread reader;

	/**
	 * Adapter for the Alien reader.
	 * 
	 * @param llrpri
	 *            The connection info for the Alien Reader.
	 */
	public LLRPReaderPlugin(LLRPReaderInfo llrpri) {
		this.llrpri = llrpri;
		ServiceRegistry.getInstance().service(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerAdapter.IReaderAdapter#connect()
	 */
	@Override
	public void connect() throws RifidiConnectionException {
		if (communicationService == null) {
			throw new RifidiConnectionException(
					"CommunicationSerivce Not Found!");
		}

		// Connect to the LLRP Reader
		try {
			communicationConnection = communicationService.createConnection(
					this, llrpri, new LLRPProtocol());

			reader = new LLRPReadThread(communicationConnection);

			reader.start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			throw new RifidiConnectionException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RifidiConnectionException(e);
		}

		SET_READER_CONFIG config = createSetReaderConfig();
		write(config, "Set Reader Config");

		// CREATE an ADD_ROSPEC Message and send it to the reader
		ADD_ROSPEC addROSpec = new ADD_ROSPEC();
		addROSpec.setROSpec(createROSpec());
		write(addROSpec, "ADD_ROSPEC");
		pause(250);

		// Create an ENABLE_ROSPEC message and send it to the reader
		ENABLE_ROSPEC enableROSpec = new ENABLE_ROSPEC();
		enableROSpec.setROSpecID(new UnsignedInteger(ROSPEC_ID));
		write(enableROSpec, "ENABLE_ROSPEC");
		pause(250);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerAdapter.IReaderAdapter#disconnect()
	 */
	@Override
	public void disconnect() throws RifidiConnectionException {
		// wait for one second before closing the connection

		// Create a DISABLE_ROSPEC message and send it to the reader
		DISABLE_ROSPEC disableROSpec = new DISABLE_ROSPEC();
		disableROSpec.setROSpecID(new UnsignedInteger(ROSPEC_ID));
		write(disableROSpec, "DISABLE_ROSPEC");

		// Create a DELTE_ROSPEC message and send it to the reader
		DELETE_ROSPEC deleteROSpec = new DELETE_ROSPEC();
		deleteROSpec.setROSpecID(new UnsignedInteger(ROSPEC_ID));
		write(deleteROSpec, "DELETE_ROSPEC");

		// Create a CLOSE_CONNECTION message and send it to the reader
		CLOSE_CONNECTION cc = new CLOSE_CONNECTION();
		write(cc, "CloseConnection");

		this.pause(1000);
	}

	/**
	 * This class is a allows LLRP messages to be read on a separate thread.  
	 * 
	 * @author Kyle Neumeier
	 * @author Andreas Huebner
	 * @author Matthew Dean
	 */
	private class LLRPReadThread extends Thread {

		/**
		 * A queue to store incoming LLRP Messages
		 */
		private LinkedBlockingQueue<LLRPMessage> commandQueue = null;

		/**
		 * A queue to store incoming LLRP Messages
		 */
		private LinkedBlockingQueue<LLRPMessage> tagQueue = null;

		/**
		 * A queue to store incoming LLRP Messages
		 */
		private LinkedBlockingQueue<LLRPMessage> roSpecListQueue = null;

		/**
		 * The connection object.
		 */
		private ICommunicationConnection connection;

		/**
		 * Thread for constant reading of the stream
		 * 
		 * @param inStream
		 */
		public LLRPReadThread(ICommunicationConnection comm) {
			this.commandQueue = new LinkedBlockingQueue<LLRPMessage>();
			this.tagQueue = new LinkedBlockingQueue<LLRPMessage>();
			this.roSpecListQueue = new LinkedBlockingQueue<LLRPMessage>();
			this.connection = comm;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			super.run();
			boolean isRunning = true;
			while (isRunning) {
				LLRPMessage message = null;
				try {
					message = read();
					if (message != null) {
						// If the message isn't a RO_ACCESS_REPORT, it is
						// going on the command queue.
						if (!(message instanceof RO_ACCESS_REPORT)
								&& !(message instanceof GET_ROSPECS_RESPONSE)) {
							commandQueue.put(message);
							logger.debug("ADDING A NON-TAG MESSAGE: "
									+ message.getMessageID().toString());
							logger.info("Received Message: \n"
									+ message.toXMLString());
							commandQueue.clear();
						} else if (message instanceof RO_ACCESS_REPORT) {
							logger.debug("ADDING A TAG MESSAGE");
							tagQueue.clear();
							tagQueue.put(message);
						} else if (message instanceof GET_ROSPECS_RESPONSE) {
							logger.debug("ADDING A GET ROSPEC RESPONSE");
							roSpecListQueue.clear();
							roSpecListQueue.put(message);
						}
					} else {
						logger.info("closing socket");
						isRunning = false;
					}
				} catch (IOException e) {
					logger.error("Error while reading message", e);
					isRunning = false;
				} catch (InvalidLLRPMessageException e) {
					isRunning = false;
					logger.error("Error while reading message", e);
				} catch (InterruptedException e) {
					isRunning = false;
					logger.error("Error while reading message", e);
				}

			}
		}

		/**
		 * Read everything from the stream until the socket is closed
		 * 
		 * @throws InvalidLLRPMessageException
		 */
		public LLRPMessage read() throws IOException,
				InvalidLLRPMessageException {
			LLRPMessage m = (LLRPMessage) connection.recieve();
			return m;
		}

		/**
		 * Receive the next Message
		 * 
		 * @return returns the Message form the Queue and removes it. It blocks
		 *         if there is no Message.
		 */
		public LLRPMessage getNextTags() {
			LLRPMessage m = null;
			try {
				m = tagQueue.poll(1000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				// nothing
			}
			return m;
		}

		/**
		 * Returns the next GET_ROSPEC_REQUEST method.
		 * 
		 * @return
		 */
		public LLRPMessage getNextRoSpecList() {
			LLRPMessage m = null;
			try {
				m = roSpecListQueue.take();
			} catch (InterruptedException e) {
				// nothing
			}
			return m;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerAdapter.IReaderAdapter#sendCommand(byte[])
	 */
	@Override
	public ICustomCommandResult sendCustomCommand(ICustomCommand customCommand) {

		return null;
	}

	/**
	 * Finds the next acceptable ROSpec number.
	 * 
	 * @param grr
	 *            The GET_ROSPECS_RESPONSE, which will list all ROSpec IDs
	 *            already in use.
	 */
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
		logger.debug("got past the while loop in the find next int");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerPlugin.IReaderPlugin#getNextTags()
	 */
	@Override
	public List<TagRead> getNextTags() {
		logger.debug("STARTING THE GETNEXTTAGS");

		GET_ROSPECS gr = new GET_ROSPECS();
		write(gr, "GET ROSPECS");
		GET_ROSPECS_RESPONSE grr = (GET_ROSPECS_RESPONSE) reader
				.getNextRoSpecList();

		logger.debug("Got past the get rospec response");
		if (!doesRoSpecExist(grr)) {

			logger.debug("ROSpec does not exist, creating a new one");
			findNextInt(grr);

			// CREATE an ADD_ROSPEC Message and send it to the reader
			ADD_ROSPEC addROSpec = new ADD_ROSPEC();
			addROSpec.setROSpec(createROSpec());
			write(addROSpec, "ADD_ROSPEC");
			pause(250);

			// Create an ENABLE_ROSPEC message and send it to the reader
			ENABLE_ROSPEC enableROSpec = new ENABLE_ROSPEC();
			enableROSpec.setROSpecID(new UnsignedInteger(ROSPEC_ID));
			write(enableROSpec, "ENABLE_ROSPEC");
			pause(250);
		}

		logger.debug("got past the if statement");

		List<TagRead> retVal = null;
		// Create a START_ROSPEC message and send it to the reader
		START_ROSPEC startROSpec = new START_ROSPEC();
		startROSpec.setROSpecID(new UnsignedInteger(ROSPEC_ID));
		write(startROSpec, "START_ROSPEC");

		pause(1000);

		// Create a STOP_ROSPEC message and send it to the reader
		STOP_ROSPEC stopROSpec = new STOP_ROSPEC();
		stopROSpec.setROSpecID(new UnsignedInteger(ROSPEC_ID));
		write(stopROSpec, "STOP_ROSPEC");

		logger.debug("right before the tag read");

		retVal = parseTags(reader.getNextTags());

		return retVal;
	}

	/**
	 * Does the ROSpec exist?
	 * 
	 * @param grr
	 *            The GET_ROSPECS_RESPONSE search through.
	 * @return Returns true if the default ROSpec exists, false if it does not
	 *         exist.  
	 */
	private boolean doesRoSpecExist(GET_ROSPECS_RESPONSE grr) {
		try {
			logger.debug(grr.toXMLString());
		} catch (InvalidLLRPMessageException e) {
			e.printStackTrace();
		}

		boolean retVal = false;

		for (ROSpec r : grr.getROSpecList()) {
			if (r.getROSpecID().toInteger() == ROSPEC_ID) {
				retVal = true;
			}
		}

		return retVal;
	}

	/**
	 * Send a llrp message to the reader
	 * 
	 * @param msg
	 *            Message to be send
	 * @param message
	 *            Description for output purposes
	 */
	private void write(LLRPMessage msg, String message) {
		try {
			logger.debug(" Sending message: \n" + msg.toXMLString());
			communicationConnection.send(msg);
		} catch (IOException e) {
			logger.error("Couldn't send Command ", e);
		} catch (InvalidLLRPMessageException e) {
			logger.error("Couldn't send Command", e);
		}
	}

	/**
	 * This method creates a SET_READER_CONFIG method.  
	 * 
	 * @return		The SET_READER_CONFIG object.  
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
		noteState.setNotificationState(new Bit(1));
		eventNoteSpec.addToEventNotificationStateList(noteState);
		noteState = new EventNotificationState();
		noteState.setEventType(new NotificationEventType(
				NotificationEventType.ROSpec_Event));
		noteState.setNotificationState(new Bit(1));
		eventNoteSpec.addToEventNotificationStateList(noteState);
		noteState = new EventNotificationState();
		noteState.setEventType(new NotificationEventType(
				NotificationEventType.GPI_Event));
		noteState.setNotificationState(new Bit(1));
		eventNoteSpec.addToEventNotificationStateList(noteState);
		setReaderConfig.setReaderEventNotificationSpec(eventNoteSpec);

		setReaderConfig.setResetToFactoryDefault(new Bit(0));

		return setReaderConfig;
	}

	/**
	 * Parses the tags from xml.
	 * 
	 * @param msg
	 *            The tag message to parse.
	 * @return A list of TagRead objects representing tags.
	 */
	public List<TagRead> parseTags(LLRPMessage msg) {

		List<TagRead> retVal = new ArrayList<TagRead>();

		if (msg == null) {
			return retVal;
		}

		RO_ACCESS_REPORT rar = (RO_ACCESS_REPORT) msg;

		// TagReportData list parse
		for (TagReportData t : rar.getTagReportDataList()) {
			TagRead newTag = new TagRead();
			EPC_96 id = (EPC_96) t.getEPCParameter();
			newTag.setId(ByteAndHexConvertingUtility.fromHexString(id.getEPC()
					.toString()));
			// TODO: Either our classes or the Toolkit handles this in the wrong
			// way.
			// newTag.setLastSeenTime(t.getLastSeenTimestampUTC().getMicroseconds().toLong());
			newTag.setLastSeenTime(System.nanoTime());
			retVal.add(newTag);
		}

		return retVal;
	}

	/**
	 * Returns true if the adapter is blocking
	 */
	@Override
	public boolean isBlocking() {
		return false;
	}

	/**
	 * This method creates a ROSpec with null start and stop triggers
	 * 
	 * @return	A ROSpec object.  
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

	/**
	 * This method causes the calling thread to sleep for a specified number of
	 * milliseconds
	 * 
	 * @param ms
	 *            How many milliseconds to pause.
	 */
	private void pause(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Inject the communication service.
	 * 
	 * @param communicationService
	 *            The communication service to inject.
	 */
	@Inject
	public void setCommunicationService(
			CommunicationService communicationService) {
		logger.debug("communicationService set");
		this.communicationService = communicationService;

	}
}

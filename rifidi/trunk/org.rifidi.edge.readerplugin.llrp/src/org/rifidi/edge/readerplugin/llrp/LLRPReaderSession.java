/*
 *  LLRPReaderSession.java
 *
 *  Created:	Mar 9, 2009
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.readerplugin.llrp;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeoutException;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.llrp.ltk.generated.enumerations.AccessReportTriggerType;
import org.llrp.ltk.generated.enumerations.NotificationEventType;
import org.llrp.ltk.generated.enumerations.ROReportTriggerType;
import org.llrp.ltk.generated.enumerations.StatusCode;
import org.llrp.ltk.generated.messages.RO_ACCESS_REPORT;
import org.llrp.ltk.generated.messages.SET_READER_CONFIG;
import org.llrp.ltk.generated.messages.SET_READER_CONFIG_RESPONSE;
import org.llrp.ltk.generated.parameters.AccessReportSpec;
import org.llrp.ltk.generated.parameters.C1G2EPCMemorySelector;
import org.llrp.ltk.generated.parameters.EPC_96;
import org.llrp.ltk.generated.parameters.EventNotificationState;
import org.llrp.ltk.generated.parameters.ROReportSpec;
import org.llrp.ltk.generated.parameters.ReaderEventNotificationSpec;
import org.llrp.ltk.generated.parameters.TagReportContentSelector;
import org.llrp.ltk.generated.parameters.TagReportData;
import org.llrp.ltk.net.LLRPConnection;
import org.llrp.ltk.net.LLRPConnectionAttemptFailedException;
import org.llrp.ltk.net.LLRPConnector;
import org.llrp.ltk.net.LLRPEndpoint;
import org.llrp.ltk.types.Bit;
import org.llrp.ltk.types.LLRPMessage;
import org.llrp.ltk.types.UnsignedInteger;
import org.llrp.ltk.types.UnsignedShort;
import org.rifidi.edge.core.messages.EPCGeneration2Event;
import org.rifidi.edge.core.readers.impl.AbstractReaderSession;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

/**
 * 
 * 
 * @author Matthew Dean
 */
public class LLRPReaderSession extends AbstractReaderSession implements
		LLRPEndpoint {

	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(LLRPReaderSession.class);

	private LLRPConnection connection = null;

	int messageID = 1;
	int maxConAttempts = -1;
	int reconnectionInterval = -1;

	/**
	 * 
	 */
	public LLRPReaderSession(String id, String host, int reconnectionInterval,
			int maxConAttempts, Destination destination, JmsTemplate template) {
		super(id, destination, template);
		System.out.println(host);
		this.connection = new LLRPConnector(this, host);
		this.maxConAttempts = maxConAttempts;
		this.reconnectionInterval = reconnectionInterval;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readers.ReaderSession#connect()
	 */
	@Override
	public void connect() throws IOException {
		logger.debug("Connecting");
		boolean connected = false;
		for (int connCount = 0; connCount < maxConAttempts && !connected; connCount++) {
			try {
				System.out.println("JUST BEFORE THE CONNECTION ATTEMPT");
				((LLRPConnector) connection).connect();
				connected = true;
				System.out.println("JUST AFTER THE CONNECTION ATTEMPT");
			} catch (LLRPConnectionAttemptFailedException e) {
				logger.debug("Unable to connect to LLRP");
			}

			if (!connected) {
				try {
					Thread.sleep(reconnectionInterval);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					return;
				}
			}
		}

		if (!connected) {
			return;
		}
		
		executor = new ScheduledThreadPoolExecutor(1);

		try {
			SET_READER_CONFIG config = createSetReaderConfig();
			config.setMessageID(new UnsignedInteger(messageID++));

			SET_READER_CONFIG_RESPONSE config_response = (SET_READER_CONFIG_RESPONSE) connection
					.transact(config);

			StatusCode sc = config_response.getLLRPStatus().getStatusCode();
			if (sc.intValue() != StatusCode.M_Success) {
				logger
						.debug("SET_READER_CONFIG_RESPONSE returned with status code "
								+ sc.intValue());
			}

			if (!processing.compareAndSet(false, true)) {
				logger.warn("Executor was already active! ");
			}
		} catch (TimeoutException e) {
			e.printStackTrace();
			disconnect();
		} catch (ClassCastException ex) {
			logger.error(ex.getMessage());
			disconnect();
		}

	}

	/**
	 * 
	 */
	@Override
	public void disconnect() {
		if (processing.get()) {
			if (processing.compareAndSet(true, false)) {
				logger.debug("Disconnecting");
				((LLRPConnector) connection).disconnect();
			}
		}
		executor.shutdownNow();
		executor=null;
	}

	/**
	 * 
	 */
	public LLRPMessage transact(LLRPMessage message) {
		System.out.println("Sending an LLRP message: " + message.getName());
		LLRPMessage retVal = null;
		try {
			retVal = this.connection.transact(message);
		} catch (TimeoutException e) {
			e.printStackTrace();
		}

		return retVal;
	}

	/**
	 * 
	 * @param message
	 */
	public void send(LLRPMessage message) {
		connection.send(message);
	}

	/**
	 * 
	 */
	@Override
	public void errorOccured(String arg0) {

	}

	/**
	 * 
	 */
	@Override
	public void messageReceived(LLRPMessage arg0) {
		logger.debug("Asynchronous message recieved");
		if (arg0 instanceof RO_ACCESS_REPORT) {
			RO_ACCESS_REPORT rar = (RO_ACCESS_REPORT) arg0;
			List<TagReportData> trdl = rar.getTagReportDataList();

			List<String> tagdatastring = new ArrayList<String>();

			for (TagReportData t : trdl) {
				// AntennaID antid = t.getAntennaID();
				EPC_96 id = (EPC_96) t.getEPCParameter();
				tagdatastring.add(id.getEPC().toString(16));
			}

			for (BigInteger m : parseString(tagdatastring)) {
				this.getTemplate().send(this.getDestination(),
						new ObjectMessageCreator(m));
			}
		}
	}

	/**
	 * Parse the given string for results.
	 * 
	 * @param input
	 * @return
	 */
	private List<BigInteger> parseString(List<String> input) {

		List<BigInteger> retVal = new ArrayList<BigInteger>();

		try {
			for (String s : input) {
				s = s.trim();

				retVal.add(new BigInteger(s, 16));
			}
		} catch (Exception e) {
			logger.warn("There was a problem when parsing LLRP Tags.  "
					+ "tag has not been added", e);
		}
		return retVal;
	}

	/**
	 * This method creates a SET_READER_CONFIG method.
	 * 
	 * @return The SET_READER_CONFIG object.
	 */
	public SET_READER_CONFIG createSetReaderConfig() {
		SET_READER_CONFIG setReaderConfig = new SET_READER_CONFIG();

		// Create a default RoReportSpec so that reports are sent at the end of
		// ROSpecs
		ROReportSpec roReportSpec = new ROReportSpec();
		roReportSpec.setN(new UnsignedShort(0));
		roReportSpec.setROReportTrigger(new ROReportTriggerType(
				ROReportTriggerType.None));
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
				AccessReportTriggerType.Whenever_ROReport_Is_Generated));
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

	private class ObjectMessageCreator implements MessageCreator {

		/** The message that should be part of the object. */
		private BigInteger message;
		private ActiveMQObjectMessage objectMessage;

		/**
		 * Constructor.
		 * 
		 * @param message
		 */
		public ObjectMessageCreator(BigInteger message) {
			super();
			this.message = message;
			objectMessage = new ActiveMQObjectMessage();
			EPCGeneration2Event gen2event = new EPCGeneration2Event();
			gen2event.setEPCMemory(message);
			// make some wild guesses on the length of the epc field
			if (message.bitLength() > 96) {
				// 192 bits
				gen2event.setEpcLength(192);
			} else if (message.bitLength() > 64) {
				// 96 bits
				gen2event.setEpcLength(96);
			} else {
				// 64 bits
				gen2event.setEpcLength(64);
			}
			try {
				objectMessage.setObject(gen2event);
			} catch (JMSException e) {
				logger.warn("Unable to set tag event: " + e);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.springframework.jms.core.MessageCreator#createMessage(javax.jms
		 * .Session)
		 */
		@Override
		public Message createMessage(Session arg0) throws JMSException {
			return objectMessage;
		}
	}

}

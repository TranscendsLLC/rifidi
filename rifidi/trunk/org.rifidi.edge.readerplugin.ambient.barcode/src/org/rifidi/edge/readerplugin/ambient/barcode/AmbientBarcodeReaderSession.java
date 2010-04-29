/*
 *  AmbientBarcodeReaderSession.java
 *
 *  Created:	Apr 22, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.ambient.barcode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.core.sensors.SensorSession;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.sensors.sessions.AbstractServerSocketSensorSession;
import org.rifidi.edge.core.sensors.sessions.MessageParsingStrategy;
import org.rifidi.edge.core.sensors.sessions.MessageParsingStrategyFactory;
import org.rifidi.edge.core.sensors.sessions.MessageProcessingStrategy;
import org.rifidi.edge.core.sensors.sessions.MessageProcessingStrategyFactory;
import org.rifidi.edge.core.services.notification.NotifierService;
import org.rifidi.edge.core.services.notification.data.ReadCycle;
import org.rifidi.edge.core.services.notification.data.ReadCycleMessageCreator;
import org.rifidi.edge.readerplugin.ambient.barcode.tag.AmbientBarcodeTagHandler;
import org.springframework.jms.core.JmsTemplate;

/**
 * The session for the Ambient Barcode reader.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class AmbientBarcodeReaderSession extends
		AbstractServerSocketSensorSession {

	/** Service used to send out notifications */
	private volatile NotifierService notifierService;

	/** The ID for the reader. */
	private String readerID = null;

	/** The JMS Template */
	private JmsTemplate template = null;

	/** A class for handling incoming tags.   */
	private AmbientBarcodeTagHandler tagHandler = null;

	/**
	 * 
	 * 
	 * @param sensor
	 * @param ID
	 * @param destination
	 * @param template
	 * @param commandConfigurations
	 */
	public AmbientBarcodeReaderSession(AbstractSensor<?> sensor, String id,
			int port, JmsTemplate template, NotifierService notifierService,
			String readerID,
			Set<AbstractCommandConfiguration<?>> commandConfigurations) {
		super(sensor, id, template.getDefaultDestination(), template, port, 10,
				commandConfigurations);
		this.readerID = readerID;
		this.template = template;
		this.notifierService = notifierService;
		this.tagHandler = new AmbientBarcodeTagHandler(readerID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readers.impl.AbstractReaderSession#setStatus(org
	 * .rifidi.edge.core.api.SessionStatus)
	 */
	@Override
	protected void setStatus(SessionStatus status) {
		super.setStatus(status);
		// TODO: Remove this once we have aspectJ
		notifierService.sessionStatusChanged(this.readerID, this.getID(),
				status);
	}

	/**
	 * Returns the JMSTemplate. 
	 */
	public JmsTemplate getTemplate() {
		return template;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.sensors.sessions.AbstractIPSensorSession#
	 * getMessageParsingStrategyFactory()
	 */
	@Override
	protected MessageParsingStrategyFactory getMessageParsingStrategyFactory() {
		return new AmbientBarcodeMessageParsingStrategyFactory();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.sensors.sessions.AbstractIPSensorSession#
	 * getMessageProcessingStrategyFactory()
	 */
	@Override
	protected MessageProcessingStrategyFactory getMessageProcessingStrategyFactory() {
		return new AmbientBarcodeMessageProcessingStrategyFactory(this);
	}

	/**
	 * Process and send the tag.
	 */
	public void sendTag(byte[] tag) {
		ReadCycle cycle = this.tagHandler.processTag(tag);

		this.getSensor().send(cycle);

		this.template.send(this.template.getDefaultDestination(),
				new ReadCycleMessageCreator(cycle));
	}

	/**
	 * Factory class for creating AmbientBarcodeMessageParsingStrategies.
	 * 
	 * @author Matthew Dean - matt@pramari.com
	 */
	private class AmbientBarcodeMessageParsingStrategyFactory implements
			MessageParsingStrategyFactory {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.rifidi.edge.core.sensors.sessions.MessageParsingStrategyFactory
		 * #createMessageParser()
		 */
		@Override
		public MessageParsingStrategy createMessageParser() {
			return new AmbientBarcodeMessageParsingStrategy();
		}
	}

	/**
	 * Message processing object for the Ambient barcode reader. This class
	 * takes byte arrays representing a physical barcode and converts them into
	 * a tag object that can be used by Rifidi Edge.
	 * 
	 * @author Matthew Dean - matt@pramari.com
	 */
	private class AmbientBarcodeMessageProcessingStrategy implements
			MessageProcessingStrategy {

		/**
		 * The session object.  
		 */
		private AmbientBarcodeReaderSession session = null;

		/**
		 * Processing strategy for the Ambient Barcode.  
		 * 
		 * @param session
		 * @param template
		 */
		public AmbientBarcodeMessageProcessingStrategy(SensorSession session) {
			this.session = (AmbientBarcodeReaderSession) session;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @seeorg.rifidi.edge.core.sensors.sessions.MessageProcessingStrategy#
		 * processMessage(byte[])
		 */
		@Override
		public void processMessage(byte[] message) {
			this.session.sendTag(message);
		}
	}

	/**
	 * Factory to generate MessageProcessingStrategy classes.
	 * 
	 * @author Matthew Dean - matt@pramari.com
	 */
	private class AmbientBarcodeMessageProcessingStrategyFactory implements
			MessageProcessingStrategyFactory {

		/** The session */
		private AmbientBarcodeReaderSession session;

		/**
		 * Factory class for the ProcessingStrategy.  
		 */
		public AmbientBarcodeMessageProcessingStrategyFactory(
				AmbientBarcodeReaderSession session) {
			this.session = session;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.rifidi.edge.core.sensors.sessions.MessageProcessingStrategyFactory
		 * #createMessageProcessor()
		 */
		@Override
		public MessageProcessingStrategy createMessageProcessor() {
			return new AmbientBarcodeMessageProcessingStrategy(session);
		}

	}

	/**
	 * All messages coming back from the barcode reader are 10 characters long.
	 * So any
	 * 
	 * @author Matthew Dean - matt@pramari.com
	 */
	private class AmbientBarcodeMessageParsingStrategy implements
			MessageParsingStrategy {

		/** The message currently being processed. */
		private List<Byte> messagebuilder = new ArrayList<Byte>();
		/** Once we hit this size, we have a completed message */
		private static final int MSGSIZE = 10;

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.rifidi.edge.core.sensors.sessions.MessageParsingStrategy#isMessage
		 * (byte)
		 */
		@Override
		public byte[] isMessage(byte message) {
			messagebuilder.add(message);
			if (messagebuilder.size() == MSGSIZE) {
				byte retval[] = new byte[MSGSIZE];
				for (int i = 0; i < MSGSIZE; i++) {
					retval[i] = messagebuilder.get(i);
				}
				messagebuilder.clear();
				return retval;
			}
			return null;
		}
	}

}

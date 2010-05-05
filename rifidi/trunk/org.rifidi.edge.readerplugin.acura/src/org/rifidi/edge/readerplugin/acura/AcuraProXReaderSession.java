/*
 *  AcuraProXReaderSession.java
 *
 *  Created:	Dec 3, 2009
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.acura;

import gnu.io.SerialPort;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.sensors.messages.ByteMessage;
import org.rifidi.edge.core.sensors.sessions.AbstractSerialSensorSession;
import org.rifidi.edge.core.sensors.sessions.MessageParsingStrategyFactory;
import org.rifidi.edge.core.services.notification.NotifierService;
import org.springframework.jms.core.JmsTemplate;

/**
 * The Session for the AcuraProXReader.
 * 
 * @author Matthew Dean
 */
public class AcuraProXReaderSession extends AbstractSerialSensorSession {

	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(AcuraProXReaderSession.class);

	/** Service used to send out notifications */
	private volatile NotifierService notifierService;

	private String readerID = null;

	private JmsTemplate template = null;

	private AcuraProXTagHandler handler = null;

	/**
	 * 
	 * 
	 * @param sensor
	 *            The sensor type.
	 * @param id
	 *            The ID of the reader
	 * @param comm
	 *            The Serial Port the reader will connect with.
	 * @param destination
	 * @param template
	 * @param commandConfigurations
	 */
	public AcuraProXReaderSession(AbstractSensor<?> sensor, String id,
			String comm, JmsTemplate template, NotifierService notifierService,
			String readerID,
			Set<AbstractCommandConfiguration<?>> commandConfigurations) {
		super(sensor, id, template.getDefaultDestination(), template,
				commandConfigurations, comm, 9600, SerialPort.DATABITS_8,
				SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		this.readerID = readerID;
		this.template = template;
		this.notifierService = notifierService;
		this.handler = new AcuraProXTagHandler(this, readerID);
	}

	/**
	 * 
	 */
	public JmsTemplate getTemplate() {
		return template;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.sensors.sessions.AbstractSerialSensorSession#
	 * getMessageParsingStrategyFactory()
	 */
	@Override
	protected MessageParsingStrategyFactory getMessageParsingStrategyFactory() {
		return new AcuraMessageParsingStrategyFactory();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.sensors.sessions.AbstractSerialSensorSession#
	 * messageReceived(org.rifidi.edge.core.sensors.messages.ByteMessage)
	 */
	@Override
	public void messageReceived(ByteMessage message) {
		logger.debug("Sending a tag");
		this.handler.processTag(message.message);
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

}

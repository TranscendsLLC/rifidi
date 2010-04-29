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

import java.util.Set;

import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.sensors.sessions.AbstractServerSocketSensorSession;
import org.rifidi.edge.core.sensors.sessions.MessageParsingStrategyFactory;
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

	private String readerID = null;

	private JmsTemplate template = null;

	private AmbientBarcodeTagHandler tagHandler = null;

	/**
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
	 * 
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
}

/**
 * 
 */
package org.rifidi.edge.readerplugin.alien.autonomous;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.base.AbstractServerSocketSensorSession;
import org.rifidi.edge.core.sensors.base.threads.MessageParsingStrategyFactory;
import org.rifidi.edge.core.sensors.base.threads.MessageProcessingStrategyFactory;
import org.rifidi.edge.core.sensors.commands.Command;
import org.rifidi.edge.core.services.notification.NotifierService;
import org.rifidi.edge.readerplugin.alien.AlienMessageParsingStrategyFactory;
import org.springframework.jms.core.JmsTemplate;

/**
 * The Session that Alien Readers can send reports to.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AlienAutonomousSensorSession extends
		AbstractServerSocketSensorSession {

	/** The logger */
	private final static Log logger = LogFactory
			.getLog(AlienAutonomousSensorSession.class);
	/** The notifierService used to send out notifications of session changes */
	private NotifierService notifierService;
	/** The factory that produces Alien Message Parsing Strategy */
	private AlienMessageParsingStrategyFactory messageParserFactory;
	/** The factory that produces Alien Autonomous Message Processing Strategies */
	private AlienAutonomousMessageProcessingStrategyFactory messageProcessingFactory;

	/**
	 * 
	 * @param sensor
	 * @param ID
	 * @param template
	 * @param notifierService
	 * @param serverSocketPort
	 * @param maxNumAutonomousReaders
	 */
	public AlienAutonomousSensorSession(AbstractSensor<?> sensor, String ID,
			JmsTemplate template, NotifierService notifierService,
			int serverSocketPort, int maxNumAutonomousReaders) {
		super(sensor, ID, template.getDefaultDestination(), template,
				serverSocketPort, maxNumAutonomousReaders);
		this.notifierService = notifierService;
		this.messageParserFactory = new AlienMessageParsingStrategyFactory();
		this.messageProcessingFactory = new AlienAutonomousMessageProcessingStrategyFactory(
				this, template);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.sensors.base.AbstractServerSocketSensorSession#
	 * getMessageParsingStrategyFactory()
	 */
	@Override
	protected MessageParsingStrategyFactory getMessageParsingStrategyFactory() {
		return this.messageParserFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.sensors.base.AbstractServerSocketSensorSession#
	 * getMessageProcessingStrategyFactory()
	 */
	@Override
	protected MessageProcessingStrategyFactory getMessageProcessingStrategyFactory() {
		return this.messageProcessingFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.AbstractSensorSession#setStatus(org
	 * .rifidi.edge.api.SessionStatus)
	 */
	@Override
	protected synchronized void setStatus(SessionStatus status) {
		super.setStatus(status);
		notifierService.sessionStatusChanged(super.getSensor().getID(),
				getID(), status);
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.sensors.base.AbstractSensorSession#getCommandInstance(java.lang.String)
	 */
	@Override
	protected Command getCommandInstance(String commandID) {
		// TODO Auto-generated method stub
		return null;
	}
	
}

/*
 * 
 * AlienAutonomousSensorSession.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
/**
 * 
 */
package org.rifidi.edge.adapter.alien.autonomous;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.adapter.alien.AlienMessageParsingStrategyFactory;
import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.sensors.sessions.AbstractServerSocketSensorSession;
import org.rifidi.edge.core.sensors.sessions.MessageParsingStrategyFactory;
import org.rifidi.edge.core.sensors.sessions.MessageProcessingStrategyFactory;
import org.rifidi.edge.core.services.notification.NotifierService;

/**
 * The Session that Alien Readers can send reports to.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AlienAutonomousSensorSession extends
		AbstractServerSocketSensorSession {

	/** The logger */
	@SuppressWarnings("unused")
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
	 * @param notifierService
	 * @param serverSocketPort
	 * @param maxNumAutonomousReaders
	 * @param commands
	 */
	public AlienAutonomousSensorSession(AbstractSensor<?> sensor, String ID,
			NotifierService notifierService, int serverSocketPort,
			int maxNumAutonomousReaders,
			Set<AbstractCommandConfiguration<?>> commands) {
		super(sensor, ID, serverSocketPort, maxNumAutonomousReaders, commands);
		this.notifierService = notifierService;
		this.messageParserFactory = new AlienMessageParsingStrategyFactory();
		this.messageProcessingFactory = new AlienAutonomousMessageProcessingStrategyFactory(
				this);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.sessions.AbstractServerSocketSensorSession
	 * #toString()
	 */
	@Override
	public String toString() {
		return "[Autonomous Session " + super.toString() + "]";
	}

}

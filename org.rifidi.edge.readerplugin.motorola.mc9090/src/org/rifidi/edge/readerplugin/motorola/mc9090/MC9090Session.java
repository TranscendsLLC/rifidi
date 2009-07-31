/**
 * 
 */
package org.rifidi.edge.readerplugin.motorola.mc9090;

import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.base.AbstractServerSocketSensorSession;
import org.rifidi.edge.core.sensors.base.threads.MessageParsingStrategy;
import org.rifidi.edge.core.sensors.base.threads.MessageParsingStrategyFactory;
import org.rifidi.edge.core.sensors.base.threads.MessageProcessingStrategy;
import org.rifidi.edge.core.sensors.base.threads.MessageProcessingStrategyFactory;
import org.rifidi.edge.core.services.notification.NotifierService;
import org.springframework.jms.core.JmsTemplate;

/**
 * A session for a MC9090 sensor. It opens up a server socket and listens for
 * new connection attempts. The MC9090 reader must have the Rifidi software
 * installed on it.
 * 
 * This session cannot control the MC9090 reader and can only passively listen
 * for tag messages
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class MC9090Session extends AbstractServerSocketSensorSession {

	/**
	 * The factory that produces MessageParsingStrategies for the MC9090 reader
	 * apdatper
	 */
	private MC9090MessageParsingStategyFactory messageParserFactory;
	/** Factory for producing new Message Processing Strategies */
	private MC9090MessageProcessingStrategyFactory messageProcessorFactory;
	/** notifier servier used to send notifications to clients */
	private NotifierService notifierService;
	/** internal JMS message bus */
	private JmsTemplate template;

	/**
	 * Constructor
	 * 
	 * @param sensor
	 *            The sensor that created this session
	 * @param ID
	 *            The ID of this session
	 * @param template
	 *            The internal JMS Message Bus
	 * @param serverSocketPort
	 *            The port to listen for messages on
	 * @param maxNumSensors
	 *            The maximum number of sessions allowed to connect at once
	 */
	public MC9090Session(AbstractSensor<?> sensor, String ID,
			JmsTemplate template, NotifierService notifierService,
			int serverSocketPort, int maxNumSensors) {
		super(sensor, ID, template.getDefaultDestination(), template,
				serverSocketPort, maxNumSensors);
		this.messageParserFactory = new MC9090MessageParsingStategyFactory();
		this.messageProcessorFactory = new MC9090MessageProcessingStrategyFactory(
				this);
		this.notifierService = notifierService;
		this.template = template;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.sensors.base.AbstractServerSocketSensorSession#
	 * getMessageParsingStrategyFactory()
	 */
	@Override
	protected MessageParsingStrategyFactory getMessageParsingStrategyFactory() {
		return messageParserFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.sensors.base.AbstractServerSocketSensorSession#
	 * getMessageProcessingStrategyFactory()
	 */
	@Override
	protected MessageProcessingStrategyFactory getMessageProcessingStrategyFactory() {
		return messageProcessorFactory;
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
		notifierService.sessionStatusChanged(this.sensor.getID(), this.getID(),
				status);
	}

	/**
	 * 
	 * The factory to produce new MessageParsingStrategies
	 * 
	 * @author Kyle Neumeier - kyle@pramari.com
	 * 
	 */
	private class MC9090MessageParsingStategyFactory implements
			MessageParsingStrategyFactory {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.rifidi.edge.core.sensors.base.threads.MessageParsingStrategyFactory
		 * #createMessageParser()
		 */
		@Override
		public MessageParsingStrategy createMessageParser() {
			return new MC9090MessageParsingStrategy();
		}

	}

	/**
	 * The factory to produce new MessageProcessingStrategies
	 * 
	 * @author Kyle Neumeier - kyle@pramari.com
	 * 
	 */
	private class MC9090MessageProcessingStrategyFactory implements
			MessageProcessingStrategyFactory {

		MC9090Session session;

		public MC9090MessageProcessingStrategyFactory(MC9090Session session) {
			this.session = session;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @seeorg.rifidi.edge.core.sensors.base.threads.
		 * MessageProcessingStrategyFactory#createMessageProcessor()
		 */
		@Override
		public MessageProcessingStrategy createMessageProcessor() {
			return new MC9090MessageProcessingStrategy(template, session);
		}

	}

}

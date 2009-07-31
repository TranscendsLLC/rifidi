/**
 * 
 */
package org.rifidi.edge.readerplugin.alien.autonomous;

import org.rifidi.edge.core.sensors.base.threads.MessageProcessingStrategy;
import org.rifidi.edge.core.sensors.base.threads.MessageProcessingStrategyFactory;
import org.springframework.jms.core.JmsTemplate;

/**
 * A factory for producing new AlienAutonomousMessageProcessingStrategy
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AlienAutonomousMessageProcessingStrategyFactory implements
		MessageProcessingStrategyFactory {

	/** The session */
	private AlienAutonomousSensorSession session;
	/** Template used to send notifications */
	private JmsTemplate template;

	/**
	 * Constructor
	 * 
	 * @param session
	 * @param template
	 */
	public AlienAutonomousMessageProcessingStrategyFactory(
			AlienAutonomousSensorSession session, JmsTemplate template) {
		this.session = session;
		this.template = template;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.threads.MessageProcessingStrategyFactory
	 * #createMessageProcessor()
	 */
	@Override
	public MessageProcessingStrategy createMessageProcessor() {
		return new AlienAutonomousMessageProcessingStrategy(session, template);
	}

}

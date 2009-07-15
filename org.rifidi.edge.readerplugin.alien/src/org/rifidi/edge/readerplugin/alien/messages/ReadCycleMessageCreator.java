/**
 * 
 */
package org.rifidi.edge.readerplugin.alien.messages;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.services.notification.data.ReadCycle;
import org.springframework.jms.core.MessageCreator;

/**
 * This is a class that creates Messages able to be used by a JMSTemplate from
 * ReadCycles
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ReadCycleMessageCreator implements MessageCreator {

	/** The message to be sent */
	private ActiveMQObjectMessage objectMessage;
	/** The logger for this class */
	private static final Log logger = LogFactory
			.getLog(ReadCycleMessageCreator.class);

	/**
	 * Constructor
	 * 
	 * @param readCycle
	 *            The message to be sent
	 */
	public ReadCycleMessageCreator(ReadCycle readCycle) {
		super();
		objectMessage = new ActiveMQObjectMessage();

		try {
			objectMessage.setObject(readCycle);
		} catch (JMSException e) {
			logger.warn("Unable to set tag event: " + e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.jms.core.MessageCreator#createMessage(javax.jms.Session
	 * )
	 */
	@Override
	public Message createMessage(Session arg0) throws JMSException {
		return objectMessage;
	}

}

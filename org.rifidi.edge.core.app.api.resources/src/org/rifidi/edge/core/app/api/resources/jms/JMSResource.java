package org.rifidi.edge.core.app.api.resources.jms;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class JMSResource {
	/** JMS Destination to send messages to */
	private volatile Destination destination;
	/** JMS Template to use to send messages */
	private volatile JmsTemplate template;

	/**
	 * Called by spring
	 * 
	 * @param destination
	 *            the destination to set
	 */
	public void setDestination(Destination destination) {
		this.destination = destination;
	}

	/**
	 * Called by spring
	 * 
	 * @param template
	 *            the template to set
	 */
	public void setTemplate(JmsTemplate template) {
		this.template = template;
	}
	
	/**
	 * @return the destination
	 */
	public Destination getDestination() {
		return destination;
	}

	/**
	 * @return the template
	 */
	public JmsTemplate getTemplate() {
		return template;
	}

	/**
	 * Subclasses can use this method to send a message out over JMS.
	 * 
	 * @param message
	 *            The message to send
	 * @throws JmsException
	 *             If there was a problem when sending the message.
	 */
	public void sendTextMessage(final String message) throws JmsException {
		template.send(destination, new MessageCreator() {

			@Override
			public Message createMessage(Session arg0) throws JMSException {
				return arg0.createTextMessage(message);
			}
		});
	}
	
	


}

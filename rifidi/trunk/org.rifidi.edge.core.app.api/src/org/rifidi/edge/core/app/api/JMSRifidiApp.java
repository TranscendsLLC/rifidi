/**
 * 
 */
package org.rifidi.edge.core.app.api;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

/**
 * Rifidi applications which need JMS functionality can extend this class.
 * 
 * Implementors must inject a Spring JmsTemplate and a Destination.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class JMSRifidiApp extends AbstractRifidiApp {

	/** JMS Destination to send messages to */
	private volatile Destination destination;
	/** JMS Template to use to send messages */
	private volatile JmsTemplate template;

	/**
	 * Constructor for a AbstractRifidiApp
	 * 
	 * @param group
	 *            the group this application is a part of
	 * @param name
	 *            The name of the application
	 */
	public JMSRifidiApp(String group,String name) {
		super(group,name);
	}

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
	 * Subclasses can use this method to send a message out over JMS.
	 * 
	 * @param message
	 *            The message to send
	 * @throws JmsException
	 *             If there was a problem when sending the message.
	 */
	protected void sendTextMessage(final String message) throws JmsException {
		template.send(destination, new MessageCreator() {

			@Override
			public Message createMessage(Session arg0) throws JMSException {
				return arg0.createTextMessage(message);
			}
		});
	}

}

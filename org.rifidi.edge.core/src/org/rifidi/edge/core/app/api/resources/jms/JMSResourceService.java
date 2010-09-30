/**
 * 
 */
package org.rifidi.edge.core.app.api.resources.jms;

import javax.jms.Destination;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.rifidi.edge.core.app.api.resources.CannotCreateResourceException;
import org.rifidi.edge.core.app.api.resources.ResourceService;
import org.springframework.jms.core.JmsTemplate;

/**
 * A service that creates JMS Resources.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class JMSResourceService extends
		ResourceService<JMSResourceDescription, JMSResource> {

	/** The template to use */
	private volatile JmsTemplate jmsTemplate;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.app.api.resources.ResourceService#createResource
	 * (org.rifidi.edge.core.app.api.resources.ResourceDescription)
	 */
	@Override
	protected JMSResource createResource(
			JMSResourceDescription resourceDescription)
			throws CannotCreateResourceException {
		JMSResource sender = new JMSResource();
		sender.setTemplate(jmsTemplate);
		sender.setDestination(createDestination(resourceDescription));
		return sender;
	}

	/**
	 * A private method to help create an ActiveMQ destination
	 * 
	 * @param description
	 * @return
	 */
	private Destination createDestination(JMSResourceDescription description)
			throws CannotCreateResourceException {
		if (description.getDesinationType().equalsIgnoreCase("topic")) {
			return new ActiveMQTopic(description.getDestinationName());
		}

		if (description.getDesinationType().equalsIgnoreCase("queue")) {
			return new ActiveMQQueue(description.getDestinationName());
		}

		throw new CannotCreateResourceException("Invalid Destination Type: "
				+ description.getDesinationType()
				+ ". Must be 'topic' or 'queue'");
	}

	/**
	 * @param jmsTemplate
	 *            the jmsTemplate to set
	 */
	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

}

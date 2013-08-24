/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.api.resources;

import javax.jms.Destination;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
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
	 * org.rifidi.edge.api.resources.ResourceService#createResource
	 * (org.rifidi.edge.api.resources.ResourceDescription)
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

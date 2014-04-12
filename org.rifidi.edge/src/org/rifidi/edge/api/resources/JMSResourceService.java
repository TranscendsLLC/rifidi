/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
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

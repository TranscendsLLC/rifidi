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

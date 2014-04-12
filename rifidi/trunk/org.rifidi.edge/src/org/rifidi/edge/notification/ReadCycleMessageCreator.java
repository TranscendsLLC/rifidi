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
/**
 * 
 */
package org.rifidi.edge.notification;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

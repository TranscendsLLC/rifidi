/*
 * 
 * NotificationMessageCreator.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */

package org.rifidi.edge.core.services.notification;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.jms.core.MessageCreator;

/**
 * This class creates notification messages for JMS.  
 * 
 * @author kyle
 */
public class NotificationMessageCreator implements MessageCreator {

	private Serializable notification;

	/**
	 * Constructor.   
	 * 
	 * @param message
	 */
	public NotificationMessageCreator(Serializable notification) {
		this.notification = notification;
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
		BytesMessage message = arg0.createBytesMessage();
		ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
		try {
			ObjectOutput out = new ObjectOutputStream(bos);
	        out.writeObject(notification);
	        out.close();
	        message.writeBytes(bos.toByteArray());
	        return message;
		} catch (IOException e) {
			throw new JMSException(e.getMessage());
		}
	}
}

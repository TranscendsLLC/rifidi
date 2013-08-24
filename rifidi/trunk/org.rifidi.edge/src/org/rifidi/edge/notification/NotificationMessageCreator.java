/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.notification;

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

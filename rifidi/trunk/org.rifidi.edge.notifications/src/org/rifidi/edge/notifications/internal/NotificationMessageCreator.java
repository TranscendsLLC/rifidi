/**
 * 
 */
package org.rifidi.edge.notifications.internal;

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
 * @author kyle
 * 
 */
public class NotificationMessageCreator implements MessageCreator {

	private Serializable notification;

	/**
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

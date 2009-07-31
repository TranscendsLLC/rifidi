/**
 * 
 */
package org.rifidi.edge.demo1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.rifidi.edge.core.services.notification.data.ReadCycle;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

/**
 * @author kyle
 * 
 */
public class JMSSender {

	private JmsTemplate template;
	private Destination destination;

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
	 * Called by spring
	 * 
	 * @param destination
	 *            the destination to set
	 */
	public void setDestination(Destination destination) {
		this.destination = destination;
	}

	public void newReadCycle(ReadCycle readCycle) {
		template.send(destination, new AcmeNotificationMessageCreator(
				readCycle));
		
	}

	private class AcmeNotificationMessageCreator implements MessageCreator {

		private Serializable notification;

		/**
		 * @param notification
		 */
		public AcmeNotificationMessageCreator(Serializable notification) {
			this.notification = notification;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.springframework.jms.core.MessageCreator#createMessage(javax.jms
		 * .Session)
		 */
		@Override
		public Message createMessage(Session arg0) throws JMSException {
			BytesMessage message = arg0.createBytesMessage();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				ObjectOutput out = new ObjectOutputStream(bos);
				out.writeObject(notification);
				out.close();
				message.writeBytes(bos.toByteArray());
				return message;
			} catch (IOException e) {
				e.printStackTrace();
				throw new JMSException(e.getMessage());
			}
		}

	}
}

/*
 * 
 * TagMessageMessageCreator.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
/**
 * 
 */
package org.rifidi.edge.notification;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.rifidi.edge.api.TagBatch;
import org.springframework.jms.core.MessageCreator;

/**
 * An object that helps create Tag Messages for placing on the external JMS
 * Destination
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class TagMessageMessageCreator implements MessageCreator {

	private TagBatch tagBatch;

	/**
	 * Constructor.
	 * 
	 * @param cycle
	 */
	public TagMessageMessageCreator(TagBatch batch) {
		this.tagBatch = batch;

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
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ObjectOutput out = new ObjectOutputStream(bos);
			out.writeObject(tagBatch);
			out.close();
			message.writeBytes(bos.toByteArray());
			return message;
		} catch (IOException e) {
			throw new JMSException(e.getMessage());
		}
	}

}

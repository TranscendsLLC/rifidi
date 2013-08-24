/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
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

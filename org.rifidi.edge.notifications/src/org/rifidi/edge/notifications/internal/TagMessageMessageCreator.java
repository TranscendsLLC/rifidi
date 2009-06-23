/**
 * 
 */
package org.rifidi.edge.notifications.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.rifidi.edge.api.tags.TagBatch;
import org.rifidi.edge.api.tags.TagDTO;
import org.rifidi.edge.core.messages.DatacontainerEvent;
import org.rifidi.edge.core.messages.EPCGeneration1Event;
import org.rifidi.edge.core.messages.ReadCycle;
import org.rifidi.edge.core.messages.TagReadEvent;
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
	public TagMessageMessageCreator(ReadCycle cycle) {
		Set<TagDTO> tags = new HashSet<TagDTO>();
		for (TagReadEvent tagRead : cycle.getTags()) {
			DatacontainerEvent event = tagRead.getTag();
			tags.add(new TagDTO(((EPCGeneration1Event) event).getEPCMemory(),
					tagRead.getAntennaID(), tagRead.getTimestamp()));
		}

		tagBatch = new TagBatch(cycle.getReaderID(), cycle.getEventTimestamp(),
				tags);

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

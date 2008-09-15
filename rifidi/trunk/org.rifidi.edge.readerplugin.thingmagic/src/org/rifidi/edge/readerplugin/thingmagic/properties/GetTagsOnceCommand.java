package org.rifidi.edge.readerplugin.thingmagic.properties;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.common.utilities.converter.ByteAndHexConvertingUtility;
import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.messageQueue.MessageQueue;
import org.rifidi.edge.core.readerplugin.messages.impl.TagMessage;
import org.rifidi.edge.core.readerplugin.property.Property;
import org.w3c.dom.Element;

/**
 * @author Jerry Maine - jerry@pramari.com
 *
 */
public class GetTagsOnceCommand implements Property {
	private static final Log logger = LogFactory
			.getLog(GetTagsOnceCommand.class);
	private static final String GET_TAGS = "select id, timestamp from tag_id;";

	public Element execute(Connection connection, MessageQueue errorQueue,
			Element propertyConfig) {
		try {
			logger.debug("Issuing Command: " + GET_TAGS);
			connection.sendMessage(GET_TAGS);
		} catch (IOException e) {
			return null;
		}

		String recieved;
		try {
			recieved = (String) connection.receiveMessage();
		} catch (IOException e1) {
			return null;
		}

		logger.debug("Returned: " + recieved);
		if (!recieved.equals("")) {
			String[] rawTags = recieved.split("\n");
			for (String rawTag : rawTags) {
				// logger.debug(rawTag);

				// All tag data sent back is separated by vertical bars.
				String[] rawTagItems = rawTag.split("\\|");

				TagMessage tag = new TagMessage();

				// logger.debug(rawTagItems[0]);

				tag.setId(ByteAndHexConvertingUtility
						.fromHexString(rawTagItems[0].substring(2,
								rawTagItems[0].length())));

				// TODO: correct the time stamps.
				tag.setLastSeenTime(System.nanoTime());
				// logger.debug(tag.toXML());
				//TODO figure this out.
//				Element returnnode = propertyConfig.getOwnerDocument().createElement(propertyConfig.getNodeName());
//				Element tagID= propertyConfig.getOwnerDocument().createElement("TAGID");
//				Text data= propertyConfig.getOwnerDocument().createTextNode(rawtag);
//				tagID.appendChild(data);
//				returnnode.appendChild(tagID);
			}

		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.property.Property#getProperty(org.rifidi.edge.core.communication.Connection, org.rifidi.edge.core.messageQueue.MessageQueue, org.w3c.dom.Element)
	 */
	@Override
	public Element getProperty(Connection connection, MessageQueue errorQueue,
			Element propertyConfig) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.property.Property#setProperty(org.rifidi.edge.core.communication.Connection, org.rifidi.edge.core.messageQueue.MessageQueue, org.w3c.dom.Element)
	 */
	@Override
	public Element setProperty(Connection connection, MessageQueue errorQueue,
			Element propertyConfig) {
		// TODO Auto-generated method stub
		return null;
	}

}

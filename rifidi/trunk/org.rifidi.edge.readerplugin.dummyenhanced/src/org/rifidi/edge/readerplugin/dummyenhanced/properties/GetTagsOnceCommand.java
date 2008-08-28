package org.rifidi.edge.readerplugin.dummyenhanced.properties;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.messageQueue.MessageQueue;
import org.rifidi.edge.core.readerplugin.property.Property;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class GetTagsOnceCommand implements Property {
	private static final Log logger = LogFactory
			.getLog(GetTagsOnceCommand.class);
	boolean running = true;

	@Override
	public Element execute(Connection connection, MessageQueue errorQueue,
			Element propertyConfig) {
		logger.debug("Getting tags.");

		String rawtag = "123456789\n";

		try {
			connection.sendMessage(rawtag);
			rawtag = (String) connection.receiveMessage();
		} catch (IOException e1) {
			return null;
		}
		Element returnnode = propertyConfig.getOwnerDocument().createElement(propertyConfig.getNodeName());
		Element tagID= propertyConfig.getOwnerDocument().createElement("TAGID");
		Text data= propertyConfig.getOwnerDocument().createTextNode(rawtag);
		tagID.appendChild(data);
		returnnode.appendChild(tagID);
	
		return returnnode;
	}
}

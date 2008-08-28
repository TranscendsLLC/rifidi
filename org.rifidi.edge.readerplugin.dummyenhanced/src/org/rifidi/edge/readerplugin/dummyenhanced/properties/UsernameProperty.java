package org.rifidi.edge.readerplugin.dummyenhanced.properties;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.messageQueue.MessageQueue;
import org.rifidi.edge.core.readerplugin.property.Property;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class UsernameProperty implements Property {

	Log logger = LogFactory.getLog(UsernameProperty.class);

	@Override
	public Element execute(Connection connection, MessageQueue errorQueue,
			Element propertyConfig) {

		String type = propertyConfig.getAttribute("Type");
		String response = null;

		try {
			if (type.equals("get")) {
				connection.sendMessage("get username");
				response = (String) connection.receiveMessage();
				response = "username = " + "CURRENT USERNAME";
			} else {
				NodeList nl = propertyConfig.getChildNodes();
				for (int i = 0; i < nl.getLength(); i++) {
					if (nl.item(i).getNodeType() == Node.TEXT_NODE) {
						Text newName = (Text) nl.item(i);
						connection.sendMessage("set username = "
								+ newName.getData());
						response = (String) connection.receiveMessage();
						response = "username = " + newName;
						break;
					}
				}
			}
		} catch (IOException ex) {
			logger.debug("IOException");
		}

		if (response == null) {
			response = "ERROR";
		}

		Element returnnode = propertyConfig.getOwnerDocument().createElement(
				propertyConfig.getNodeName());
		Text data = propertyConfig.getOwnerDocument().createTextNode(response);
		returnnode.appendChild(data);
		return returnnode;
	}

}

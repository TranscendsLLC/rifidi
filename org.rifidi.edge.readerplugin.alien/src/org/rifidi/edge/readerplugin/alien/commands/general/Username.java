package org.rifidi.edge.readerplugin.alien.commands.general;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.messageQueue.MessageQueue;
import org.rifidi.edge.core.readerplugin.commands.StringMetadata;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;


/**
 * @author Jerry Maine - jerry@pramari.com
 *
 */
@StringMetadata(defaultValue = "alien", displayName = "User name", editable = true, name = "username", regex = "")
public class Username implements org.rifidi.edge.core.readerplugin.property.Property {
	private static final Log logger = LogFactory
	.getLog(Username.class);

	@Override
	public Element execute(Connection connection, MessageQueue errorQueue,
			Element propertyConfig) {
		logger.debug("Executing: " + this.getClass());
		String type = propertyConfig.getAttribute("type");
		String response = null;
		

		try {
			if (type.equals("get")) {
				connection.sendMessage("get username\n");
				response = (String) connection.receiveMessage();
				if (response.contains("=")) {
					String[] temp = response.split("=");
					response = temp[1];
				}
				response = response.trim();
			} else if(type.equals("set")) {
				NodeList nl = propertyConfig.getChildNodes();
				for (int i = 0; i < nl.getLength(); i++) {
					if (nl.item(i).getNodeType() == Node.TEXT_NODE) {
						Text newName = (Text) nl.item(i);
						connection.sendMessage("set username = "
								+ newName.getData()+"\n");
						response = (String) connection.receiveMessage();
						if (response.contains("=")) {
							String[] temp = response.split("=");
							response = temp[1];
						}
						response = response.trim();
						break;
					}
				}
			}else{
				logger.debug("type attribute not found");
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

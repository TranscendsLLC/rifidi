package org.rifidi.edge.readerplugin.alien.properties.network;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.dynamicswtforms.xml.annotaions.Form;
import org.rifidi.dynamicswtforms.xml.annotaions.FormElement;
import org.rifidi.dynamicswtforms.xml.constants.FormElementType;
import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.messageQueue.MessageQueue;
import org.rifidi.edge.core.readerplugin.property.Property;
import org.rifidi.edge.readerplugin.alien.properties.AlienResponse;
import org.w3c.dom.Element;

/**
 * 
 * @author Matthew Dean
 */
@Form(name = DHCP.DHCP, formElements = { @FormElement(type = FormElementType.STRING, elementName = DHCP.DHCP_DATA, editable = true, defaultValue = "0", displayName = DHCP.DHCP_DISPLAY) })
public class DHCP implements Property {

	private static final String DHCP = "DHCP";

	private static final String DHCP_DATA = "DHCP Data";

	private static final String DHCP_DISPLAY = "DHCP";

	private static final Log logger = LogFactory.getLog(DHCP.class);

	private static final String command = "DHCP";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readerplugin.property.Property#getProperty(org.rifidi
	 * .edge.core.communication.Connection,
	 * org.rifidi.edge.core.messageQueue.MessageQueue, org.w3c.dom.Element)
	 */
	@Override
	public Element getProperty(Connection connection, MessageQueue errorQueue,
			Element propertyConfig) {
		AlienResponse response = new AlienResponse();
		String responseString = null;
		try {
			connection.sendMessage("\1get " + command + "\n");

			responseString = (String) connection.receiveMessage();

		} catch (IOException e) {
			logger.debug("IOException");
		}
		response.setResponseMessage(responseString);
		return response.formulateResponseXML(propertyConfig, DHCP, DHCP_DATA);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readerplugin.property.Property#setProperty(org.rifidi
	 * .edge.core.communication.Connection,
	 * org.rifidi.edge.core.messageQueue.MessageQueue, org.w3c.dom.Element)
	 */
	@Override
	public Element setProperty(Connection connection, MessageQueue errorQueue,
			Element propertyConfig) {
		return getProperty(connection, errorQueue, propertyConfig);
	}

}

package org.rifidi.edge.readerplugin.alien.properties.network;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.dynamicswtforms.xml.annotaions.Form;
import org.rifidi.dynamicswtforms.xml.annotaions.FormElement;
import org.rifidi.dynamicswtforms.xml.constants.FormElementType;
import org.rifidi.edge.core.api.communication.Connection;
import org.rifidi.edge.core.api.messageQueue.MessageQueue;
import org.rifidi.edge.core.api.readerplugin.property.Property;
import org.rifidi.edge.readerplugin.alien.properties.AlienResponse;
import org.w3c.dom.Element;

/**
 * 
 * @author Matthew Dean
 */
@Form(name = CommandPort.COMMAND_PORT, formElements = { @FormElement(type = FormElementType.INTEGER, elementName = CommandPort.COMMAND_PORT_DATA, editable = false, defaultValue = "0", displayName = CommandPort.COMMAND_PORT_DISPLAY) })
public class CommandPort implements Property {

	private static final String COMMAND_PORT = "CommandPort";

	private static final String COMMAND_PORT_DATA = "CommandPort_Data";

	private static final String COMMAND_PORT_DISPLAY = "Command Port";

	private static final Log logger = LogFactory.getLog(CommandPort.class);

	private static final String command = "CommandPort";

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
		return response.formulateResponseXML(propertyConfig, COMMAND_PORT,
				COMMAND_PORT_DATA);
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
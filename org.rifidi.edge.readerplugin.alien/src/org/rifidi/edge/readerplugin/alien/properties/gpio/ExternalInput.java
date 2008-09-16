package org.rifidi.edge.readerplugin.alien.properties.gpio;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.dynamicswtforms.xml.annotaions.Form;
import org.rifidi.dynamicswtforms.xml.annotaions.FormElement;
import org.rifidi.dynamicswtforms.xml.constants.FormElementType;
import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.messageQueue.MessageQueue;
import org.rifidi.edge.readerplugin.alien.properties.AlienResponse;
import org.w3c.dom.Element;

/**
 *
 */
@Form(name = ExternalInput.EXTERNAL_INPUT, formElements = { @FormElement(type = FormElementType.INTEGER, elementName = ExternalInput.EXTERNAL_INPUT_DATA, editable = false, defaultValue = "0", displayName = ExternalInput.EXTERNAL_INPUT_DISPLAY, min = 0, max = 15) })
public class ExternalInput implements
		org.rifidi.edge.core.readerplugin.property.Property {
	
	private static final String EXTERNAL_INPUT = "ExternalInput";

	private static final String EXTERNAL_INPUT_DATA = "ExternalInputData";

	private static final String EXTERNAL_INPUT_DISPLAY = "External Input";
	
	private static final Log logger = LogFactory.getLog(ExternalInput.class);

	private static String command = "ExternalInput";

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
		return response.formulateResponseXML(propertyConfig, EXTERNAL_INPUT,
				EXTERNAL_INPUT_DATA);
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

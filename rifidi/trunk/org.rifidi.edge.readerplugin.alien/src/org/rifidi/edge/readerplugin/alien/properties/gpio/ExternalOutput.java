package org.rifidi.edge.readerplugin.alien.properties.gpio;

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
import org.rifidi.edge.readerplugin.alien.properties.PropertyWrapper;
import org.w3c.dom.Element;

/**
 *
 */
@Form(name = ExternalOutput.EXTERNAL_OUTPUT, formElements = { @FormElement(type = FormElementType.INTEGER, elementName = ExternalOutput.EXTERNAL_OUTPUT_DATA, editable = true, defaultValue = "0", displayName = ExternalOutput.EXTERNAL_OUTPUT_DISPLAY, min = 0, max = 15) })
public class ExternalOutput implements Property {

	private static final String EXTERNAL_OUTPUT = "ExternalOutput";

	private static final String EXTERNAL_OUTPUT_DATA = "ExternalOutputData";

	private static final String EXTERNAL_OUTPUT_DISPLAY = "External Output";

	private static final Log logger = LogFactory.getLog(ExternalOutput.class);

	private static String command = "ExternalOutput";

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
		return response.formulateResponseXML(propertyConfig, EXTERNAL_OUTPUT,
				EXTERNAL_OUTPUT_DATA);
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
		PropertyWrapper wrapper = new PropertyWrapper(propertyConfig);

		String comm = "\1set " + command + " = "
				+ wrapper.getElementValue(EXTERNAL_OUTPUT_DATA) + "\n";
		AlienResponse response = new AlienResponse();
		String responseString = null;
		try {
			connection.sendMessage(comm);
			responseString = (String) connection.receiveMessage();

		} catch (IOException e) {
			logger.debug("IOException");
		}
		response.setResponseMessage(responseString);
		return response.formulateResponseXML(propertyConfig, EXTERNAL_OUTPUT,
				EXTERNAL_OUTPUT_DATA);
	}

}

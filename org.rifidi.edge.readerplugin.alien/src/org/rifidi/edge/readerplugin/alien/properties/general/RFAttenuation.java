package org.rifidi.edge.readerplugin.alien.properties.general;

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
 * @author Matthew Dean
 */
@Form(name = RFAttenuation.RFATTENUATION, formElements = { @FormElement(type = FormElementType.INTEGER, elementName = RFAttenuation.RFATTENUATION_DATA, editable = true, defaultValue = "0", displayName = RFAttenuation.RFATTENUATION_DISPLAY, min=0, max=160) })
public class RFAttenuation implements Property {

	private static final String RFATTENUATION = "RFAttenuation";

	private static final String RFATTENUATION_DATA = "RFAttenuationData";

	private static final String RFATTENUATION_DISPLAY = "RF Attenuation";

	private static final Log logger = LogFactory.getLog(RFAttenuation.class);

	private static final String command = "RFAttenuation";

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
		return response.formulateResponseXML(propertyConfig, RFATTENUATION,
				RFATTENUATION_DATA);
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

		String command = "\1set " + RFAttenuation.command + " = "
				+ wrapper.getElementValue(RFATTENUATION_DATA) + "\n";
		AlienResponse response = new AlienResponse();
		String responseString = null;
		try {
			connection.sendMessage(command);
			responseString = (String) connection.receiveMessage();

		} catch (IOException e) {
			logger.debug("IOException");
		}
		response.setResponseMessage(responseString);
		return response.formulateResponseXML(propertyConfig, RFATTENUATION,
				RFATTENUATION_DATA);
	}

}

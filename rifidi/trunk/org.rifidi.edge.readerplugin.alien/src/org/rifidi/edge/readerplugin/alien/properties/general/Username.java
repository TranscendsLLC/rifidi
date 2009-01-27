package org.rifidi.edge.readerplugin.alien.properties.general;

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
 */
@Form(name = Username.USERNAME, formElements = { @FormElement(type = FormElementType.STRING, elementName = Username.USERNAME_DATA, editable = true, defaultValue = "alien", displayName = Username.USERNAME_DISPLAY, regex="(.)+") })
public class Username implements Property {

	public static final String USERNAME = "Username";

	public static final String USERNAME_DATA = "UsernameData";

	public static final String USERNAME_DISPLAY = "Username";

	private static final Log logger = LogFactory.getLog(Username.class);

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
			connection.sendMessage("\1get username\n");

			responseString = (String) connection.receiveMessage();

		} catch (IOException e) {
			logger.debug("IOException");
		}
		response.setResponseMessage(responseString);
		return response.formulateResponseXML(propertyConfig, USERNAME,
				USERNAME_DATA);
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

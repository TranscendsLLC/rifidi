package org.rifidi.edge.readerplugin.alien.properties.general;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.dynamicswtforms.xml.annotaions.Form;
import org.rifidi.dynamicswtforms.xml.annotaions.FormElement;
import org.rifidi.dynamicswtforms.xml.constants.FormElementType;
import org.rifidi.edge.core.api.readerplugin.Property;
import org.rifidi.edge.core.api.readerplugin.commands.CommandConfiguration;
import org.rifidi.edge.core.api.readerplugin.communication.Connection;
import org.rifidi.edge.core.api.readerplugin.messageQueue.MessageQueue;
import org.rifidi.edge.readerplugin.alien.properties.AlienResponse;

/**
 *
 */
@Form(name = Uptime.UPTIME, formElements = { @FormElement(type = FormElementType.INTEGER, elementName = Uptime.UPTIME_DATA, editable = false, defaultValue = Uptime.DEFAULT, displayName = Uptime.UPTIME_DISPLAY) })
public class Uptime implements Property {

	private static final String DEFAULT = "0";

	private static final String UPTIME = "Uptime";

	private static final String UPTIME_DATA = "UptimeData";

	private static final String UPTIME_DISPLAY = "Uptime";

	private static final Log logger = LogFactory.getLog(Uptime.class);

	private static final String command = "Uptime";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readerplugin.property.Property#getProperty(org.rifidi
	 * .edge.core.communication.Connection,
	 * org.rifidi.edge.core.messageQueue.MessageQueue, org.w3c.dom.Element)
	 */
	@Override
	public CommandConfiguration getProperty(Connection connection,
			MessageQueue errorQueue, CommandConfiguration propertyConfig) {
		AlienResponse response = new AlienResponse();
		String responseString = null;
		try {
			connection.sendMessage("\1get " + command + "\n");

			responseString = (String) connection.receiveMessage(500);

		} catch (IOException e) {
			logger.debug("IOException");
		}
		if (responseString == null) {
			responseString = DEFAULT;
		}
		response.setResponseMessage(responseString);
		return response.formulateResponse(UPTIME, UPTIME_DATA);
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
	public CommandConfiguration setProperty(Connection connection,
			MessageQueue errorQueue, CommandConfiguration propertyConfig) {
		return getProperty(connection, errorQueue, propertyConfig);
	}

}

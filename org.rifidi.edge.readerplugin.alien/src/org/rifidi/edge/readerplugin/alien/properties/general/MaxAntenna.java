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
import org.rifidi.edge.core.api.readerplugin.messageQueue.EventQueue;
import org.rifidi.edge.readerplugin.alien.properties.AlienResponse;

/**
 *
 */
@Form(name = MaxAntenna.MAXANTENNA, formElements = { @FormElement(type = FormElementType.STRING, elementName = MaxAntenna.MAXANTENNA_DATA, editable = true, defaultValue = "0", displayName = MaxAntenna.MAXANTENNA_DISPLAY) })
public class MaxAntenna implements Property {

	private static final String MAXANTENNA = "MaxAntenna";

	private static final String MAXANTENNA_DATA = "MaxAntennaData";

	private static final String MAXANTENNA_DISPLAY = "Max Antenna";

	private static final Log logger = LogFactory.getLog(MaxAntenna.class);

	private static String command = "maxantenna";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readerplugin.property.Property#getProperty(org.rifidi
	 * .edge.core.communication.Connection,
	 * org.rifidi.edge.core.messageQueue.MessageQueue, org.w3c.dom.Element)
	 */
	@Override
	public CommandConfiguration getProperty(Connection connection, EventQueue errorQueue,
			CommandConfiguration propertyConfig) {
		AlienResponse response = new AlienResponse();
		String responseString = null;
		try {
			connection.sendMessage("\1get " + command + "\n");

			responseString = (String) connection.receiveMessage();

		} catch (IOException e) {
			logger.debug("IOException");
		}
		response.setResponseMessage(responseString);
		return response.formulateResponse(MAXANTENNA, MAXANTENNA_DATA);
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
	public CommandConfiguration setProperty(Connection connection, EventQueue errorQueue,
			CommandConfiguration propertyConfig) {
		return getProperty(connection, errorQueue, propertyConfig);
	}

}

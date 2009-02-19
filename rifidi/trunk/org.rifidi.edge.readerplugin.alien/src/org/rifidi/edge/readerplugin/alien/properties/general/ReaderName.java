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
 * @author Matthew Dean - matt@pramari.com
 * @author Jerry Maine - jerry@pramari.com
 */
@Form(name = ReaderName.READERNAME, formElements = { @FormElement(type = FormElementType.STRING, elementName = ReaderName.READERNAME_DATA, editable = true, defaultValue = ReaderName.DEFAULT, displayName = ReaderName.READERNAME_DISPLAY, regex = "(.)+") })
public class ReaderName implements Property {

	private static final String DEFAULT = "ALIEN_READER";

	private static final String READERNAME = "ReaderName";

	private static final String READERNAME_DATA = "ReaderNameData";

	private static final String READERNAME_DISPLAY = "Reader Name";

	private static final Log logger = LogFactory.getLog(ReaderName.class);

	private static final String command = "ReaderName";

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

			responseString = (String) connection.receiveMessage(500);

		} catch (IOException e) {
			logger.debug("IOException");
		}

		if (responseString == null) {
			responseString = DEFAULT;
		}
		response.setResponseMessage(responseString);
		return response.formulateResponse(READERNAME, READERNAME_DATA);
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

		String comm = "\1set " + command + " = "
				+ propertyConfig.getArgValue(READERNAME_DATA) + "\n";
		AlienResponse response = new AlienResponse();
		String responseString = null;
		try {
			connection.sendMessage(comm);
			responseString = (String) connection.receiveMessage();

		} catch (IOException e) {
			logger.debug("IOException");
		}
		response.setResponseMessage(responseString);
		return response.formulateResponse(READERNAME, READERNAME_DATA);
	}
}

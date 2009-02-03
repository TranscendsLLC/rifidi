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
 * 
 * @author Matthew Dean - matt@pramari.com
 * @author Jerry Maine - jerry@pramari.com
 * 
 */
@Form(name = ReaderNumber.READERNUMBER, formElements = { @FormElement(type = FormElementType.INTEGER, elementName = ReaderNumber.READERNUMBER_DATA, editable = true, defaultValue = "0", displayName = ReaderNumber.READERNUMBER_DISPLAY, min = 0, max = 255) })
public class ReaderNumber implements Property {

	public static final String READERNUMBER = "ReaderNumber";

	public static final String READERNUMBER_DATA = "ReaderNumberData";

	public static final String READERNUMBER_DISPLAY = "Reader Number";

	private static final Log logger = LogFactory.getLog(ReaderNumber.class);

	private static final String command = "ReaderNumber";

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

			responseString = (String) connection.receiveMessage();

		} catch (IOException e) {
			logger.debug("IOException");
		}
		response.setResponseMessage(responseString);
		return response.formulateResponse(READERNUMBER, READERNUMBER_DATA);
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

		String comm = "\1set " + command + " = "
				+ propertyConfig.getArgValue(READERNUMBER_DATA) + "\n";
		AlienResponse response = new AlienResponse();
		String responseString = null;
		try {
			connection.sendMessage(comm);
			responseString = (String) connection.receiveMessage();

		} catch (IOException e) {
			logger.debug("IOException");
		}
		response.setResponseMessage(responseString);
		return response.formulateResponse(READERNUMBER, READERNUMBER_DATA);
	}
}

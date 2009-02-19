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
 * 
 * @author Matthew Dean - matt@pramari.com
 * @author Jerry Maine - jerry@pramari.com
 */
@Form(name = ReaderType.READERTYPE, formElements = { @FormElement(type = FormElementType.STRING, elementName = ReaderType.READERTYPE_DATA, editable = false, defaultValue = ReaderType.DEFUALT, displayName = ReaderType.READERTYPE_DISPLAY) })
public class ReaderType implements Property {

	private static final String DEFUALT = "0";

	private static final String READERTYPE = "ReaderType";

	private static final String READERTYPE_DATA = "ReaderTypeData";

	private static final String READERTYPE_DISPLAY = "Reader Type";

	private static final Log logger = LogFactory.getLog(ReaderType.class);

	private static String command = "ReaderType";

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
			EventQueue errorQueue, CommandConfiguration propertyConfig) {
		AlienResponse response = new AlienResponse();
		String responseString = null;
		try {
			connection.sendMessage("\1get " + command + "\n");

			responseString = (String) connection.receiveMessage(500);

		} catch (IOException e) {
			logger.debug("IOException");
		}
		if (responseString == null) {
			responseString = DEFUALT;
		}
		response.setResponseMessage(responseString);
		return response.formulateResponse(READERTYPE, READERTYPE_DATA);
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
			EventQueue errorQueue, CommandConfiguration propertyConfig) {
		return getProperty(connection, errorQueue, propertyConfig);
	}
}

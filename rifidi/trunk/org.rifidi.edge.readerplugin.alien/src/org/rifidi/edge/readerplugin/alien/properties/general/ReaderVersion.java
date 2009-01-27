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
 * @author Matthew Dean
 */
@Form(name = ReaderVersion.READERVERSION, formElements = { @FormElement(type = FormElementType.STRING, elementName = ReaderVersion.READERVERSION_DATA, editable = false, defaultValue = "0", displayName = ReaderVersion.READERVERSION_DISPLAY) })
public class ReaderVersion implements Property {
	
	private static final String READERVERSION = "ReaderVersion";

	private static final String READERVERSION_DATA = "ReaderVersionData";

	private static final String READERVERSION_DISPLAY = "Reader Version";
	
	private static final Log logger = LogFactory.getLog(ReaderVersion.class);

	private static final String command = "ReaderVersion";

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
		return response.formulateResponseXML(propertyConfig, READERVERSION,
				READERVERSION_DATA);
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

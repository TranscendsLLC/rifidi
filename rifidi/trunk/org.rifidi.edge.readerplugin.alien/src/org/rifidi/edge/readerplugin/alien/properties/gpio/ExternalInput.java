package org.rifidi.edge.readerplugin.alien.properties.gpio;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.dynamicswtforms.xml.annotaions.Form;
import org.rifidi.dynamicswtforms.xml.annotaions.FormElement;
import org.rifidi.dynamicswtforms.xml.constants.FormElementType;
import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.messageQueue.MessageQueue;
import org.w3c.dom.Element;


/**
 *
 */
@Form(name = "ExternalInput", formElements = { 
		@FormElement(type = FormElementType.INTEGER, elementName = "ExternalInputData", editable = false, defaultValue = "0", displayName = "External Input", min=0, max=15) })
public class ExternalInput implements org.rifidi.edge.core.readerplugin.property.Property {
	private static final Log logger = LogFactory
	.getLog(ExternalInput.class);

	static private String command = "ExternalInput";

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.property.Property#getProperty(org.rifidi.edge.core.communication.Connection, org.rifidi.edge.core.messageQueue.MessageQueue, org.w3c.dom.Element)
	 */
	@Override
	public Element getProperty(Connection connection, MessageQueue errorQueue,
			Element propertyConfig) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.property.Property#setProperty(org.rifidi.edge.core.communication.Connection, org.rifidi.edge.core.messageQueue.MessageQueue, org.w3c.dom.Element)
	 */
	@Override
	public Element setProperty(Connection connection, MessageQueue errorQueue,
			Element propertyConfig) {
		// TODO Auto-generated method stub
		return null;
	}
}

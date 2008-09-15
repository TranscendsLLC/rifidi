package org.rifidi.edge.readerplugin.alien.properties.general;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.messageQueue.MessageQueue;
import org.rifidi.edge.core.readerplugin.property.Property;
import org.w3c.dom.Element;


/**
 *
 */
public class RFAttenuation implements Property {
	private static final Log logger = LogFactory
	.getLog(RFAttenuation.class);

	static private String command = "RFAttenuation";

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

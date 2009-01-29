/* 
 * HeartbeatAddress.java
 *  Created:	Nov 18, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.readerplugin.alien.heartbeat;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.dynamicswtforms.xml.annotaions.Form;
import org.rifidi.dynamicswtforms.xml.annotaions.FormElement;
import org.rifidi.dynamicswtforms.xml.constants.FormElementType;
import org.rifidi.edge.core.api.communication.Connection;
import org.rifidi.edge.core.api.messageQueue.MessageQueue;
import org.rifidi.edge.core.api.readerplugin.commands.CommandConfiguration;
import org.rifidi.edge.core.api.readerplugin.property.api.Property;
import org.rifidi.edge.readerplugin.alien.properties.AlienResponse;

/**
 * 
 * 
 * @author Jerry Maine
 * @author Kyle Neumeier - kyle@pramari.com
 * @author Matthew Dean - matt@pramari.com
 */
@Form(name = HeartbeatAddress.HEARTBEAT_ADDRESS, formElements = { @FormElement(type = FormElementType.STRING, elementName = HeartbeatAddress.HEARTBEAT_ADDRESS_DATA, editable = true, defaultValue = "0", displayName = HeartbeatAddress.HEARTBEAT_ADDRESS_DISPLAY) })
public class HeartbeatAddress implements Property {

	private static final String HEARTBEAT_ADDRESS = "HeartbeatAddress";

	private static final String HEARTBEAT_ADDRESS_DATA = "HeartbeatAddressData";

	private static final String HEARTBEAT_ADDRESS_DISPLAY = "Heartbeat Address";

	private static final Log logger = LogFactory.getLog(HeartbeatAddress.class);

	private static final String command = "heartbeataddress";

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
		return response.formulateResponse(HEARTBEAT_ADDRESS,
				HEARTBEAT_ADDRESS_DATA);

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

		String command = "\1set " + HeartbeatAddress.command + " = "
				+ propertyConfig.getArgValue(HEARTBEAT_ADDRESS_DATA) + "\n";
		AlienResponse response = new AlienResponse();
		String responseString = null;
		try {
			connection.sendMessage(command);
			responseString = (String) connection.receiveMessage();

		} catch (IOException e) {
			logger.debug("IOException");
		}
		response.setResponseMessage(responseString);
		return response.formulateResponse(HEARTBEAT_ADDRESS,
				HEARTBEAT_ADDRESS_DATA);
	}

}

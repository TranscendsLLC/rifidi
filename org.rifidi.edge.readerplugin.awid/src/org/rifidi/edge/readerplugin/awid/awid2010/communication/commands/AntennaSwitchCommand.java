/*
 * Gen2PortalIDCommand.java
 * 
 * Created:     Oct 20th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:     The software in this package is published under the terms of the EPL License
 *                   A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.readerplugin.awid.awid2010.communication.commands;

/**
 * Command sent to the Awid reader that provides the ability to read EPC Gen2
 * tags. Responses are sent back as tags are seen
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AntennaSwitchCommand extends AbstractAwidCommand {

	/**
	 * Create a new Gen2PortalID command
	 * 
	 * @param enabled
	 *            Enables the antenna switch functionality if true. Otherwise disable the antenna switch.
	 *            06 00 0F 00 xx xx
	 */
	public AntennaSwitchCommand(boolean enabled) {
		if(enabled){
			rawmessage = new byte[] { 0x06, 0x00, 0x0F, 0x01};
		}else{
			rawmessage = new byte[] { 0x06, 0x00, 0x0F, 0x00};
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Antenna Switch Command";
	}

}

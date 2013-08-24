/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.adapter.awid.awid2010.communication.commands;

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

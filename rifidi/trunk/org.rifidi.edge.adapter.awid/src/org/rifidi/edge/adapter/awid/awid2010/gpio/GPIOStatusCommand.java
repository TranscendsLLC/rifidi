/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.adapter.awid.awid2010.gpio;

import org.rifidi.edge.adapter.awid.awid2010.communication.commands.AbstractAwidCommand;

/**
 * This class represents the bytes to send to the AWID the 'GPIO Status'
 * command
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class GPIOStatusCommand extends AbstractAwidCommand {

	/**
	 * Constructor
	 */
	public GPIOStatusCommand() {
		this.rawmessage = new byte[] { 0x05, 0x00, 0x03 };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AWID GPIO Status Command " + getCommandAsString();
	}

}

/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.adapter.awid.awid2010.gpio;

import org.rifidi.edge.adapter.awid.awid2010.communication.messages.AbstractAwidMessage;

/**
 * This class represents a response from the AWID reader to the 'Flash On/Off
 * Control' command
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class GPOFlashCommandResponse extends AbstractAwidMessage {

	/**
	 * Constructor
	 * 
	 * @param rawmessage
	 */
	public GPOFlashCommandResponse(byte[] rawmessage) {
		super(rawmessage);
	}

	/**
	 * 
	 * @return true if the flash message succeeded.
	 */
	public boolean flashSuceeded() {
		return this.rawmessage[3] == 0x00;
	}

	/**
	 * 
	 * @return Return true if teh flash failed because the pin was busy
	 */
	public boolean pinBusy() {
		return this.rawmessage[3] == 0x80;
	}

}

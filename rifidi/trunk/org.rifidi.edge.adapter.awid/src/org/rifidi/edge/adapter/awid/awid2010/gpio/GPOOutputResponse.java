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
 * This class represents a response from the AWID reader to the 'Output' command
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class GPOOutputResponse extends AbstractAwidMessage {

	/**
	 * Constructor
	 * 
	 * @param rawmessage
	 */
	public GPOOutputResponse(byte[] rawmessage) {
		super(rawmessage);
	}

	/**
	 * 
	 * @return true if the 'Output' command succeeded.
	 */
	public boolean GPOSetSucceeded() {
		return rawmessage[3] == 0x00;
	}
}

/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.adapter.awid.awid2010.communication.messages;

/**
 * This is a response message for when the second byte is 0xFF and the thrid
 * byte is 0x1E. The message is sent back from the awid reader when a portal ID
 * command times out
 * 
 * @author Kyle Neumeier - kyle@pramari.
 * 
 */
public class PortalIDError extends AbstractAwidMessage {

	public PortalIDError(byte[] rawmessage) {
		super(rawmessage);
	}

}

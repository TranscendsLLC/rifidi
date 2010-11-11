/*
 * PoralIDError.java
 * 
 * Created:     Oct 20th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:     The software in this package is published under the terms of the EPL License
 *                   A copy of the license is included in this distribution under Rifidi-License.txt 
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

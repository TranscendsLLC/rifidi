/*
 * ReaderStatusMessage.java
 * 
 * Created:     Feb 19th, 2010
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:     The software in this package is published under the terms of the EPL License
 *                   A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.adapter.awid.awid2010.communication.messages;

/**
 * This class represents a response from the Awid reader to a Reader Status
 * message.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ReaderStatusMessage extends AbstractAwidMessage {

	public ReaderStatusMessage(byte[] rawmessage) {
		super(rawmessage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Reader Status Response";
	}

}

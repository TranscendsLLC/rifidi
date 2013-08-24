/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
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

/*
 * AbstractAwidCommand.java
 * 
 * Created:     Feb 19th, 2010
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:     The software in this package is published under the terms of the EPL License
 *                   A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.adapter.awid.awid2010.communication.commands;

/**
 * This class represents a reader status command that is sent to the AWID reader
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ReaderStatusCommand extends AbstractAwidCommand {

	public ReaderStatusCommand() {
		super.rawmessage = new byte[] { 0x05, 0x00, 0x0B };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Reader Status Command";
	}

}

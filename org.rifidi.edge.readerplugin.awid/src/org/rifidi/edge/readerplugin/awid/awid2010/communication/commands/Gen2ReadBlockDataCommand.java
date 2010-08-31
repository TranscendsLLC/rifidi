/*
 * Gen2ReadBlockDataCommand.java
 * 
 * Created:     Feb 15th, 2010
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
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * @author Daniel Gomez - dgomez@idlinksolutions.com
 * 
 */
public class Gen2ReadBlockDataCommand extends AbstractAwidCommand {

	/**
	 * @param memoryBank
	 * 			0x00 Access Code + Kill Code Bank
	 * 			0x01 EPC bank
	 * 			0x02 TID bank
	 * 			0x03 User bank
	 */
	public Gen2ReadBlockDataCommand(byte memoryBank) {
		
		rawmessage = new byte[] {0x06, 0x20, 0x0D, memoryBank};
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Gen2 Read Block Data Command";
	}

}

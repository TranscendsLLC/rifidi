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

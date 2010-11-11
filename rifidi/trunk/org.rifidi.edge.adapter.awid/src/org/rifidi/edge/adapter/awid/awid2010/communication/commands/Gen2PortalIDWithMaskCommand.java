/*
 * Gen2PortalIDWithMaskCommand.java
 * 
 * Created:     Feb 15th, 2010
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:     The software in this package is published under the terms of the EPL License
 *                   A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.adapter.awid.awid2010.communication.commands;

/**
 * Command sent to the Awid reader that provides the ability to read EPC Gen2
 * tags. Responses are sent back as tags are seen
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * @author Daniel Gomez - dgomez@idlinksolutions.com
 * 
 */
public class Gen2PortalIDWithMaskCommand extends AbstractAwidCommand {

	/**
	 * Create a new Gen2PortalWithMaskID command
	 * @param packetLength
	 * 			1-byte packet length, value depending on how long the mask is or simply MaskLength plus
	 * 			fourteen
	 * @param memoryBank
	 * 			0x01 EPC bank
	 * 			0x02 TID bank
	 * 			0x03 User bank
	 * 
	 * @param startingBit
	 * 			starting bit position in memory bank
	 * 
	 * @param maskLength
	 * 			Mask length ex:
	 * 			0x06	6 bits
	 * 
	 * @param maskValue
	 *  		Mask value for bit mask ex:
	 *  		0xFC	"11111100"	
	 * 
	 * @param qValue
	 * 			For example, if there are about 20 tags to be read, then a Q Value of 4 should be 
	 * 			used for reader to have 15 (2^4-1) time slots employed by its searching algorithm and 5
	 * 			for 35 tags, 7 for 131 tags and so on.	
	 * 
	 * @param timeout
	 *            Execute this command for timeout*100 ms. If set to 0x00,
	 *            execute until stop command is sent
	 * @param repeat
	 *            Return results every repeat*100 ms. If set to 0x00
	 *            continuously return tags.
	 *            
	 *            0F 20 5E 02 04 01 20 06 FC 01 08 00 00 XX XX
	 */
	public Gen2PortalIDWithMaskCommand(byte packetLength, byte memoryBank, 
			byte startingBit, byte maskLength, byte maskValue, byte qValue, 
			byte timeout, byte repeat) {
		
		rawmessage = new byte[] { packetLength, 0x20, 0x5E, 0x02, 0x04, memoryBank, startingBit, 
				maskLength,maskValue, 0x01, qValue, timeout, repeat };
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Gen2 Portal ID With Mask Command";
	}

}

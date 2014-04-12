/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
package org.rifidi.edge.adapter.awid.awid2010.gpio;

import java.util.BitSet;

import org.rifidi.edge.adapter.awid.awid2010.communication.messages.AbstractAwidMessage;

/**
 * This class represents a response from the AWID reader to the 'GPIO Status'
 * Command
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class GPIOStatusResponse extends AbstractAwidMessage {

	/** Constructor */
	public GPIOStatusResponse(byte[] rawmessage) {
		super(rawmessage);
	}

	/**
	 * Get the state of the input pins
	 * 
	 * @return BitSet representing the state of the input pins. The BitSet has
	 *         four bits, with bit 0 indicating the state of Input pin 1.
	 */
	public BitSet getInputStatus() {
		BitSet inputPorts = new BitSet(4);
		for (int i = 3; i < 7; i++) {
			int portNum = i - 3;
			if (rawmessage[i] == 0x01)
				inputPorts.set(portNum);

		}
		return inputPorts;
	}

	/**
	 * Get the state of the output pins
	 * 
	 * @return A BitSet representing the state of the output pins. The BitSet
	 *         has four bits, with bit 0 indicating the state of Output pin 1.
	 */
	public BitSet getOutputStatus() {
		BitSet outputPorts = new BitSet(4);
		for (int i = 7; i < 11; i++) {
			int portNum = i - 7;
			if (rawmessage[i] == 0x01)
				outputPorts.set(portNum);

		}
		return outputPorts;
	}

}

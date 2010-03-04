/**
 * 
 */
package org.rifidi.edge.readerplugin.awid.awid2010.gpio;

import java.util.BitSet;

import org.rifidi.edge.readerplugin.awid.awid2010.communication.messages.AbstractAwidMessage;

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

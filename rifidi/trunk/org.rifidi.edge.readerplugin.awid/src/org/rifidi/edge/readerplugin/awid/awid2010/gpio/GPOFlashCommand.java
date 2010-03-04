/**
 * 
 */
package org.rifidi.edge.readerplugin.awid.awid2010.gpio;

import org.rifidi.edge.readerplugin.awid.awid2010.communication.commands.AbstractAwidCommand;

/**
 * This class represents the bytes to send to the AWID the 'Flash On/Off
 * Control' command
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class GPOFlashCommand extends AbstractAwidCommand {

	/**
	 * Constructor
	 * 
	 * @param pin
	 *            The pin to set
	 * @param onTime
	 *            The time to hold the line high in 100 ms
	 * @param offTime
	 *            The time to hold the line low in 100 ms
	 * @param totalCount
	 *            The number of times to flash
	 */
	public GPOFlashCommand(byte pin, byte onTime, byte offTime, byte totalCount) {
		super();
		if (pin < 0 || pin > 3) {
			throw new IllegalArgumentException("Pin must be 0>=pin>=3");
		}
		this.rawmessage = new byte[] { 0x09, 0x00, 0x02, pin, onTime, offTime,
				totalCount };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AWID Flash On/Off Control: " + getCommandAsString();
	}

}

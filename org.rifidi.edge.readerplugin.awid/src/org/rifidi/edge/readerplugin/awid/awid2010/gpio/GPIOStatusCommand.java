/**
 * 
 */
package org.rifidi.edge.readerplugin.awid.awid2010.gpio;

import org.rifidi.edge.readerplugin.awid.awid2010.communication.commands.AbstractAwidCommand;

/**
 * This class represents the bytes to send to the AWID the 'GPIO Status'
 * command
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class GPIOStatusCommand extends AbstractAwidCommand {

	/**
	 * Constructor
	 */
	public GPIOStatusCommand() {
		this.rawmessage = new byte[] { 0x05, 0x00, 0x03 };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AWID GPIO Status Command " + getCommandAsString();
	}

}

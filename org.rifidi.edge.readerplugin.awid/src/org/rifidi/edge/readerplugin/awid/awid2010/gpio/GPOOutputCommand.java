/**
 * 
 */
package org.rifidi.edge.readerplugin.awid.awid2010.gpio;

import org.rifidi.edge.readerplugin.awid.awid2010.communication.commands.AbstractAwidCommand;

/**
 * This class represents the bytes to send to the AWID the 'Output' command
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class GPOOutputCommand extends AbstractAwidCommand {

	/**
	 * Constructor
	 * 
	 * @param port
	 *            The port to set
	 * @param high
	 *            if true, set the port high. If false, set the port low
	 */
	public GPOOutputCommand(int port, boolean high) {
		super();
		if (port < 0 || port > 3) {
			throw new IllegalArgumentException("Port must be 0>=port>=3");
		}
		byte on;
		if (high) {
			on = 0x00;
		} else {
			on = 0x01;
		}
		this.rawmessage = new byte[] { 0x06, 0x00, on, (byte) port };

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AWID Output Command " + getCommandAsString();
	}

}

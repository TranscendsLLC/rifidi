/**
 * 
 */
package org.rifidi.edge.readerplugin.awid.awid2010.gpio;

import org.rifidi.edge.readerplugin.awid.awid2010.communication.messages.AbstractAwidMessage;

/**
 * This class represents a response from the AWID reader to the 'Output' command
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class GPOOutputResponse extends AbstractAwidMessage {

	/**
	 * Constructor
	 * 
	 * @param rawmessage
	 */
	public GPOOutputResponse(byte[] rawmessage) {
		super(rawmessage);
	}

	/**
	 * 
	 * @return true if the 'Output' command succeeded.
	 */
	public boolean GPOSetSucceeded() {
		return rawmessage[3] == 0x00;
	}
}

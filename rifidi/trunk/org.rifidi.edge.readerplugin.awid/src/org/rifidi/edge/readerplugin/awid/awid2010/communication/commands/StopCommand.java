/*
 * StopCommand.java
 * 
 * Created:     Oct 20th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:     The software in this package is published under the terms of the EPL License
 *                   A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.readerplugin.awid.awid2010.communication.commands;

/**
 * A command to stop the current activity on the Awid reader
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class StopCommand extends AbstractAwidCommand {

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.readerplugin.awid.awid2010.communication.commands.
	 * AbstractAwidCommand#getCommand()
	 */
	@Override
	public byte[] getCommand() {
		// need to override this method so that CRC is not calculated.
		return new byte[] { 0x00 };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Stop Command";
	}

}

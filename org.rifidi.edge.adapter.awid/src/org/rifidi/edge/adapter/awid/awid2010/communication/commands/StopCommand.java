/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.adapter.awid.awid2010.communication.commands;

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

/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.adapter.awid.awid2010.communication.commands;

/**
 * This command enables the identification of the antenna in tag messages coming
 * from the awid reader.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AntennaSourceCommand extends AbstractAwidCommand {

	/**
	 * 
	 */
	public AntennaSourceCommand() {
		this.rawmessage = new byte[] { 06, 00, 0x53, 01 };
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Antenna Source Command";
	}

}

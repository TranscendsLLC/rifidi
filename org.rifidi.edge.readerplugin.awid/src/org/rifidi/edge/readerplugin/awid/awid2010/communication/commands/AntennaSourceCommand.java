/*
 * AntennaSourceCommand.java
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

}

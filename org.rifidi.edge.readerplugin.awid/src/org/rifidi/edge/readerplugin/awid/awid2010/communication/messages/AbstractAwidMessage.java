/*
 * AbstractAwidMessage.java
 * 
 * Created:     Oct 20th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:     The software in this package is published under the terms of the EPL License
 *                   A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.readerplugin.awid.awid2010.communication.messages;

/**
 * An AwidMessage is an array of bytes coming from the Awid reader. 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AbstractAwidMessage {

	/**The array of bytes*/
	protected final byte[] rawmessage;

	/**
	 * @param rawmessage
	 */
	public AbstractAwidMessage(byte[] rawmessage) {
		this.rawmessage = rawmessage;
	}

}

/* 
 * Protocol.java
 *  Created:	Jun 20, 2006
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.core.communication.protocol;

import org.rifidi.edge.core.exception.readerConnection.RifidiInvalidMessageFormat;


/**
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public abstract class Protocol {

	/**
	 * Add single byte form the Stream to the internal buffer
	 * 
	 * @param b
	 *            the most recent byte read from the socket
	 * @return true when a whole message was read
	 */
	public abstract Object add(byte b) throws  RifidiInvalidMessageFormat;


	/**
	 * Transform object message into a array of bytes to transmit it
	 * 
	 * @param o
	 *            the message to send
	 * @return byte array representing the message
	 */
	public abstract byte[] toByteArray(Object o) throws RifidiInvalidMessageFormat;

//	 /**
//	 * This method receives a raw array of bytes from the socket and
//	 * should transform it into a reader specific object message that
//	 * its adapter can understand.
//	 * @param arg
//	 * @return
//	 */
//	 public abstract List<Object> toObject(byte[] arg);
//	
//	 /**
//	 * This method receives a reader specific object and
//	 * should transform it into an array of bytes that
//	 * the specific reader can understand
//	 * @param arg
//	 * @return
//	 */
//	 public abstract byte[] fromObject(Object arg);
}

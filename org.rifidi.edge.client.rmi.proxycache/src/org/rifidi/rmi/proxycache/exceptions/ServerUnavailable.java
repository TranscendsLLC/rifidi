/*
 * ServerUnavailable.java
 * 
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the EPL License
 *                    A copy of the license is included in this distribution under Rifidi-License.txt 
 */
/**
 * 
 */
package org.rifidi.rmi.proxycache.exceptions;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Thrown if there was a problem when trying to make an RMI call on the remote
 * server
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ServerUnavailable extends Exception implements Externalizable {
	
	/**
	 * 
	 */
	public ServerUnavailable() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ServerUnavailable(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public ServerUnavailable(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ServerUnavailable(Throwable cause) {
		super(cause);
	}

	@Override
	public void readExternal(ObjectInput arg0) throws IOException,
			ClassNotFoundException {
	}

	@Override
	public void writeExternal(ObjectOutput arg0) throws IOException {
	}

}

/*
 *  TestBarcode.java
 *
 *  Created:	Apr 28, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.barcodesimulator.test;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.readerplugin.barcodesimulator.BarcodeConstants;

/**
 * Tests out a barcode reader by sending either a fake or a real tag.  
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class TestBarcode {

	/**
	 * The logger for this object.
	 */
	private static final Log logger = LogFactory.getLog(TestBarcode.class);

	/**
	 * Start (called by spring)
	 */
	public void start() {
		logger.debug("Starting Barcode Simulator App");
	}

	/**
	 * Stop (called by spring)
	 */
	public void stop() {
		logger.debug("Stopping Barcode Simulator App");
	}

	/**
	 * @param message
	 */
	public void sendMessage(String message) {
		try {
			Socket sock = new Socket(BarcodeConstants.IP_ADDRESS,
					BarcodeConstants.PORT);
			DataOutputStream dos = new DataOutputStream(sock.getOutputStream());
			dos.writeBytes(message);
		} catch (UnknownHostException e) {
			logger.error("Unknown host exception at IP: " + e.getMessage());
		} catch (IOException e) {
			logger.error("IOException occured while sending message "
					+ "to barcode: " +  e.getMessage());
		}
	}
}

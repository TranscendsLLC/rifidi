/*
 *  SerialCommandProvider.java
 *
 *  Created:	May 18, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.app.diag.serial;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

/**
 * This class provides the commands that allow you to list the serial ports
 * connected to the current computer, and also to connect to a serial device and
 * read in data for debugging purposes.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class SerialCommandProvider implements CommandProvider {

	private volatile SerialApp serialApp;

	/**
	 * Lists the serial ports.
	 * 
	 * @param intp
	 * @return
	 */
	public Object _listSerial(CommandInterpreter intp) {
		this.serialApp.listSerialPorts(intp);
		return null;
	}

	/**
	 * Connects to a serial port.
	 * 
	 * @param intp
	 * @return
	 */
	public Object _connectSerial(CommandInterpreter intp) {
		String port = intp.nextArgument();
		if (port == null || port.equals("")) {
			intp.println("usage: connectSerial <port> <baud> "
					+ "<databits> <stopbits> <parity>");
			return null;
		}
		String baud = intp.nextArgument();
		if (baud == null || baud.equals("") || !isNum(baud)) {
			intp.println("usage: connectSerial <port> <baud> "
					+ "<databits> <stopbits> <parity>");
			return null;
		}
		String data = intp.nextArgument();
		if (data == null || data.equals("") || !isNum(data)) {
			intp.println("usage: connectSerial <port> <baud> "
					+ "<databits> <stopbits> <parity>");
			return null;
		}
		String stop = intp.nextArgument();
		if (stop == null || stop.equals("") || !isNum(stop)) {
			intp.println("usage: connectSerial <port> <baud> "
					+ "<databits> <stopbits> <parity>");
			return null;
		}
		String parity = intp.nextArgument();
		if (parity == null || parity.equals("") || !isNum(parity)) {
			intp.println("usage: connectSerial <port> <baud> "
					+ "<databits> <stopbits> <parity>");
			return null;
		}
		this.serialApp
				.openSerialPort(port, Integer.parseInt(baud), Integer
						.parseInt(data), Integer.parseInt(stop), Integer
						.parseInt(stop));
		return null;
	}

	/**
	 * Disconnects from the serial port that it is currently connected to.
	 * 
	 * @param intp
	 * @return
	 */
	public Object _disconnectSerial(CommandInterpreter intp) {
		this.serialApp.closeSerialPort();
		return null;
	}

	/*
	 * Returns true if the argument is a number, false otherwise.
	 */
	private boolean isNum(String arg) {
		try {
			Integer.parseInt(arg);
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	/**
	 * Called by Spring.
	 * 
	 * @param serialApp
	 */
	public void setSerialApp(SerialApp serialApp) {
		this.serialApp = serialApp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.osgi.framework.console.CommandProvider#getHelp()
	 */
	@Override
	public String getHelp() {
		StringBuffer retVal = new StringBuffer();
		retVal.append("  ---Diagnostic Serial App Commands---\n");
		retVal.append("\tlistSerial - Prints out a list of serial ports " +
				"that belong to this reader \n");
		retVal.append("\tconnectSerial <port> <baud> <databits> <stopbits> <parity> " +
				"- Connects to a serial port with the given options.  \n");
		retVal.append("\tdisconnectSerial - Disconnects you from any serial ports you are " +
				"connected to.\n");
		return retVal.toString();
	}

}

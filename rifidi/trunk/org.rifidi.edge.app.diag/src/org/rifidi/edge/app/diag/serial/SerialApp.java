/*
 *  SerialApp.java
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

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.osgi.framework.console.CommandInterpreter;

/**
 * 
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class SerialApp {

	private static final Log logger = LogFactory.getLog(SerialApp.class);

	private SerialTester serial = new SerialTester();

	public void start() {
		logger.debug("Starting SerialApp");
	}

	public void stop() {
		logger.debug("Stopping SerialApp");
	}

	/**
	 * Lists the serial ports that this computer has.
	 * 
	 * @param intp
	 */
	@SuppressWarnings("unchecked")
	public void listSerialPorts(CommandInterpreter intp) {
		Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier
				.getPortIdentifiers();
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier portIdentifier = portEnum.nextElement();
			intp.println(portIdentifier.getName() + " - "
					+ getPortTypeName(portIdentifier.getPortType()));
		}
	}

	/**
	 * 
	 * @param serialPort
	 */
	public void openSerialPort(String serialPort, int baud, int databits,
			int stopbits, int parity) {
		serial.start(serialPort, baud, databits, stopbits, parity);
	}

	/**
	 * 
	 */
	public void closeSerialPort() {
		if (serial.isOpen) {
			System.out.println("Closing the serial port at " + serial.portName);
			serial.stop();
		}
	}

	/*
	 * Returns the type of port this serial port is.
	 */
	private static String getPortTypeName(int portType) {
		switch (portType) {
		case CommPortIdentifier.PORT_I2C:
			return "I2C";
		case CommPortIdentifier.PORT_PARALLEL:
			return "Parallel";
		case CommPortIdentifier.PORT_RAW:
			return "Raw";
		case CommPortIdentifier.PORT_RS485:
			return "RS485";
		case CommPortIdentifier.PORT_SERIAL:
			return "Serial";
		default:
			return "unknown type";
		}
	}

	/**
	 * 
	 * @author Matthew Dean - matt@pramari.com
	 */
	private class SerialTester {

		Thread readThread;

		CommPort commPort;

		public String portName = "";

		public boolean isOpen = false;

		public SerialTester() {
			super();
		}

		private void connect(String portName, int baud, int databits,
				int stopbits, int parity) throws Exception {
			this.portName = portName;
			CommPortIdentifier portIdentifier = CommPortIdentifier
					.getPortIdentifier(portName);
			if (portIdentifier.isCurrentlyOwned()) {
				System.out.println("Error: Port is currently in use");
			} else {
				commPort = portIdentifier.open(this.getClass().getName(), 2000);

				if (commPort instanceof SerialPort) {
					SerialPort serialPort = (SerialPort) commPort;
					serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,
							SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

					InputStream in = serialPort.getInputStream();

					this.readThread = new Thread(new SerialReader(in));
					this.readThread.start();
				} else {
					System.out
							.println("Error: Only serial ports are handled by this command.");
				}
			}
		}

		/** */
		public class SerialReader implements Runnable {
			InputStream in;

			public SerialReader(InputStream in) {
				this.in = in;
			}

			public void run() {
				int data;
				try {
					while ((data = in.read()) > -1) {
						System.out.print(data + " ");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		/**
		 * Start reading from the given serial port
		 * 
		 * @param port
		 */
		public void start(String port, int baud, int data, int stop, int parity) {
			if (!isOpen) {
				try {
					(new SerialTester())
							.connect(port, baud, data, stop, parity);
					isOpen = true;
				} catch (Exception e) {
					System.err
							.println("There was an exception while trying to connect to serial port "
									+ port + ": " + e.getMessage());
				}
			}
		}

		/**
		 * 
		 */
		public void stop() {
			try {
				if (this.readThread != null) {
					this.readThread.join();
				}
				if (this.commPort != null) {
					this.commPort.getInputStream().close();
				}
			} catch (Exception e) {
				//Don't care
			} finally {
				if (this.commPort != null) {
					this.commPort.close();
				}
				isOpen = false;
			}
		}
	}

}

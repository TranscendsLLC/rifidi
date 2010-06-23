/**
 * 
 */
package org.rifidi.edge.core.app.api.resources.serial;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class allows sends data via a Serial Port
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class SerialResource {

	// Info for creating the serial port:
	private String commPortName = null;
	private int baud, databits, stopbits, parity;
	/** The serial port we will talk to. */
	private CommPort commPort = null;
	/** The ID for the serial port */
	private CommPortIdentifier portIdentifier = null;
	private static final Log logger = LogFactory
			.getLog(SerialResource.class);

	/**
	 * @param commPortName
	 * @param baud
	 * @param databits
	 * @param stopbits
	 * @param parity
	 */
	SerialResource() {
		super();
		this.commPortName = "/dev/ttyS0";
		this.baud = 38400;
		this.databits = 8;
		this.stopbits = 1;
		this.parity = 0;
	}

	/**
	 * Establish a serial connection
	 * 
	 * @throws Exception
	 *             If there was a problem when connecting
	 */
	public void connect() throws Exception {
		portIdentifier = CommPortIdentifier.getPortIdentifier(commPortName);
		if (portIdentifier.isCurrentlyOwned()) {
			logger.warn("Error: Port is currently in use");
			throw new PortInUseException();
		} else {
			commPort = portIdentifier.open(this.getClass().getName(), 2000);

			// will throw a cast class exception if somehow this is connected to
			// the wrong thing
			SerialPort serialPort = (SerialPort) commPort;
			serialPort.setSerialPortParams(this.baud, this.databits,
					this.stopbits, this.parity);
			logger.info(this + " is connected");

		}
	}

	/**
	 * Disconnect from the serial connection
	 */
	public void disconnect() {
		try {
			commPort.close();
			commPort.getOutputStream().close();
		} catch (IOException e) {
		}

	}

	/**
	 * Write the given bytes over the serial connection
	 * 
	 * @param bytes
	 */
	public void write(byte[] bytes) throws IOException {
		commPort.getOutputStream().write(bytes);
		commPort.getOutputStream().flush();
	}

	/**
	 * @param commPortName
	 *            the commPortName to set
	 */
	void setCommPortName(String commPortName) {
		this.commPortName = commPortName;
	}

	/**
	 * @param baud
	 *            the baud to set
	 */
	void setBaud(int baud) {
		this.baud = baud;
	}

	/**
	 * @param databits
	 *            the databits to set
	 */
	void setDatabits(int databits) {
		this.databits = databits;
	}

	/**
	 * @param stopbits
	 *            the stopbits to set
	 */
	void setStopbits(int stopbits) {
		this.stopbits = stopbits;
	}

	/**
	 * @param parity
	 *            the parity to set
	 */
	void setParity(int parity) {
		this.parity = parity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Serial Communication (" + commPortName + ")";
	}
}

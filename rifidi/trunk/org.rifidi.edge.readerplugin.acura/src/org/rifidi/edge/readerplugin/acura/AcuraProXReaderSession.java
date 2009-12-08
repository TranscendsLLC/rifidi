/*
 *  AcuraProXReaderSession.java
 *
 *  Created:	Dec 3, 2009
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.acura;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.sensors.commands.Command;
import org.rifidi.edge.core.sensors.sessions.AbstractSensorSession;
import org.springframework.jms.core.JmsTemplate;

/**
 * 
 * 
 * @author Matthew Dean
 */
public class AcuraProXReaderSession extends AbstractSensorSession {

	private OneWaySerialComm serialPort = null;

	/**
	 * The Serial Port the reader will connect with.
	 */
	private String commPort = null;

	/**
	 * 
	 * 
	 * @param sensor
	 *            The sensor type.
	 * @param id
	 *            The ID of the reader
	 * @param comm
	 *            The Serial Port the reader will connect with.
	 * @param destination
	 * @param template
	 * @param commandConfigurations
	 */
	public AcuraProXReaderSession(AbstractSensor<?> sensor, String id,
			String comm, JmsTemplate template,
			Set<AbstractCommandConfiguration<?>> commandConfigurations) {
		super(sensor, id, template.getDefaultDestination(), template, commandConfigurations);

		commPort = comm;
		serialPort = new OneWaySerialComm();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.sensors.SensorSession#_connect()
	 */
	@Override
	protected void _connect() throws IOException {
		boolean success = true;
		this.setStatus(SessionStatus.CONNECTING);
		try {
			serialPort.connect(commPort);
		} catch (Exception e) {
			success = false;
			this.setStatus(SessionStatus.CLOSED);
			throw new IOException(e.getMessage());
		}
		if (success) {
			this.setStatus(SessionStatus.PROCESSING);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.sensors.SensorSession#disconnect()
	 */
	@Override
	public void disconnect() {
		if (this.getStatus().equals(SessionStatus.PROCESSING)) {
			this.serialPort.closeConnection();
			this.setStatus(SessionStatus.CLOSED);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.sensors.SensorSession#getResetCommand()
	 */
	@Override
	protected Command getResetCommand() {
		return null;
	}

	/**
	 * Inner class taking care of serial communication.
	 */
	public class OneWaySerialComm {
		private CommPort commPort = null;

		/**
		 * 
		 */
		public OneWaySerialComm() {
			super();
		}

		/**
		 * Connect the
		 * 
		 * @param portName
		 * @throws Exception
		 */
		public void connect(String portName) throws Exception {
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

					(new Thread(new SerialReader(in))).start();
				} else {
					System.out
							.println("Error: Only serial ports are handled by this example.");
				}
			}
		}

		/**
		 * Close the connection for the comm port.
		 */
		public void closeConnection() {
			commPort.close();
		}

		/** */
		public class SerialReader implements Runnable {
			InputStream in;

			public SerialReader(InputStream in) {
				this.in = in;
			}

			public void run() {
				byte[] buffer = new byte[1024];
				int len = -1;
				try {
					while ((len = this.in.read(buffer)) > -1) {
						System.out.print(new String(buffer, 0, len));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}

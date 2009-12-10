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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.sensors.commands.Command;
import org.rifidi.edge.core.sensors.sessions.AbstractSensorSession;
import org.rifidi.edge.readerplugin.acura.commands.internal.AcuraProXResetCommand;
import org.springframework.jms.core.JmsTemplate;

/**
 * 
 * 
 * @author Matthew Dean
 */
public class AcuraProXReaderSession extends AbstractSensorSession {

	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(AcuraProXReaderSession.class);

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
		super(sensor, id, template.getDefaultDestination(), template,
				commandConfigurations);

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
		this.setStatus(SessionStatus.CONNECTING);
		if (processing.get()) {
			if (!processing.compareAndSet(true, false)) {
				logger
						.warn("Killed a non active executor. That should not happen. ");
			}
			// TODO: better would be to have a method in
			// AbstractSensorSession that handles the shutdown of the
			// executor
			executor.shutdownNow();
			executor = null;
			resetCommands();
		}
		boolean success = true;
		try {
			serialPort.connect(commPort);
		} catch (Exception e) {
			success = false;
			this.setStatus(SessionStatus.CLOSED);
			throw new IOException(e.getMessage());
		}
		executor = new ScheduledThreadPoolExecutor(1);
		if (success) {
			this.setStatus(SessionStatus.PROCESSING);
			processing.compareAndSet(false, true);
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
		return new AcuraProXResetCommand("AcuraProXResetCommand");
	}

	/**
	 * Inner class taking care of serial communication.
	 */
	public class OneWaySerialComm {
		private CommPort commPort = null;

		public static final byte END_OF_TEXT = 0x03;
		public static final byte START_OF_TEXT = 0x02;

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

		/**
		 * 
		 */
		public class SerialReader implements Runnable {
			InputStream in;

			public SerialReader(InputStream in) {
				this.in = in;
			}

			public void run() {
				List<Byte> byteBuffer = new ArrayList<Byte>();
				int current = -1;
				try {
					while ((current = this.in.read()) > -1) {
						// Either it is a start byte, and end byte, or a middle
						// byte
						if (((byte) current) == START_OF_TEXT) {
							//Make sure there is nothing in the list for the start
							byteBuffer.clear();
						} else if (((byte) current) == END_OF_TEXT) {
							//Send the completed buffer off for processing
							
							//Clear the list, we don't need it anymore.  
							byteBuffer.clear();
						} else {
							//We don't care about any bytes after the first 10.  
							if (byteBuffer.size() < 10) {
								byteBuffer.add(new Byte((byte)current));
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}

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
import org.rifidi.edge.core.services.notification.NotifierService;
import org.rifidi.edge.readerplugin.acura.commands.internal.AcuraProXResetCommand;
import org.springframework.jms.core.JmsTemplate;

/**
 * The Session for the AcuraProXReader.
 * 
 * @author Matthew Dean
 */
public class AcuraProXReaderSession extends AbstractSensorSession {

	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(AcuraProXReaderSession.class);

	private OneWaySerialComm serialPort = null;

	/** Service used to send out notifications */
	private volatile NotifierService notifierService;

	/**
	 * The Serial Port the reader will connect with.
	 */
	private String commPort = null;

	private String readerID = null;

	private JmsTemplate template = null;

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
			String comm, JmsTemplate template, NotifierService notifierService,
			String readerID,
			Set<AbstractCommandConfiguration<?>> commandConfigurations) {
		super(sensor, id, template.getDefaultDestination(), template,
				commandConfigurations);
		this.readerID = readerID;
		this.template = template;
		this.notifierService = notifierService;
		commPort = comm;
		serialPort = new OneWaySerialComm(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readers.impl.AbstractReaderSession#setStatus(org
	 * .rifidi.edge.core.api.SessionStatus)
	 */
	@Override
	protected synchronized void setStatus(SessionStatus status) {
		super.setStatus(status);
		// TODO: Remove this once we have aspectJ
		NotifierService service = notifierService;
		if (service != null) {
			service.sessionStatusChanged(this.readerID, this.getID(), status);
		}
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
				logger.warn("Killed a non active executor. "
						+ "That should not happen. ");
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
			processing.compareAndSet(false, true);
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
		//System.out.println("Disconnecting");
		try {
			this.setStatus(SessionStatus.DISCONNECTING);
			//System.out.println("attempting to close the connection");
			//System.out.flush();
			if (this.getStatus().equals(SessionStatus.PROCESSING)) {
				this.serialPort.closeConnection();
			}
			//System.out.println("closed connection successfully!");
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			//System.out.println("In the finally!");
			// make sure executor is shutdown!
			if (executor != null) {
				executor.shutdownNow();
				executor = null;
			}
			//System.out.println("All done, closing the session.  ");
			// notify anyone who cares that session is now closed
			this.processing.compareAndSet(true, false);
			setStatus(SessionStatus.CLOSED);
		}
	}

	/**
	 * 
	 */
	public JmsTemplate getTemplate() {
		return template;
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
		CommPortIdentifier portIdentifier = null;

		public static final byte END_OF_TEXT = 0x03;
		public static final byte START_OF_TEXT = 0x02;

		private AcuraProXReaderSession session;

		/**
		 * 
		 */
		public OneWaySerialComm(AcuraProXReaderSession session) {
			// super();
			this.session = session;
		}

		/**
		 * Connect the
		 * 
		 * @param portName
		 * @throws Exception
		 */
		public void connect(String portName) throws Exception {
			portIdentifier = CommPortIdentifier
					.getPortIdentifier(portName);
			if (portIdentifier.isCurrentlyOwned()) {
				logger.warn("Error: Port is currently in use");
			} else {
				commPort = portIdentifier.open(this.getClass().getName(), 2000);

				if (commPort instanceof SerialPort) {
					SerialPort serialPort = (SerialPort) commPort;
					serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,
							SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

					InputStream in = serialPort.getInputStream();

					(new Thread(new SerialReader(in, session))).start();
				} else {
					logger.warn("Error: Port is not a serial port.");
				}
			}
		}

		/**
		 * Close the connection for the comm port.
		 */
		public void closeConnection() {
			try {
				SerialPort serialPort = (SerialPort) commPort;
				serialPort.getInputStream().close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				commPort.close();
				//commPort = null;
			}
		}

		/**
		 * 
		 */
		public class SerialReader implements Runnable {
			InputStream in;

			AcuraProXTagHandler tagHandler = null;

			public SerialReader(InputStream in, AcuraProXReaderSession session) {
				this.in = in;
				tagHandler = new AcuraProXTagHandler(session, readerID);
			}

			public void run() {
				byte[] buffer = new byte[16];
				int current = -1;
				try {
					List<Byte> byteBuffer = new ArrayList<Byte>();
					while ((current = this.in.read(buffer)) > -1) {
						if (current != 0) {
							for (int i = 0; i < current; i++) {
								if (byteBuffer.isEmpty()) {
									if (buffer[i] == 0x02) {
										byteBuffer.add(buffer[i]);
									}
								} else {
									byteBuffer.add(buffer[i]);
								}
								if (byteBuffer.size() == 14) {
									// System.out.println("The bytes:");
									// for(Byte b:byteBuffer) {
									// System.out.print(b + " ");
									// }
									// System.out.println();
									List<Byte> tempBuffer = new ArrayList<Byte>();
									for (int x = 1; x < 11; x++) {
										tempBuffer.add(byteBuffer.get(x));
									}
									tagHandler.processTag(tempBuffer);
									byteBuffer.clear();
								}
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

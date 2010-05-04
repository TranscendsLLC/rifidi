/*
 *  AbstractSerialSensorSession.java
 *
 *  Created:	May 3, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.core.sensors.sessions;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.jms.Destination;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.sensors.messages.ByteMessage;
import org.rifidi.edge.core.sensors.sessions.poll.QueueingMessageProcessingStrategy;
import org.springframework.jms.core.JmsTemplate;

/**
 * This class represents a session with a Serial reader.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public abstract class AbstractSerialSensorSession extends AbstractSensorSession {

	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(AbstractSerialSensorSession.class);

	/** Thread for reading from the socket. */
	@SuppressWarnings("unused")
	private SerialReader readObject;
	/** Thread for writing to the socket. */
	private Thread writeThread;

	/** The serial port we will talk to. */
	private CommPort commPort = null;
	/** The ID for the serial port */
	private CommPortIdentifier portIdentifier = null;

	/** Queue for writing messages. */
	private LinkedBlockingQueue<ByteMessage> readQueue = new LinkedBlockingQueue<ByteMessage>();
	/** Queue for writing messages. */
	private LinkedBlockingQueue<ByteMessage> writeQueue = new LinkedBlockingQueue<ByteMessage>();

	/** The factory to produce MessageProcessingStrategy objects */
	private final QueueingMessageProcessingStrategyFactory qmpsf;

	// Info for creating the serial port:
	private String commPortName = null;
	private int baud, databits, stopbits, parity;

	/**
	 * 
	 * @param sensor
	 * @param ID
	 * @param destination
	 * @param template
	 * @param commandConfigurations
	 */
	public AbstractSerialSensorSession(AbstractSensor<?> sensor, String ID,
			Destination destination, JmsTemplate template,
			Set<AbstractCommandConfiguration<?>> commandConfigurations,
			String commPortName, int baud, int databits, int stopbits,
			int parity) {
		super(sensor, ID, destination, template, commandConfigurations);
		this.baud = baud;
		this.databits = databits;
		this.stopbits = stopbits;
		this.parity = parity;
		this.qmpsf = new QueueingMessageProcessingStrategyFactory(readQueue);
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
			this.connectSerial(commPortName);
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

	/**
	 * Get a factory for MessageParsingStrategy objects
	 * 
	 * @return
	 */
	protected abstract MessageParsingStrategyFactory getMessageParsingStrategyFactory();

	/**
	 * Handles the connection logic for setting things to the serial port.
	 * 
	 * @param portName
	 */
	private void connectSerial(String portName) throws PortInUseException,
			NoSuchPortException, UnsupportedCommOperationException, IOException {
		portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
		if (portIdentifier.isCurrentlyOwned()) {
			logger.warn("Error: Port is currently in use");
			throw new PortInUseException();
		} else {
			commPort = portIdentifier.open(this.getClass().getName(), 2000);

			if (commPort instanceof SerialPort) {
				SerialPort serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(this.baud, this.databits,
						this.stopbits, this.parity);

				// The InputStream to read data from the serial reader.
				InputStream in = serialPort.getInputStream();
				// The OutputStream to write data to the reader.
				OutputStream out = serialPort.getOutputStream();
				// Start the thread
				this.readObject = new SerialReader(in, this
						.getMessageParsingStrategyFactory(), this.qmpsf
						.createMessageProcessor());
				this.writeThread = new Thread(new SerialWriteThread(out,
						this.writeQueue));
			} else {
				logger.warn("Error: Port is not a serial port.");
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.sensors.SensorSession#disconnect()
	 */
	@Override
	public void disconnect() {
		try {
			// Should we do anything with the SerialReader here?
			this.writeThread.join();
		} catch (InterruptedException e) {
			// Don't care
		}
		try {
			this.commPort.getInputStream().close();
			this.commPort.getOutputStream().close();
		} catch (IOException e) {
			// Don't care
		} finally {
			// Close the serial port
			this.commPort.close();
		}

	}

	/**
	 * This method gets called when a message is received from the reader.  
	 * 
	 * @param message
	 */
	public abstract void messageReceived(ByteMessage message);
	
	/**
	 * Send a message over the line. This method is protected so that subclasses
	 * can choose whether or not to expose it to clients.
	 * 
	 * @param o
	 * @throws IOException
	 */
	protected void sendMessage(ByteMessage message) throws IOException {
		writeQueue.add(message);
	}

	/**
	 * Read data from the serial port.this.readThread = new Thread(new
	 * SerialReadThread(in, this .getMessageParsingStrategyFactory(), this
	 * .getMessageProcessingStrategyFactory()));
	 * 
	 * @author Matthew Dean - matt@pramari.com
	 */
	public class SerialReader implements SerialPortEventListener {

		/** The InputStream that comes in from the reader. */
		InputStream in = null;

		/** Message Parser used in this thread */
		private MessageParsingStrategy messageParser;
		/** Message Processor to be used in this thread */
		private MessageProcessingStrategy messageProcessor;

		/**
		 * The thread to read data from the serial port.
		 * 
		 * @param in
		 *            The InputStream that comes in from the reader.
		 */
		public SerialReader(InputStream in,
				MessageParsingStrategyFactory messageParserFactory,
				MessageProcessingStrategy messageProcessor) {
			this.in = in;
			this.messageParser = messageParserFactory.createMessageParser();
			this.messageProcessor = messageProcessor;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * gnu.io.SerialPortEventListener#serialEvent(gnu.io.SerialPortEvent)
		 */
		@Override
		public void serialEvent(SerialPortEvent ev) {
			int data;

			try {
				while ((data = in.read()) > -1) {
					byte[] message = messageParser.isMessage((byte) data);
					if (message != null) {
						this.messageProcessor.processMessage(message);
						messageReceived(new ByteMessage(message));
						break;
					}
				}

			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
	}

	/**
	 * Write data to the serial port.
	 * 
	 * @author Matthew Dean - matt@pramari.com
	 */
	public class SerialWriteThread implements Runnable {

		OutputStream out = null;
		/** Reference to the message queue. */
		private BlockingQueue<ByteMessage> messageQueue;

		/**
		 * The thread to write data to a serial port.
		 * 
		 * @param out
		 */
		public SerialWriteThread(OutputStream out,
				BlockingQueue<ByteMessage> messageQueue) {
			this.out = out;
			this.messageQueue = messageQueue;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			logger.debug("Starting Write Thread");
			try {
				while (!Thread.interrupted()) {
					ByteMessage m = messageQueue.take();
					out.write(m.message);
					out.flush();
				}
			} catch (IOException e) {
				logger.error(e);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	/**
	 * FIXME: Stolen from AbstractPollIP. We should make this into a full class
	 * instead of an inner class - Matt
	 * 
	 * @author Kyle Neumeier - kyle@pramari.com
	 */
	private class QueueingMessageProcessingStrategyFactory implements
			MessageProcessingStrategyFactory {

		/** The queue to put new objects on */
		private final Queue<ByteMessage> queue;

		public QueueingMessageProcessingStrategyFactory(Queue<ByteMessage> queue) {
			this.queue = queue;
		}

		@Override
		public MessageProcessingStrategy createMessageProcessor() {
			return new QueueingMessageProcessingStrategy(queue);
		}

	}

}

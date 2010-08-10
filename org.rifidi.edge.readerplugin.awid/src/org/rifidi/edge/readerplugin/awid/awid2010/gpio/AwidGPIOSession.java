/**
 * 
 */
package org.rifidi.edge.readerplugin.awid.awid2010.gpio;

import java.io.IOException;
import java.util.BitSet;
import java.util.HashSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.sensors.exceptions.CannotExecuteException;
import org.rifidi.edge.core.sensors.messages.ByteMessage;
import org.rifidi.edge.core.sensors.sessions.MessageParsingStrategy;
import org.rifidi.edge.core.sensors.sessions.MessageParsingStrategyFactory;
import org.rifidi.edge.core.sensors.sessions.pubsub.AbstractPubSubIPSensorSession;
import org.rifidi.edge.core.sensors.sessions.pubsub.IPSessionEndpoint;
import org.rifidi.edge.readerplugin.awid.awid2010.communication.commands.AbstractAwidCommand;

/**
 * This is a Session that is used for the GPIO functionality for the AWID TCP/IP
 * MPR reader. The AWID reader uses a separate software unit for GPI/O from the
 * unit that handles tag reads. The GPI/O process runs on port 4001 on the
 * reader, so a separate TCP/IP session is needed. The AWID has four input lines
 * and four output lines.
 * 
 * The AWID reader will send back asynchronous notifications when an input line
 * changes state. For this reason, this session extends
 * AbstractPubSubIPSensorSession, which allows this session to process incoming
 * messages as they happen. These notification events are given to esper.
 * 
 * There are also three request/response type messages that are available
 * (namely 'Output Command', 'Flash On/Off Control', and 'GPIO Status'). So this
 * session first checks if the incoming message is a GPI notification. If it is,
 * it will give the notification event to esper. If it is not, it will queue the
 * message so that the process that sent the request can call receiveMessage to
 * get the response.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AwidGPIOSession extends AbstractPubSubIPSensorSession implements
		IPSessionEndpoint {

	private static final Log logger = LogFactory.getLog(AwidGPIOSession.class);

	/**
	 * The current status of the GPI ports. This object is shared, so make sure
	 * to synchronize before accessing
	 */
	private final BitSet GPIPorts = new BitSet(4);
	/** Queue for messages that are not GPI Notification messages. */
	private final LinkedBlockingQueue<ByteMessage> messageQueue;

	/**
	 * Create a new session for interacting with the AWID reader's GPIO module
	 * 
	 * @param sensor
	 * @param ID
	 * @param host
	 * @param port
	 */
	public AwidGPIOSession(AbstractSensor<?> sensor, String ID, String host,
			int port) {
		super(sensor, ID, host, port, 1000, 3,
				new HashSet<AbstractCommandConfiguration<?>>());
		messageQueue = new LinkedBlockingQueue<ByteMessage>();
	}

	/**
	 * 
	 * @param ports
	 * @throws CannotExecuteException
	 */
	public void setOutputPort(BitSet ports) throws CannotExecuteException {
		try {
			SetOutputPortCommand command = new SetOutputPortCommand(ports);
			command.setReaderSession(this);
			super.submitAndBlock(command, 5, TimeUnit.SECONDS);
		} catch (Exception e) {
			throw new CannotExecuteException(e);
		}

	}

	/**
	 * 
	 * @param port
	 * @return
	 * @throws CannotExecuteException
	 */
	public boolean testInputPort(int port) throws CannotExecuteException {
		try {
			TestInputPortsCommand command = new TestInputPortsCommand(GPIPorts);
			command.setReaderSession(this);
			super.submitAndBlock(command, 5, TimeUnit.SECONDS);
			return GPIPorts.get(port);
		} catch (Exception e) {
			throw new CannotExecuteException(e);
		}
	}

	/**
	 * A custom Awid GPIO command that flashes a line high for a given amount of
	 * time and a given number of times
	 * 
	 * @param pin
	 *            The line to set
	 * @param onTime
	 *            The amount of time in 100Ms to set high
	 * @param offTime
	 *            the amount of time in 100Ms to set low
	 * @param totalCount
	 *            The number of times to flash.
	 */
	private void flashOutputPort(byte pin, byte onTime, byte offTime,
			byte totalCount) throws CannotExecuteException {
		GPOFlashCommand awidCommand = new GPOFlashCommand(pin, onTime, offTime,
				totalCount);
		FlashControlCommand command = new FlashControlCommand(awidCommand);
		command.setReaderSession(this);
		super.submit(command);
	}

	/**
	 * 
	 * @param ports
	 * @param finalPorts
	 * @param timeOn
	 * @param timeOff
	 * @param repeat
	 * @throws CannotExecuteException
	 */
	public void flashOutput(final BitSet ports, final BitSet finalPorts,
			final int timeOn, final int timeOff, final int repeat)
			throws CannotExecuteException {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					for (int i = 0; i < 4; i++) {
						if (ports.get(i)) {
							flashOutputPort((byte) i, (byte) timeOn,
									(byte) timeOff, (byte) repeat);
						}
					}
					setOutputPort(finalPorts);
				} catch (CannotExecuteException e) {
					logger.warn("Cannot Flash Output: " + e.getMessage());
				}
			}
		});
		t.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.sessions.AbstractIPSensorSession#sendMessage
	 * (org.rifidi.edge.core.sensors.messages.ByteMessage)
	 */
	public void sendMessage(AbstractAwidCommand message) throws IOException {
		super.sendMessage(new ByteMessage(message.getCommand()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.sensors.sessions.AbstractIPSensorSession#
	 * getMessageParsingStrategyFactory()
	 */
	@Override
	protected MessageParsingStrategyFactory getMessageParsingStrategyFactory() {
		return new MessageParsingStrategyFactory() {

			@Override
			public MessageParsingStrategy createMessageParser() {
				return new AwidGPIOMessageParsingStrategy();
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.sessions.AbstractIPSensorSession#onConnect()
	 */
	@Override
	protected boolean onConnect() throws IOException {
		super.subscribe(this);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.sessions.AbstractIPSensorSession#disconnect
	 * ()
	 */
	@Override
	public void disconnect() {
		super.disconnect();
		super.unsubscribe(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.sessions.pubsub.IPSessionEndpoint#handleMessage
	 * (org.rifidi.edge.core.sensors.messages.ByteMessage)
	 */
	@Override
	public void handleMessage(ByteMessage message) {
		// first check to see if the message is a GPI Notification
		if (isInputChangedMessage(message)) {
			// TODO: put GPIEvent on esper
		} else {
			// if it's not, put the message on the queue.
			messageQueue.add(message);
		}
	}

	/**
	 * Receive a message. This method blocks for the given amount of time. If
	 * the time expires the method will return.
	 * 
	 * @param timeout
	 *            in milisecons
	 * @return The Message that was recieved
	 * @throws IOException
	 *             If there was a problem recieving the message or if a Timeout
	 *             happened.
	 */
	public ByteMessage receiveMessage(long timeout) throws IOException {
		try {
			return messageQueue.poll(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IOException();
		}
	}

	/**
	 * A helper method to check if the incoming message is a "Input Response"
	 * Message from the Awid reader informing us that a GPI has changed states.
	 * 
	 * The messages look like this:
	 * 
	 * [length][0xFF][pin][hi][crc][crc]
	 * 
	 * Because the second byte (0xFF) is the same as the responses to 'Output
	 * command' and 'flash on/off control', we have to look at the third byte.
	 * If the third byte is 0,1,2,or 3, then the message is an Input Response.
	 * Otherwise, it is another kind of response message.
	 * 
	 * @param The
	 *            message received from the AWID reader
	 * @return true if the message is an input changed message.
	 */
	private boolean isInputChangedMessage(ByteMessage message) {
		if (message.message.length == 6) {
			if (message.message[1] == (byte) 0xFF) {
				if (message.message[2] == (byte) 0x00
						|| message.message[2] == (byte) 0x01
						|| message.message[2] == (byte) 0x02
						|| message.message[2] == (byte) 0x03) {
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.sessions.AbstractIPSensorSession#toString()
	 */
	@Override
	public String toString() {
		return "[GPIO Session " + super.toString() + "]";
	}
}

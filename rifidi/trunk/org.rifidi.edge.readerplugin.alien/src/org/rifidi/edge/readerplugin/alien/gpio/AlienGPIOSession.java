/**
 * 
 */
package org.rifidi.edge.readerplugin.alien.gpio;

import java.io.IOException;
import java.util.HashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.sensors.exceptions.CannotExecuteException;
import org.rifidi.edge.core.sensors.sessions.AbstractServerSocketSensorSession;
import org.rifidi.edge.core.sensors.sessions.MessageParsingStrategy;
import org.rifidi.edge.core.sensors.sessions.MessageParsingStrategyFactory;
import org.rifidi.edge.core.sensors.sessions.MessageProcessingStrategy;
import org.rifidi.edge.core.sensors.sessions.MessageProcessingStrategyFactory;
import org.rifidi.edge.readerplugin.alien.Alien9800Reader;
import org.rifidi.edge.readerplugin.alien.Alien9800ReaderSession;
import org.rifidi.edge.readerplugin.alien.AlienMessageParsingStrategyFactory;
import org.rifidi.edge.readerplugin.alien.gpio.messages.AlienIOListMessageParsingStrategy;

/**
 * @author kyle
 * 
 */
public class AlienGPIOSession extends AbstractServerSocketSensorSession {

	private AlienIOListMessageParsingStrategy IOListParsingStrategy;
	private int initialExternalInputValue = 0;
	private int initialExternalOutputValue = 0;
	private boolean hasBeenInitialized = false;
	private Log logger = LogFactory.getLog(AlienGPIOSession.class);

	public AlienGPIOSession(AbstractSensor<?> sensor, String ID,
			int serverSocketPort,
			AlienIOListMessageParsingStrategy parsingStrategy) {
		super(sensor, ID, null, null, serverSocketPort, 1,
				new HashSet<AbstractCommandConfiguration<?>>());
		this.IOListParsingStrategy = parsingStrategy;
	}

	/**
	 * Initialize must be called before connect can be called
	 * 
	 * @param interactiveSession
	 * @throws CannotExecuteException
	 *             if there was a problem when the initialization was attempted.
	 */
	public void initialize(Alien9800ReaderSession interactiveSession)
			throws CannotExecuteException {
		this.initialExternalInputValue = interactiveSession.getExternalInput();
		this.hasBeenInitialized = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.sessions.AbstractServerSocketSensorSession
	 * #_connect()
	 */
	@Override
	protected void _connect() throws IOException {
		if (!hasBeenInitialized) {
			throw new IOException("GPIO Session must be initialized");
		}
		super._connect();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.sessions.AbstractServerSocketSensorSession
	 * #disconnect()
	 */
	@Override
	public void disconnect() {
		super.disconnect();
		this.hasBeenInitialized = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.sessions.AbstractServerSocketSensorSession
	 * #getMessageParsingStrategyFactory()
	 */
	@Override
	protected MessageParsingStrategyFactory getMessageParsingStrategyFactory() {
		return new MessageParsingStrategyFactory() {

			@Override
			public MessageParsingStrategy createMessageParser() {
				return new AlienGPIOMessageParsingStrategy();
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.sessions.AbstractServerSocketSensorSession
	 * #getMessageProcessingStrategyFactory()
	 */
	@Override
	protected MessageProcessingStrategyFactory getMessageProcessingStrategyFactory() {
		return new MessageProcessingStrategyFactory() {
			@Override
			public MessageProcessingStrategy createMessageProcessor() {
				return new AlienGPIOMessageProcessingStrategy(
						IOListParsingStrategy, (Alien9800Reader) getSensor(),
						initialExternalInputValue, initialExternalOutputValue);
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.sessions.AbstractServerSocketSensorSession
	 * #toString()
	 */
	@Override
	public String toString() {
		return "[GPIO Session " + super.toString() + "]";
	}

}

/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
package org.rifidi.edge.adapter.generic;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.notification.NotifierService;
import org.rifidi.edge.notification.ReadCycle;
import org.rifidi.edge.notification.TagReadEvent;
import org.rifidi.edge.sensors.AbstractCommandConfiguration;
import org.rifidi.edge.sensors.AbstractSensor;
import org.rifidi.edge.sensors.sessions.AbstractServerSocketSensorSession;
import org.rifidi.edge.sensors.sessions.MessageParsingStrategy;
import org.rifidi.edge.sensors.sessions.MessageParsingStrategyFactory;
import org.rifidi.edge.sensors.sessions.MessageProcessingStrategy;
import org.rifidi.edge.sensors.sessions.MessageProcessingStrategyFactory;

/**
 * The session class for the Generic sensor.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class GenericSensorSession extends AbstractServerSocketSensorSession {

	/** Logger for this class. */
	// private static final Log logger = LogFactory
	// .getLog(GenericSensorSession.class);

	/** Service used to send out notifications */
	private volatile NotifierService notifierService;

	private GenericSensorSessionTagHandler tagHandler = null;

	/** The ID for the reader. */
	private String readerID = null;
	
	//rest
	private Integer restPort;
	
	//mqtt
	private Integer mqttPort;
	private MqttClient mqttclient;
	private String mqttURI;
	private String mqttClientId;
	

	public GenericSensorSession(AbstractSensor<?> sensor, String ID,
			NotifierService notifierService, String readerID,
			int serverSocketPort, int restPort, int mqttPort,
			Set<AbstractCommandConfiguration<?>> commandConfigurations) {
		super(sensor, ID, serverSocketPort, 10, commandConfigurations);
		this.readerID = readerID;
		this.notifierService = notifierService;
		this.tagHandler = new GenericSensorSessionTagHandler(readerID);
		this.restPort = restPort;
		this.mqttPort = mqttPort;
	}
	
	public void startMqttService() {
		try {
			this.mqttclient = new MqttClient(mqttURI, mqttClientId);
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readers.impl.AbstractReaderSession#setStatus(org
	 * .rifidi.edge.core.api.SessionStatus)
	 */
	@Override
	protected void setStatus(SessionStatus status) {
		super.setStatus(status);
		// TODO: Remove this once we have aspectJ
		notifierService.sessionStatusChanged(this.readerID, this.getID(),
				status);
	}

	/**
	 * Parses and sends the tag to the desired destination.
	 * 
	 * @param tag
	 */
	public void sendTag(byte[] message) {
		TagReadEvent event = this.tagHandler.parseTag(new String(message));

		Set<TagReadEvent> tres = new HashSet<TagReadEvent>();
		tres.add(event);
		ReadCycle cycle = new ReadCycle(tres, readerID, System
				.currentTimeMillis());

		this.getSensor().send(cycle);
		
		//TODO: SEND TAGS
		//this.template.send(this.template.getDefaultDestination(),
		//		new ReadCycleMessageCreator(cycle));
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.sessions.AbstractServerSocketSensorSession
	 * #getMessageParsingStrategyFactory()
	 */
	@Override
	protected MessageParsingStrategyFactory getMessageParsingStrategyFactory() {
		return new MessageParsingStrategyFactory() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.rifidi.edge.sensors.sessions.MessageParsingStrategyFactory
			 * #createMessageParser()
			 */
			@Override
			public MessageParsingStrategy createMessageParser() {
				return new MessageParsingStrategy() {

					List<Byte> messageList = new LinkedList<Byte>();

					/*
					 * (non-Javadoc)
					 * 
					 * @see
					 * org.rifidi.edge.sensors.sessions.MessageParsingStrategy
					 * #isMessage(byte)
					 */
					@Override
					public byte[] isMessage(byte message) {
						if (message == '\n') {
							byte[] retVal = new byte[messageList.size()];
							int index = 0;
							for (Byte b : messageList) {
								retVal[index] = b;
								index++;
							}
							return retVal;
						}
						messageList.add(message);
						return null;
					}
				};
			}

		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.sessions.AbstractServerSocketSensorSession
	 * #getMessageProcessingStrategyFactory()
	 */
	@Override
	protected MessageProcessingStrategyFactory getMessageProcessingStrategyFactory() {
		return new GenericMessageProcessingStrategyFactory(this);
	}

	/**
	 * 
	 * @author Matthew Dean - matt@pramari.com
	 */
	private class GenericMessageProcessingStrategyFactory implements
			MessageProcessingStrategyFactory {

		private GenericSensorSession session = null;

		public GenericMessageProcessingStrategyFactory(
				GenericSensorSession session) {
			this.session = session;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.rifidi.edge.sensors.sessions.MessageProcessingStrategyFactory
		 * #createMessageProcessor()
		 */
		@Override
		public MessageProcessingStrategy createMessageProcessor() {
			return new GenericMessageProcessingStrategy(session);
		}

	}

	/*
	 * Message processing strategy for the GenericSensorSession. This class will
	 * take in a pipe-delimited message, parse out the expected values: ID:(tag
	 * id), Antenna:(antenna tag was last seen on), and Timestamp:(milliseconds
	 * since epoch expected).
	 * 
	 * Anything else will go in the extrainformation hashmap as a key:value pair
	 * separated by a colon.
	 */
	private class GenericMessageProcessingStrategy implements
			MessageProcessingStrategy {

		private GenericSensorSession session = null;

		public GenericMessageProcessingStrategy(GenericSensorSession session) {
			this.session = session;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.rifidi.edge.sensors.sessions.MessageProcessingStrategy#
		 * processMessage(byte[])
		 */
		@Override
		public void processMessage(byte[] message) {
			this.session.sendTag(message);
		}
	}

}

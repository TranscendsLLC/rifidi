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

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.rifidi.edge.adapter.generic.dtos.GenericTagDTO;
import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.notification.EPCGeneration2Event;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * The session class for the Generic sensor.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class GenericSensorSession extends AbstractServerSocketSensorSession implements MqttCallback {

	/** Logger */
	private final Log logger = LogFactory.getLog(getClass());

	/** Service used to send out notifications */
	private volatile NotifierService notifierService;

	private GenericSensorSessionTagHandler tagHandler = null;

	/** The ID for the reader. */
	private String readerID = null;
	
	//mqtt
//	private Integer mqttPort;
//	private MqttClient mqttclient;
//	private String mqttURI;
//	private String mqttClientId;
//	private String mqttTopic;
	
	private Boolean restdebug;
	
	private GenericSensor sensor;
	
	//int mqttPort, String mqttURI, String mqttClientId, String mqttTopic,
	public GenericSensorSession(AbstractSensor<?> sensor, String ID,
			NotifierService notifierService, String readerID,
			int serverSocketPort, Boolean restdebug, Set<AbstractCommandConfiguration<?>> commandConfigurations) {
		super(sensor, ID, serverSocketPort, 10, commandConfigurations);
		this.readerID = readerID;
		this.notifierService = notifierService;
		this.tagHandler = new GenericSensorSessionTagHandler(readerID);
//		this.mqttPort = mqttPort;
//		this.mqttURI = mqttURI;
//		this.mqttClientId = mqttClientId;
//		this.mqttTopic = mqttTopic;
		
		this.restdebug=restdebug;
		
		this.sensor = (GenericSensor) sensor;
	}
	
//	public void startMqttService() {
//		try {
//			this.mqttclient = new MqttClient(this.mqttURI, this.mqttClientId);
//			this.mqttclient.connect();
//			this.mqttclient.subscribe(mqttTopic);
//			this.mqttclient.setCallback(this);
//		} catch (MqttException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	

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
	
	/**
	 * Parses and sends the tag to the desired destination.
	 * 
	 * @param tag
	 */
	public void sendTags(Set<TagReadEvent> tres) {
		ReadCycle cycle = new ReadCycle(tres, readerID, System.currentTimeMillis());
		this.getSensor().send(cycle);
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

	@Override
	public void connectionLost(Throwable arg0) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void messageArrived(String arg0, MqttMessage arg1) throws Exception {
		String message = new String(arg1.getPayload());
		this.sendTags(this.processTags(message));
	}
	
	public Set<TagReadEvent> processTags(String json) throws IOException{
		Gson gson = new Gson();
		Type type = new TypeToken<List<GenericTagDTO>>(){}.getType();
		List<GenericTagDTO> dtolist = gson.fromJson(json, type);
		return dtoToTagReadEventSet(dtolist);
	}
	
	private Set<TagReadEvent> dtoToTagReadEventSet(List<GenericTagDTO> dtoList) {
		Set<TagReadEvent> retval = new HashSet<TagReadEvent>();
		for(GenericTagDTO dto:dtoList) {
			EPCGeneration2Event gen2event = new EPCGeneration2Event();
			String val = dto.getEpc();
			int numbits = val.length() * 4;
			BigInteger epc;
			try {
				epc = new BigInteger(val, 16);
			} catch (Exception e) {
				throw new RuntimeException("Cannot decode ID: " + val);
			}
			gen2event.setEPCMemory(epc, val, numbits);
			TagReadEvent tag = new TagReadEvent(dto.getReader(), gen2event, dto.getAntenna(), dto.getTimestamp());
			if (dto.getRssi() != null && dto.getRssi()!="") {
				tag.addExtraInformation("RSSI", dto.getRssi());
			}
			if (dto.getExtrainformation() != null && dto.getExtrainformation() != "") {
				String[] pairs = dto.getExtrainformation().split("\\|");
				for(String s:pairs) {
					String[] kv = s.split(":");
					String key = kv[0];
					String value = kv[1];
					tag.addExtraInformation(key, value);
				}
			}
			if(restdebug) {
				logger.info("Adding a tag through REST: " + tag.toString());
			}
			retval.add(tag);
		}
		return retval;
	}

}

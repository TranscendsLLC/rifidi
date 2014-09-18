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
package org.rifidi.app.rifidiservices.subscriber;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.rifidi.app.rifidiservices.RifidiServicesApp;
import org.rifidi.app.rifidiservices.dto.StableSetMessageDto;
import org.rifidi.app.rifidiservices.dto.TagBatchSeenMessageDto;
import org.rifidi.edge.api.service.tagmonitor.ReadZone;
import org.rifidi.edge.api.service.tagmonitor.ReadZoneSubscriber;
import org.rifidi.edge.api.service.tagmonitor.StableSetSubscriber;
import org.rifidi.edge.api.service.tagmonitor.UniqueTagBatchIntervalSubscriber;
import org.rifidi.edge.notification.TagReadEvent;

/**
 * The subscriber class. This class monitors the stableset station tags arrived
 * events for reader.
 * 
 * @author Manuel Tobon - manuel@transcends.co
 */
public class MyUniqueTagBatchSubscriber 
		implements UniqueTagBatchIntervalSubscriber {

	/**
	 * Reference to application of this subscriber to notify for example when to
	 * unsubscribe this subscriber and stop reader session when stableset has
	 * reached
	 **/
	private RifidiServicesApp rifidiServicesApp;

	/** The read zone for this subscriber **/
	private ReadZone readZone;

	/** Logger for this class */
	private final Log logger = LogFactory.getLog(getClass());
	
	/** The topic name to post messages from this subscriber **/
	private String topicName = "myTopicTagBatchSeen";

	/**
	 * You can use this kind of constructor where you pass the necessary
	 * parameters
	 **/

	/**
	 * Constructor
	 * 
	 * @param rifidiServicesApp
	 *            the application to associate to this subscriber
	 * @param readZone
	 *            the readzone instance associated with this subscriber
	 */
	public MyUniqueTagBatchSubscriber(RifidiServicesApp rifidiServicesApp, ReadZone readZone) {

		this.rifidiServicesApp = rifidiServicesApp;
		this.readZone = readZone;

	}

	@Override
	public void tagBatchSeen(Set<TagReadEvent> tagReadEventSet) {

		logger.info("TAG BATCH SEEN: size: " + tagReadEventSet.size());

		// You can unsubscribe this subscriber using the app reference
		rifidiServicesApp.unsubscribeFromUniqueTagBatchIntervalService(this);
		 
		logger.debug("unsubscribed the subscriber for reader id: " + getReadZone().getReaderID());
		 
		// Create a new tagBatchSeenMessageDto instance, for example, to create an xml representation
		// of tag list, using annotations -see StableSetMessageDto
		TagBatchSeenMessageDto tagBatchSeenMessageDto = new TagBatchSeenMessageDto();
		tagBatchSeenMessageDto.setTimeStamp(new Date().getTime());
		 
		 //Maybe these tags are read by several readers in this read event (in case that
		 //the readzone maps to multiple readers), so you can keep track of all readers 
		 //in a set as long as you iterate over tags
		 //you can use a Set, that is a Collection that contains no duplicates
		 Set<String> readerIdSet = new HashSet<String>(); 
		 
		// Iterate over the tags
		for (TagReadEvent tag : tagReadEventSet) {
			
			//Extract the reader id to keep in a set
			readerIdSet.add(tag.getReaderID());

			// add this tag to the list of tags inside
			// stableSetMessageDto object

			tagBatchSeenMessageDto.addTag(tag.getTag().getFormattedID());
			logger.debug("tag added to tag list of reader id " + tag.getReaderID()
					+ ": " + tag.getTag().getFormattedID());

		}

		//You can publish the list of tags to queue server like mqtt
		publishToQueue(tagBatchSeenMessageDto);
		logger.info("published to queue tagBatchSeenMessageDto: "
					+ tagBatchSeenMessageDto);


		//You can stop the session for every reader
		for (String readerID : readerIdSet) {
			this.rifidiServicesApp.stopReaderSession(readerID);
			logger.debug("info session for reader id: " + readerID);
		}

		try {

			Thread.sleep(500);

		} catch (InterruptedException e) {
			// Don't care
		}

		// Subscribe
		rifidiServicesApp.subscribeToStableSetService(readZone);
		logger.debug("subscribed the subscriber for reader id: "
				+ getReadZone().getReaderID());

	}

	/**
	 * @return the readZone
	 */
	public ReadZone getReadZone() {
		return readZone;
	}

	/**
	 * @param readZone
	 *            the readZone to set
	 */
	public void setReadZone(ReadZone readZone) {
		this.readZone = readZone;
	}
	
	

	/**
	 * @return the topicName
	 */
	public String getTopicName() {
		return topicName;
	}

	/**
	 * @param topicName the topicName to set
	 */
	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	/**
	 * Publish a message to the broker
	 * 
	 * @param tagBatchSeenMessageDto
	 *            the object from which we are going to generate xml string
	 *            representation and send to broker
	 */
	private void publishToQueue(TagBatchSeenMessageDto tagBatchSeenMessageDto) {

		try {

			JAXBContext jaxbContext = JAXBContext
					.newInstance(TagBatchSeenMessageDto.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);

			// Create xml String and send to server using mqttClient
			Writer writer = new StringWriter();
			jaxbMarshaller.marshal(tagBatchSeenMessageDto, writer);
			String content = writer.toString();
			writer.close();

			logger.info("Publishing message: " + content);
			MqttMessage message = new MqttMessage(content.getBytes());
			message.setQos(rifidiServicesApp.getMqttQos());
			
			try {
				
				rifidiServicesApp.getMqttClient().connect();
	            logger.info("Connected to broker: " + rifidiServicesApp.getMqttClient().getServerURI());
	            
			} catch(MqttException mEx){
				
				logger.error("Error connecting to broker", mEx);
				throw new RuntimeException(mEx);
				
			}
            
            try {

            	rifidiServicesApp.getMqttClient().publish(getTopicName(), message);
            	logger.info("Message published");
            	
            } catch (MqttException mEx) {

        			logger.error("Error publishing to queue", mEx);
        			throw new RuntimeException(mEx);

        	}
			
			try {
				
				rifidiServicesApp.getMqttClient().disconnect();
				logger.info("mqttClient disconnected.");
				
			} catch (MqttException mEx){
				
				logger.error("Error trying to disconnect mqttClient", mEx);
				throw new RuntimeException(mEx);
				
			}

		} 
		
		catch (JAXBException jEx) {

			logger.error("Error publishing to queue", jEx);
			throw new RuntimeException(jEx);

		} catch (IOException ioEx) {

			logger.error("Error publishing to queue", ioEx);
			throw new RuntimeException(ioEx);

		}

	}


}

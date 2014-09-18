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
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.rifidi.app.rifidiservices.RifidiServicesApp;
import org.rifidi.app.rifidiservices.dto.TagMessageDto;
import org.rifidi.edge.api.service.tagmonitor.ReadZone;
import org.rifidi.edge.api.service.tagmonitor.ReadZoneSubscriber;
import org.rifidi.edge.notification.TagReadEvent;

/**
 * The subscriber class. This class monitors the arrived and departed events for
 * reader.
 * 
 * @author Manuel Tobon - manuel@transcends.co
 */
public class MyReadZoneSubscriber implements ReadZoneSubscriber {

	/**
	 * Reference to application of this subscriber
	 **/
	private RifidiServicesApp rifidiServicesApp;

	/** Logger for this class */
	private final Log logger = LogFactory.getLog(getClass());
	

	/**
	 * Constructor
	 * 
	 * @param rifidiServicesApp
	 *            the application to associate to this subscriber
	 */
	public MyReadZoneSubscriber(RifidiServicesApp rifidiServicesApp) {

		this.rifidiServicesApp = rifidiServicesApp;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.api.service.tagmonitor.ReadZoneSubscriber#tagArrived(
	 * org.rifidi.edge.notification.TagReadEvent)
	 */
	@Override
	public void tagArrived(TagReadEvent tag) {

		logger.info("TAG ARRIVED: " + tag.getTag().getFormattedID());

		TagMessageDto tagMessageDto = new TagMessageDto();
		tagMessageDto.setTag(tag.getTag().getFormattedID());
		tagMessageDto.setTimeStamp(tag.getTimestamp());
		tagMessageDto.setStationId(tag.getReaderID());

		try {

			JAXBContext jaxbContext = JAXBContext
					.newInstance(TagMessageDto.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);

			// Create xml String and send to server using mqttClient
			Writer writer = new StringWriter();
			jaxbMarshaller.marshal(tagMessageDto, writer);
			String content = writer.toString();
			writer.close();

			logger.info("Publishing message: " + content);
			MqttMessage message = new MqttMessage(content.getBytes());
			message.setQos(rifidiServicesApp.getMqttQos());

			String mqttTopic = tag.getReaderID();

			try {

				rifidiServicesApp.getMqttClient().connect();
				logger.info("Connected to broker: "
						+ rifidiServicesApp.getMqttClient().getServerURI());

			} catch (MqttException mEx) {

				logger.error("Error connecting to broker", mEx);
				throw new RuntimeException(mEx);

			}

			try {

				rifidiServicesApp.getMqttClient().publish(mqttTopic, message);
				logger.info("Message published");

			} catch (MqttException mEx) {

				logger.error("Error publishing to queue", mEx);
				throw new RuntimeException(mEx);

			}

			try {

				rifidiServicesApp.getMqttClient().disconnect();
				logger.info("mqttClient disconnected.");

			} catch (MqttException mEx) {

				logger.error("Error trying to disconnect mqttClient", mEx);
				throw new RuntimeException(mEx);

			}

		} catch (JAXBException jEx) {

			logger.error("Error publishing to queue", jEx);
			throw new RuntimeException(jEx);

		} catch (IOException ioEx) {

			logger.error("Error publishing to queue", ioEx);
			throw new RuntimeException(ioEx);

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.api.service.tagmonitor.ReadZoneSubscriber#tagDeparted
	 * (org.rifidi.edge.notification.TagReadEvent)
	 */
	@Override
	public void tagDeparted(TagReadEvent tag) {
		logger.info("TAG DEPARTED: " + tag.getTag().getFormattedID());
	}
}

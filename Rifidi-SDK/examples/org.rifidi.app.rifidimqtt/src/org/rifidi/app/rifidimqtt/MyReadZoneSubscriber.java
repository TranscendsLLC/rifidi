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
package org.rifidi.app.rifidimqtt;

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
import org.rifidi.app.rifidimqtt.dto.TagMessageDto;
import org.rifidi.app.rifidiservices.util.MyMqttUtil;
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
	private RifidiMqttApp rifidiServicesApp;

	/** Logger for this class */
	private final Log logger = LogFactory.getLog(getClass());
	

	/**
	 * Constructor
	 * 
	 * @param rifidiServicesApp
	 *            the application to associate to this subscriber
	 */
	public MyReadZoneSubscriber(RifidiMqttApp rifidiServicesApp) {

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

		logger.info("TAG ARRIVED: " + tag.getTag().getFormattedID() + " from antenna: " 
					+ tag.getAntennaID());

		TagMessageDto tagMessageDto = new TagMessageDto();
		tagMessageDto.setTag(tag.getTag().getFormattedID());
		tagMessageDto.setTimeStamp(tag.getTimestamp());
		tagMessageDto.setStationId(tag.getReaderID());
		tagMessageDto.setAntennaId(tag.getAntennaID());
		
		//You can do whatever you need with tag data, like posting to mqtt server
		MyMqttUtil.postMqttMesssage(rifidiServicesApp.getMqttClient(), tag.getReaderID(), 
				rifidiServicesApp.getMqttQos(), tagMessageDto);

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
		logger.info("TAG DEPARTED: " + tag.getTag().getFormattedID() + " from antenna: " 
				+ tag.getAntennaID());
	}
}

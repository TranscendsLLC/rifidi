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
import org.rifidi.edge.api.service.tagmonitor.RSSIReadZoneSubscriber;
import org.rifidi.edge.api.service.tagmonitor.ReadZone;
import org.rifidi.edge.api.service.tagmonitor.ReadZoneSubscriber;
import org.rifidi.edge.api.service.tagmonitor.StableSetSubscriber;
import org.rifidi.edge.notification.RSSITagReadEvent;
import org.rifidi.edge.notification.TagReadEvent;

/**
 * The subscriber class. This service monitors which readzone returns the
 * highest average RSSI value for a tag in a given duration.
 * 
 * @author Manuel Tobon - manuel@transcends.co
 */
public class MyRSSIReadZoneSubscriber implements RSSIReadZoneSubscriber {

	/**
	 * Reference to application of this subscriber to notify for example when to
	 * unsubscribe this subscriber and stop reader session
	 **/
	private RifidiServicesApp rifidiServicesApp;

	/** The read zone for this subscriber **/
	//private ReadZone readZone;

	/** Logger for this class */
	private final Log logger = LogFactory.getLog(getClass());

	/** The topic name to post messages from this subscriber **/
	private String topicName = "myTopicRSSIReadZone";

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
	public MyRSSIReadZoneSubscriber(RifidiServicesApp rifidiServicesApp
			//,ReadZone readZone
			) {

		this.rifidiServicesApp = rifidiServicesApp;
		//this.readZone = readZone;

	}

	/**
	 * @return the readZone
	 */
	/*
	public ReadZone getReadZone() {
		return readZone;
	}
	*/

	/**
	 * @param readZone
	 *            the readZone to set
	 */
	/*
	public void setReadZone(ReadZone readZone) {
		this.readZone = readZone;
	}
	*/

	@Override
	public void tagArrived(List<RSSITagReadEvent> rssiTagReadEventList) {

		// You can unsubscribe this subscriber using the app reference
		//rifidiServicesApp.unsubscribeFromxxxxService(this);

		logger.info("RSSI TAG LIST ARRIVED: size: "
				+ rssiTagReadEventList.size());

		for (RSSITagReadEvent rssiTagReadEvent : rssiTagReadEventList) {

			logger.info("rssiTagReadEvent.getCombinedReaderTagID(): "
					+ rssiTagReadEvent.getCombinedReaderTagID());
			logger.info("rssiTagReadEvent.getAvgRSSI(): "
					+ rssiTagReadEvent.getAvgRSSI());
			logger.info("rssiTagReadEvent.getTagCount(): "
					+ rssiTagReadEvent.getTagCount());
		}

		// Subscribe
		//rifidiServicesApp.subscribeToStableSetService(readZone);
		//logger.debug("subscribed the subscriber for reader id: "
		//		+ getReadZone().getReaderID());

	}

	@Override
	public void tagDeparted(List<RSSITagReadEvent> rssiTagReadEventList) {

		logger.info("RSSI TAG LIST DEPARTED: size: "
				+ rssiTagReadEventList.size());

		for (RSSITagReadEvent rssiTagReadEvent : rssiTagReadEventList) {

			logger.info("rssiTagReadEvent.getCombinedReaderTagID(): "
					+ rssiTagReadEvent.getCombinedReaderTagID());
			logger.info("rssiTagReadEvent.getAvgRSSI(): "
					+ rssiTagReadEvent.getAvgRSSI());
			logger.info("rssiTagReadEvent.getTagCount(): "
					+ rssiTagReadEvent.getTagCount());
		}
	}

}

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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.rifidi.edge.api.AbstractRifidiApp;
import org.rifidi.edge.api.service.tagmonitor.ReadZone;
import org.rifidi.edge.api.service.tagmonitor.ReadZoneMonitoringService;
import org.rifidi.edge.api.service.tagmonitor.UniqueTagBatchIntervalService;
import org.rifidi.edge.daos.ReaderDAO;
import org.rifidi.edge.sensors.AbstractSensor;
import org.rifidi.edge.sensors.SensorSession;

/**
 * An application which shows how to conect and use mqtt server to post and retrieve
 * messages
 * 
 * @author Manuel Tobon - manuel@transcends.co
 */
public class RifidiMqttApp 
		extends AbstractRifidiApp {

	/** The readZoneMonitoring service: Monitors a ReadZone or group of ReadZones 
	 * and reports when a tag enters or leaves a reader or antenna in the readzone */
	private ReadZoneMonitoringService readZoneMonitoringService;
	
	/** The uniqueTagBatch service: notifies subscribers every x time units of 
	 * unique tags in a read zone.**/
	private UniqueTagBatchIntervalService uniqueTagBatchIntervalService;
	
	/** Reader DAO -- used to stop reader sessions */
	/** Injected from spring, if you need to stop reader sessions for a particular reader **/
	private ReaderDAO readerDAO;
	
	// A list of all readzoneSubscribers that is kept for when the time comes to
	// unsubscribe them.
	private List<MyReadZoneSubscriber> readZoneMonitoringSubscriberList;
	
	/** mqttClient to be used in sending tag data to mqttServer  **/
	//If you need a mqtt client
	private MqttClient mqttClient;
	
	/** Quality of service used to send the message **/
	/**The MQTT protocol provides three qualities of service for delivering messages 
	 * between clients and servers: "at most once", "at least once" and "exactly once".
	 * QoS0, At most once
	 * QoS1, At least once
	 * QoS2, Exactly once
	 */
	//You can define a quality of service level for delivering message 
	private int mqttQos;
	
	
	/** departureTime: If this amount of time in seconds passes since the last 
	 * time a tag has been seen, then fire a departure event. By default is 4 seconds,
	 * but then is read from properties.
	 * It's used for readZoneMonitoring Service
	 */
	private Float departureTime;
	
	/** Logger for this class */
	private final Log logger = LogFactory.getLog(getClass());
	
		
	/**
	 * @return the mqttClient
	 */
	public MqttClient getMqttClient() {
		return mqttClient;
	}

	/**
	 * @param mqttClient the mqttClient to set
	 */
	public void setMqttClient(MqttClient mqttClient) {
		this.mqttClient = mqttClient;
	}
	
	

	/**
	 * @return the mqttQos
	 */
	public int getMqttQos() {
		return mqttQos;
	}

	/**
	 * @param mqttQos the mqttQos to set
	 */
	public void setMqttQos(int mqttQos) {
		this.mqttQos = mqttQos;
	}

	

	/**
	 * @return the readZoneMonitoringService
	 */
	public ReadZoneMonitoringService getReadZoneMonitoringService() {
		return readZoneMonitoringService;
	}

	/**
	 * @param readZoneMonitoringService the readZoneMonitoringService to set
	 */
	public void setReadZoneMonitoringService(
			ReadZoneMonitoringService readZoneMonitoringService) {
		this.readZoneMonitoringService = readZoneMonitoringService;
	}
	

	/**
	 * @return the uniqueTagBatchIntervalService
	 */
	public UniqueTagBatchIntervalService getUniqueTagBatchIntervalService() {
		return uniqueTagBatchIntervalService;
	}

	/**
	 * @param uniqueTagBatchIntervalService the uniqueTagBatchIntervalService to set
	 */
	public void setUniqueTagBatchIntervalService(
			UniqueTagBatchIntervalService uniqueTagBatchIntervalService) {
		this.uniqueTagBatchIntervalService = uniqueTagBatchIntervalService;
	}
	

	/**
	 * Inject the reader DAO to allow this app to stop the reader.  
	 * @param readerDAO
	 */
	//Used by spring framework to inject the reader dao
	public void setReaderDAO(ReaderDAO readerDAO) {
		this.readerDAO = readerDAO;
	}
	
	
	/**
	 * @return the readerDAO
	 */
	private ReaderDAO getReaderDAO() {
		return readerDAO;
	}
	
	
	/**
	 * @return the departureTime
	 */
	public Float getDepartureTime() {
		return departureTime;
	}

	/**
	 * @param departureTime the departureTime to set
	 */
	public void setDepartureTime(Float departureTime) {
		this.departureTime = departureTime;
	}
	
	
	/**
	 * Constructor called by spring injection
	 * @param group the group name of application
	 * @param name the name of application
	 */
	public RifidiMqttApp(String group, String name) {
		super(group, name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.api.AbstractRifidiApp#_start()
	 */
	@Override
	public void _start() {
		super._start();
		
		readZoneMonitoringSubscriberList = new LinkedList<MyReadZoneSubscriber>();
		
		//Get all the read zones
		HashMap<String, ReadZone> allReadZones = super.getReadZones();
		
		//Get the front door read zone. According to this readzone properties file, 
		//Front_Door readzone contains only one reader
		ReadZone frontDoorReadZone = allReadZones.get("Front_Door");
		
		//Get the back door read zone. According to this readzone properties file, 
		//Back_Door readzone contains one or many readers
		ReadZone backDoorReadZone = allReadZones.get("Back_Door");
		
		//Subscribe the frontDoorReadZone to readZoneMonitoringService
		subscribeToReadZoneService(frontDoorReadZone);
				
		//Subscribe the backDoorReadZone to readZoneMonitoringService
		subscribeToReadZoneService(backDoorReadZone);
		
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.api.AbstractRifidiApp#_stop()
	 */
	@Override
	public void _stop() {
		
		//Unsubscribe from readzone service
		for (MyReadZoneSubscriber sub : this.readZoneMonitoringSubscriberList) {
			unsubscribeFromReadZoneService(sub);
		}
		
	}
	
	public void unsubscribeFromReadZoneService(MyReadZoneSubscriber myReadZoneSubscriber){
		
		getReadZoneMonitoringService().unsubscribe(myReadZoneSubscriber);
		
		//logger.info("unsubscribed subscriber with reader id: " + myReadZoneSubscriber.getReadZone().getReaderID());
		
	}
	
	/**
	 * 
	 * @param readZone the readzone to subscribe
	 */
	
	
	public void subscribeToReadZoneService(ReadZone readZone){
		
		//Create an instance of the stableset subscriber
		MyReadZoneSubscriber myReadZoneSubscriber = new MyReadZoneSubscriber(this);
		
		readZoneMonitoringSubscriberList.add(myReadZoneSubscriber);
		
		//Create a list of readzones
		List<ReadZone> readZoneList = new LinkedList<ReadZone>();
		
		//Add the readzone to the readzone list and subscribe 
		readZoneList.add(readZone);
		
		getReadZoneMonitoringService().subscribe(myReadZoneSubscriber, readZoneList, 
				getDepartureTime(), TimeUnit.SECONDS, true, true);
		
		logger.info("subscribed readzone with reader id: " + readZone.getReaderID()
				+ " and departureTime: " + getDepartureTime());
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.api.AbstractRifidiApp#initialize()
	 */
	@Override
	public void initialize() {
		
		//Here you initialize properties and resources, like mqtt client
		
		//Set departure time from properties
		setDepartureTime( Float.parseFloat(getProperty("departureTime",	null)) );
		
		int mqttQos = Integer.parseInt(getProperty("mqttQos", "2"));
		setMqttQos(mqttQos);
		
		String mqttBroker = getProperty("mqttBroker", null);
		String mqttClientId = getProperty("mqttClientId", null);
		
		MemoryPersistence persistence = new MemoryPersistence();
		
		try {

			setMqttClient( new MqttClient(mqttBroker, mqttClientId, persistence) );
			MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
           
		} catch(MqttException mEx){
			
			logger.error("Error creating mqttClient instance to broker", mEx);
			throw new RuntimeException(mEx);
			
		}
		
	}
	
	/**
	 * Stop any currently running sessions for the given reader ID.  
	 * 
	 * @param readerID
	 */
	public void stopReaderSession(String readerID) {
		AbstractSensor<?> sensor = getReaderDAO().getReaderByID(readerID);
		if(sensor != null ) {
			Map<String,SensorSession> sessions = sensor.getSensorSessions();
			for(String s:sessions.keySet()) {
				sessions.get(s).disconnect();
			}
			
			logger.debug("Disconnected sessions for reader id " 
					+ readerID);
			
		} else {
			
			logger.error("Error disconnecting sessions for reader id " 
			+ readerID + ". No reader with this name found.");
  
		}
	}
	

}

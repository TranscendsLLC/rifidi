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
package org.rifidi.app.rifidiservices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.rifidi.app.rifidiservices.subscriber.MyReadZoneSubscriber;
import org.rifidi.app.rifidiservices.subscriber.MyStableSetSubscriber;
import org.rifidi.app.rifidiservices.subscriber.MyUniqueTagBatchSubscriber;
import org.rifidi.edge.api.AbstractRifidiApp;
import org.rifidi.edge.api.service.tagmonitor.ReadZone;
import org.rifidi.edge.api.service.tagmonitor.ReadZoneMonitoringService;
import org.rifidi.edge.api.service.tagmonitor.ReadZoneSubscriber;
import org.rifidi.edge.api.service.tagmonitor.StableSetService;
import org.rifidi.edge.api.service.tagmonitor.StableSetSubscriber;
import org.rifidi.edge.api.service.tagmonitor.UniqueTagBatchIntervalService;
import org.rifidi.edge.api.service.tagmonitor.UniqueTagBatchIntervalSubscriber;
import org.rifidi.edge.daos.ReaderDAO;
import org.rifidi.edge.sensors.AbstractSensor;
import org.rifidi.edge.sensors.SensorSession;

/**
 * An application which shows all services supported by rifidi-sdk
 * 
 * @author Manuel Tobon - manuel@transcends.co
 */
public class RifidiServicesApp 
		extends AbstractRifidiApp {

	/** The stableSet service */
	private StableSetService stableSetService;
	
	/** The readZoneMonitoring service */
	private ReadZoneMonitoringService readZoneMonitoringService;
	
	/** The uniqueTagBatch service: notifies subscribers every x time units of unique tags in a read zone.**/
	private UniqueTagBatchIntervalService uniqueTagBatchIntervalService;
	
	/** Reader DAO -- used to stop reader sessions */
	/** Injected from spring, if you need to stop reader sessions for a particular reader **/
	private ReaderDAO readerDAO;
	
	// A list of all stableSetsubscribers that is kept for when the time comes to
	// unsubscribe them.
	private List<MyStableSetSubscriber> stableSetSubscriberList;
	
	// A list of all readzoneSubscribers that is kept for when the time comes to
	// unsubscribe them.
	private List<MyReadZoneSubscriber> readZoneMonitoringSubscriberList;
	
	// A list of all uniqueTagBatchSubscribers that is kept for when the time comes to
	// unsubscribe them.
	private List<MyUniqueTagBatchSubscriber> uniqueTagBatchSubscriberList;
	
	
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
	
	/** stableSetTime - The time that must pass with no new tags having been 
	 * seen before the stable set will return.
	 * It's used for stableSet Service
	 */
	private Float stableSetTime;
	
	/** departureTime: If this amount of time in seconds passes since the last 
	 * time a tag has been seen, then fire a departure event. By default is 4 seconds,
	 * but then is read from properties.
	 */
	private Float departureTime;
	
	/**
	 * notifYInterval: Interval in seconds to notify all of tags currently seen 
	 * at the given readzones
	 */
	private Float notifyInterval;
	
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
	 * @return the stableSetService
	 */
	public StableSetService getStableSetService() {
		return stableSetService;
	}

	/**
	 * @param stableSetService the stableSetService to set
	 */
	public void setStableSetService(StableSetService stableSetService) {
		this.stableSetService = stableSetService;
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
	 * @return the notifYInterval
	 */
	public Float getNotifyInterval() {
		return notifyInterval;
	}

	/**
	 * @param notifYInterval the notifYInterval to set
	 */
	public void setNotifyInterval(Float notifyInterval) {
		this.notifyInterval = notifyInterval;
	}

	/**
	 * Constructor called by spring injection
	 * @param group the group name of application
	 * @param name the name of application
	 */
	public RifidiServicesApp(String group, String name) {
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
		
		stableSetSubscriberList = new LinkedList<MyStableSetSubscriber>();
		
		readZoneMonitoringSubscriberList = new LinkedList<MyReadZoneSubscriber>();
		
		uniqueTagBatchSubscriberList = new LinkedList<MyUniqueTagBatchSubscriber>();
		
		//Get all the read zones
		Map<String, ReadZone> allReadZones = super.getReadZones();
		
		//Get the front door read zone. According to this readzone properties file, 
		//Front_Door readzone contains only one reader
		ReadZone frontDoorReadZone = allReadZones.get("Front_Door");
		
		//Get the back door read zone. According to this readzone properties file, 
		//Back_Door readzone contains one or many readers
		ReadZone backDoorReadZone = allReadZones.get("Back_Door");
		
		//Subscribe the frontDoorReadZone to stableSetService
		//subscribeToStableSetService(frontDoorReadZone);
				
		//Subscribe the backDoorReadZone to stableSetService
		//subscribeToStableSetService(backDoorReadZone);
				
		//Subscribe the frontDoorReadZone to readZoneMonitoringService
		subscribeToReadZoneService(frontDoorReadZone);
				
		//Subscribe the backDoorReadZone to readZoneMonitoringService
		subscribeToReadZoneService(backDoorReadZone);
		
		//Subscribe the frontDoorReadZone to uniqueTagBatchIntervalService
		subscribeToUniqueTagBatchIntervalService(frontDoorReadZone);
				
		//Subscribe the backDoorReadZone to uniqueTagBatchIntervalService
		subscribeToUniqueTagBatchIntervalService(backDoorReadZone);
				
		//map->, key: the subscriber, value: the service
		//iterate over keyset
		//hashmap.get(key).unsubscribe(key)
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.api.AbstractRifidiApp#_stop()
	 */
	@Override
	public void _stop() {
		
		//Unsubscribe from stableset service
		for (MyStableSetSubscriber sub : this.stableSetSubscriberList) {
			unsubscribeFromStableSetService(sub);
		}
		
		//Unsubscribe from readzone service
		for (MyReadZoneSubscriber sub : this.readZoneMonitoringSubscriberList) {
			unsubscribeFromReadZoneService(sub);
		}
		
		//Unsubscribe from uniqueTagBatchIntervalService
		for (MyUniqueTagBatchSubscriber sub : this.uniqueTagBatchSubscriberList) {
			unsubscribeFromUniqueTagBatchIntervalService(sub);
		}
		
		
	}
	
	/**
	 * @return the stableSetTime
	 */
	
	public Float getStableSetTime() {
		return stableSetTime;
	}
	

	/**
	 * @param stableSetTime the stableSetTime to set
	 */
	
	public void setStableSetTime(Float stableSetTime) {
		this.stableSetTime = stableSetTime;
	}
	
	
	/**
	 * 
	 * @param stableSetSubscriber
	 */
	public void unsubscribeFromStableSetService(MyStableSetSubscriber myStableSetSubscriber){
		
		getStableSetService().unsubscribe(myStableSetSubscriber);
		
		logger.info("unsubscribed subscriber with reader id: " + myStableSetSubscriber.getReadZone().getReaderID());
		
	}
	
	
	public void unsubscribeFromReadZoneService(MyReadZoneSubscriber myReadZoneSubscriber){
		
		getReadZoneMonitoringService().unsubscribe(myReadZoneSubscriber);
		
		//logger.info("unsubscribed subscriber with reader id: " + myReadZoneSubscriber.getReadZone().getReaderID());
		
	}
	
	public void unsubscribeFromUniqueTagBatchIntervalService(MyUniqueTagBatchSubscriber myUniqueTagBatchSubscriber){
		
		getUniqueTagBatchIntervalService().unsubscribe(myUniqueTagBatchSubscriber);
		logger.info("unsubscribed subscriber with reader id: " + myUniqueTagBatchSubscriber.getReadZone().getReaderID());
	}
	
	/**
	 * 
	 * @param stableSetSubscriber
	 */
	public void subscribeToStableSetService(ReadZone readZone){
		
		//Create an instance of the stableset subscriber
		MyStableSetSubscriber myStableSetSubscriber = new MyStableSetSubscriber(this, 
				readZone);
		
		stableSetSubscriberList.add(myStableSetSubscriber);
		
		//Create a list of readzones
		List<ReadZone> readZoneList = new LinkedList<ReadZone>();
		
		//Add the readzone to the readzone list and subscribe 
		readZoneList.add(readZone);
		
		getStableSetService().subscribe(myStableSetSubscriber, readZoneList, getStableSetTime(), 
				TimeUnit.SECONDS, true, true);
		
		logger.info("subscribed readzone with reader id: " + readZone.getReaderID()
				+ " and readerStableSetTime: " + getStableSetTime());
		
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
				getDepartureTime(), TimeUnit.SECONDS, false, true);
		
		logger.info("subscribed readzone with reader id: " + readZone.getReaderID()
				+ " and departureTime: " + getDepartureTime());
		
	}
	
	/**
	 * 
	 * @param readZone the readzone to subscribe
	 */
	public void subscribeToUniqueTagBatchIntervalService(ReadZone readZone){
		
		//Create an instance of the unique tag batch subscriber
		MyUniqueTagBatchSubscriber myUniqueTagBatchSubscriber = new MyUniqueTagBatchSubscriber(this, 
				readZone);
		
		uniqueTagBatchSubscriberList.add(myUniqueTagBatchSubscriber);
		
		//Create a list of readzones
		List<ReadZone> readZoneList = new LinkedList<ReadZone>();
		
		//Add the readzone to the readzone list and subscribe 
		readZoneList.add(readZone);
		
		getUniqueTagBatchIntervalService().subscribe(myUniqueTagBatchSubscriber, readZoneList, getNotifyInterval(), 
				TimeUnit.SECONDS);
		
		logger.info("subscribed readzone with reader id: " + readZone.getReaderID()
				+ " and readerNotifyInterval: " + getNotifyInterval());
		
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
		
		//Set stableset time from properties
		setStableSetTime( Float.parseFloat(getProperty("stableSetTime", null)) );
		
		//Set the notifyInterval
		setNotifyInterval(Float.parseFloat(getProperty("notifyInterval", null)) );
		
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

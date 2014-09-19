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
import org.rifidi.app.rifidiservices.subscriber.MyLimitStableSetSubscriber;
import org.rifidi.app.rifidiservices.subscriber.MyRSSIReadZoneSubscriber;
import org.rifidi.app.rifidiservices.subscriber.MyReadZoneSubscriber;
import org.rifidi.app.rifidiservices.subscriber.MyStableSetSubscriber;
import org.rifidi.app.rifidiservices.subscriber.MyUniqueTagBatchSubscriber;
import org.rifidi.edge.api.AbstractRifidiApp;
import org.rifidi.edge.api.service.tagmonitor.LimitStableSetService;
import org.rifidi.edge.api.service.tagmonitor.RSSIMonitoringService;
import org.rifidi.edge.api.service.tagmonitor.ReadZone;
import org.rifidi.edge.api.service.tagmonitor.ReadZoneMonitoringService;
import org.rifidi.edge.api.service.tagmonitor.StableSetService;
import org.rifidi.edge.api.service.tagmonitor.UniqueTagBatchIntervalService;
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

	/** The stableSet service: returns a list of tags representing a "group" after 
	 * an allotted time has passed with no new tags arriving. For instance, if you 
	 * place 30 tags in the readzone of the reader, assuming all tags can be read 
	 * correctly it will return a list of 30 tags assuming no new tags are seen 
	 * for the duration given */
	private StableSetService stableSetService;
	
	/** Exactly the same as the StableSetService, however you can also place a 
	 * limit on the number of tags that can show up before it automatically returns.
	 * If the tag limit is hit before the time duration of the StableSet runs out 
	 * it will automatically return with all seen tags */
	private LimitStableSetService limitStableSetService;
	
	/** The readZoneMonitoring service: Monitors a ReadZone or group of ReadZones 
	 * and reports when a tag enters or leaves a reader or antenna in the readzone */
	private ReadZoneMonitoringService readZoneMonitoringService;
	
	/** The uniqueTagBatch service: notifies subscribers every x time units of 
	 * unique tags in a read zone.**/
	private UniqueTagBatchIntervalService uniqueTagBatchIntervalService;
	
	/** This service monitors which readzone returns the highest average RSSI 
	 * value for a tag in a given duration. **/
	private RSSIMonitoringService rssiMonitoringService;
	
	/** Reader DAO -- used to stop reader sessions */
	/** Injected from spring, if you need to stop reader sessions for a particular reader **/
	private ReaderDAO readerDAO;
	
	// A list of all stableSetsubscribers that is kept for when the time comes to
	// unsubscribe them.
	private List<MyStableSetSubscriber> stableSetSubscriberList;
	
	// A list of all limitStableSetsubscribers that is kept for when the time comes to
	// unsubscribe them.
	private List<MyLimitStableSetSubscriber> limitStableSetSubscriberList;
	
	// A list of all readzoneSubscribers that is kept for when the time comes to
	// unsubscribe them.
	private List<MyReadZoneSubscriber> readZoneMonitoringSubscriberList;
	
	// A list of all uniqueTagBatchSubscribers that is kept for when the time comes to
	// unsubscribe them.
	private List<MyUniqueTagBatchSubscriber> uniqueTagBatchSubscriberList;
	
	// A list of all rssiReadZoneSubscribers that is kept for when the time comes to
	// unsubscribe them.
	private List<MyRSSIReadZoneSubscriber> rssiReadZoneSubscriberList;
	
	
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
	
	/**
	 * limitOfTags: The limit of tags that are being looked for. If this limit is hit 
	 * before the stableSetTime is reached, it will return all seen tags. 
	 * It's used for limitStableSet Service */
	private Integer limitOfTags;
	
	/** departureTime: If this amount of time in seconds passes since the last 
	 * time a tag has been seen, then fire a departure event. By default is 4 seconds,
	 * but then is read from properties.
	 * It's used for readZoneMonitoring Service
	 */
	private Float departureTime;
	
	/**
	 * notifYInterval: Interval in seconds to notify all of tags currently seen 
	 * at the given readzones
	 * It's used for uniqueTagBatchInterval Service
	 */
	private Float notifyInterval;
	
	/**
	 * windowTime: The timeout that will be used to determine if a tag has gone 
	 * to a new zone. Times less than 5s are not recommended.
	 * It's used for RSSIMonitoring Service
	 */
	private Float windowTime;
	
	/**
	 * countThreshold: The threshold for number of times a tag must be read before 
	 * the readzone it has shown up at will be switched. 
	 * Do not set this to greater than the windowTime divided by the frequency in 
	 * seconds that tag reports will show up as configured by your reader.
	 * It's used for RSSIMonitoring Service
	 */
	private Integer countThreshold;
	
	
	/**
	 *  minAvgRSSIThreshold: The lowest average RSSI that will have to be seen
	 *  before the readzone is switched.
	 *  It's used for RSSIMonitoring Service
	 */
	private Double minAvgRSSIThreshold;
	
	
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
	 * @return the limitStableSetService
	 */
	public LimitStableSetService getLimitStableSetService() {
		return limitStableSetService;
	}

	/**
	 * @param limitStableSetService the limitStableSetService to set
	 */
	public void setLimitStableSetService(LimitStableSetService limitStableSetService) {
		this.limitStableSetService = limitStableSetService;
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
	 * @return the rssiMonitoringService
	 */
	public RSSIMonitoringService getRssiMonitoringService() {
		return rssiMonitoringService;
	}

	/**
	 * @param rssiMonitoringService the rssiMonitoringService to set
	 */
	public void setRssiMonitoringService(RSSIMonitoringService rssiMonitoringService) {
		this.rssiMonitoringService = rssiMonitoringService;
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
	 * @return the windowTime
	 */
	public Float getWindowTime() {
		return windowTime;
	}

	/**
	 * @param windowTime the windowTime to set
	 */
	public void setWindowTime(Float windowTime) {
		this.windowTime = windowTime;
	}

	/**
	 * @return the countThreshold
	 */
	public Integer getCountThreshold() {
		return countThreshold;
	}

	/**
	 * @param countThreshold the countThreshold to set
	 */
	public void setCountThreshold(Integer countThreshold) {
		this.countThreshold = countThreshold;
	}

	/**
	 * @return the minAvgRSSIThreshold
	 */
	public Double getMinAvgRSSIThreshold() {
		return minAvgRSSIThreshold;
	}

	/**
	 * @param minAvgRSSIThreshold the minAvgRSSIThreshold to set
	 */
	public void setMinAvgRSSIThreshold(Double minAvgRSSIThreshold) {
		this.minAvgRSSIThreshold = minAvgRSSIThreshold;
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
		
		limitStableSetSubscriberList = new LinkedList<MyLimitStableSetSubscriber>();
		
		readZoneMonitoringSubscriberList = new LinkedList<MyReadZoneSubscriber>();
		
		uniqueTagBatchSubscriberList = new LinkedList<MyUniqueTagBatchSubscriber>();
		
		rssiReadZoneSubscriberList = new LinkedList<MyRSSIReadZoneSubscriber>();
		
		//Get all the read zones
		HashMap<String, ReadZone> allReadZones = super.getReadZones();
		
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
		
		//Subscribe the frontDoorReadZone to limitStableSetService
		subscribeToLimitStableSetService(frontDoorReadZone);
				
		//Subscribe the backDoorReadZone to limitStableSetService
		subscribeToLimitStableSetService(backDoorReadZone);
				
		//Subscribe the frontDoorReadZone to readZoneMonitoringService
		subscribeToReadZoneService(frontDoorReadZone);
				
		//Subscribe the backDoorReadZone to readZoneMonitoringService
		subscribeToReadZoneService(backDoorReadZone);
		
		//Subscribe the frontDoorReadZone to uniqueTagBatchIntervalService
		subscribeToUniqueTagBatchIntervalService(frontDoorReadZone);
				
		//Subscribe the backDoorReadZone to uniqueTagBatchIntervalService
		subscribeToUniqueTagBatchIntervalService(backDoorReadZone);
		
		//Subscribe allReadZones to rssiMonitoringService
		subscribeToRSSIMonitoringService(allReadZones);
				
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
		
		//Unsubscribe from limitStableset service
		for (MyLimitStableSetSubscriber sub : this.limitStableSetSubscriberList) {
			unsubscribeFromLimitStableSetService(sub);
		}
		
		//Unsubscribe from readzone service
		for (MyReadZoneSubscriber sub : this.readZoneMonitoringSubscriberList) {
			unsubscribeFromReadZoneService(sub);
		}
		
		//Unsubscribe from uniqueTagBatchIntervalService
		for (MyUniqueTagBatchSubscriber sub : this.uniqueTagBatchSubscriberList) {
			unsubscribeFromUniqueTagBatchIntervalService(sub);
		}
		
		//Unsubscribe from rssiMonitoringService
		for (MyRSSIReadZoneSubscriber sub : this.rssiReadZoneSubscriberList) {
			unsubscribeFromRSSIMonitoringService(sub);
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
	 * @return the limitOfTags
	 */
	public Integer getLimitOfTags() {
		return limitOfTags;
	}

	/**
	 * @param limitOfTags the limitOfTags to set
	 */
	public void setLimitOfTags(Integer limitOfTags) {
		this.limitOfTags = limitOfTags;
	}

	/**
	 * 
	 * @param stableSetSubscriber
	 */
	public void unsubscribeFromStableSetService(MyStableSetSubscriber myStableSetSubscriber){
		
		getStableSetService().unsubscribe(myStableSetSubscriber);
		
		logger.info("unsubscribed subscriber with reader id: " + myStableSetSubscriber.getReadZone().getReaderID());
		
	}
	
	
	public void unsubscribeFromLimitStableSetService(MyLimitStableSetSubscriber myLimitStableSetSubscriber){
		
		getLimitStableSetService().unsubscribe(myLimitStableSetSubscriber);
		
		logger.info("unsubscribed subscriber with reader id: " + myLimitStableSetSubscriber.getReadZone().getReaderID());
		
	}
	
	
	public void unsubscribeFromReadZoneService(MyReadZoneSubscriber myReadZoneSubscriber){
		
		getReadZoneMonitoringService().unsubscribe(myReadZoneSubscriber);
		
		//logger.info("unsubscribed subscriber with reader id: " + myReadZoneSubscriber.getReadZone().getReaderID());
		
	}
	
	public void unsubscribeFromUniqueTagBatchIntervalService(MyUniqueTagBatchSubscriber myUniqueTagBatchSubscriber){
		
		getUniqueTagBatchIntervalService().unsubscribe(myUniqueTagBatchSubscriber);
		logger.info("unsubscribed subscriber with reader id: " + myUniqueTagBatchSubscriber.getReadZone().getReaderID());
	}
	
	
	public void unsubscribeFromRSSIMonitoringService(MyRSSIReadZoneSubscriber myRSSIReadZoneSubscriber){
		
		getRssiMonitoringService().unsubscribe(myRSSIReadZoneSubscriber);
		//logger.info("unsubscribed subscriber with reader id: " + myRSSIReadZoneSubscriber.getReadZone().getReaderID());
	}
	
	
	/**
	 * 
	 * @param readZone the readZone to subscribe
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
	 * @param readZone the readZone to subscribe
	 */
	public void subscribeToLimitStableSetService(ReadZone readZone){
		
		//Create an instance of the limit stableset subscriber
		MyLimitStableSetSubscriber myLimitStableSetSubscriber = new MyLimitStableSetSubscriber(this, 
				readZone);
		
		limitStableSetSubscriberList.add(myLimitStableSetSubscriber);
		
		//Create a list of readzones
		List<ReadZone> readZoneList = new LinkedList<ReadZone>();
		
		//Add the readzone to the readzone list and subscribe 
		readZoneList.add(readZone);
		
		getLimitStableSetService().subscribe(myLimitStableSetSubscriber, readZoneList, getStableSetTime(), 
				TimeUnit.SECONDS, getLimitOfTags(), true);
		
		logger.info("subscribed readzone with reader id: " + readZone.getReaderID()
				+ " and readerStableSetTime: " + getStableSetTime() + " and limitOfTags: "
				+ getLimitOfTags());
		
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
	
	
	/**
	 * 
	 * @param allReadZones all the readzones to subscribe
	 */
	public void subscribeToRSSIMonitoringService(HashMap<String, ReadZone> allReadZones){
		
		//Create an instance of the rssi read zone subscriber
		MyRSSIReadZoneSubscriber myRSSIReadZoneSubscriber = new MyRSSIReadZoneSubscriber(this);
		
		rssiReadZoneSubscriberList.add(myRSSIReadZoneSubscriber);
		
		getRssiMonitoringService().subscribe(myRSSIReadZoneSubscriber, allReadZones, 
				getWindowTime(), TimeUnit.SECONDS, getCountThreshold(), getMinAvgRSSIThreshold(),
				true);
		
		logger.info("subscribed all readzones to RSSI Monitor"
				+ " and getWindowTime: " + getWindowTime() + ", countThreshold: " 
				+ getCountThreshold() + ", minAvgRSSIThreshold: " + getMinAvgRSSIThreshold());
		
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
		
		//Set limit of tags for stableset time from properties
		setLimitOfTags( Integer.parseInt(getProperty("limitOfTags", null)) );
		
		//Set the notifyInterval
		setNotifyInterval(Float.parseFloat(getProperty("notifyInterval", null)) );
		
		//Set the windowTime
		setWindowTime(Float.parseFloat(getProperty("windowTime", null)) );
				
		//Set the countThreshold
		setCountThreshold(Integer.parseInt(getProperty("countThreshold", null)) );
				
		//Set the minAvgRSSIThreshold
		setMinAvgRSSIThreshold(Double.parseDouble(getProperty("minAvgRSSIThreshold", null)) );
		
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

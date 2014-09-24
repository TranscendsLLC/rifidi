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
package org.rifidi.app.rifidimanagement;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.api.AbstractRifidiApp;
import org.rifidi.edge.api.service.tagmonitor.ReadZone;
import org.rifidi.edge.api.service.tagmonitor.ReadZoneMonitoringService;

/**
 * A simple application to test available rifidi API commands
 * 
 * @author Manuel Tobon - manuel@transcends.co
 */
public class RifidiManagementApp 
		extends AbstractRifidiApp {

	/** The readZoneMonitoring service: Monitors a ReadZone or group of ReadZones 
	 * and reports when a tag enters or leaves a reader or antenna in the readzone */
	private ReadZoneMonitoringService readZoneMonitoringService;
	
	// A list of all readzoneSubscribers that is kept for when the time comes to
	// unsubscribe them.
	private List<MyReadZoneSubscriber> readZoneMonitoringSubscriberList;
	
	/** departureTime: If this amount of time in seconds passes since the last 
	 * time a tag has been seen, then fire a departure event.
	 */
	private Float departureTime;
	
	/** Logger for this class */
	private final Log logger = LogFactory.getLog(getClass());
		

	/**
	 * @return the departureTime
	 */
	private Float getDepartureTime() {
		return departureTime;
	}

	/**
	 * @param departureTime the departureTime to set
	 */
	private void setDepartureTime(Float departureTime) {
		this.departureTime = departureTime;
	}
	
	
	
	public RifidiManagementApp(String group, String name) {
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
		
		// Get the read zone.
		ReadZone observableReadZone = super.getReadZones().get("observable");

		//Subscribe the observable read zone readZoneMonitoringService
		subscribeToReadZoneService(observableReadZone);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.api.AbstractRifidiApp#initialize()
	 */
	@Override
	public void initialize() {
		
		//Set departure time from properties
		setDepartureTime( Float.parseFloat(getProperty("departureTime",
				null)) );

	}

	/**
	 * Called by spring. This method injects the ReadZoneMonitoringService into
	 * the application.
	 * 
	 * @param rzms
	 */
	public void setReadZoneMonitoringService(ReadZoneMonitoringService rzms) {
		this.readZoneMonitoringService = rzms;
	}
	
	
	
	/**
	 * @return the readZoneMonitoringService
	 */
	public ReadZoneMonitoringService getReadZoneMonitoringService() {
		return readZoneMonitoringService;
	}

	/**
	 * Subscribe to readzone service
	 * @param readZone the readzone to subscribe
	 */
	public void subscribeToReadZoneService(ReadZone readZone){
		
		//Create an instance of the stableset subscriber
		MyReadZoneSubscriber myReadZoneSubscriber = new MyReadZoneSubscriber();
		
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
	
	
	
	
public void unsubscribeFromReadZoneService(MyReadZoneSubscriber myReadZoneSubscriber){
		
		getReadZoneMonitoringService().unsubscribe(myReadZoneSubscriber);
		
		//logger.info("unsubscribed subscriber with reader id: " + myReadZoneSubscriber.getReadZone().getReaderID());
		
	}
}

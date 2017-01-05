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
package org.rifidi.app.amazonsample;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.api.AbstractRifidiApp;
import org.rifidi.edge.api.service.tagmonitor.ReadZone;
import org.rifidi.edge.api.service.tagmonitor.ReadZoneMonitoringService;
import org.rifidi.edge.api.service.tagmonitor.ReadZoneSubscriber;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.model.TableStatus;

public class DynamoDBApp extends AbstractRifidiApp {

	/** The logger for this class */
	//private static Log logger = LogFactory.getLog(DynamoDBApp.class);

	private AmazonDynamoDBClient dynamoDB;

	/** The service for monitoring arrival and departure events */
	private ReadZoneMonitoringService readZoneMonitoringService;

	private List<ReadZoneSubscriber> subscriberList;
	
	/** departureTime: If this amount of time in seconds passes since the last 
	 * time a tag has been seen, then fire a departure event.
	 * It's used for readZoneMonitoring Service
	 */
	private Float departureTime;
	
	/** awsSecretKey **/
	private String awsSecretKey;
	
	/** awsAccessKeyId **/
	private String awsAccessKeyId;
	
	

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
	 * @return the awsSecretKey
	 */
	public String getAwsSecretKey() {
		return awsSecretKey;
	}

	/**
	 * @param awsSecretKey the awsSecretKey to set
	 */
	public void setAwsSecretKey(String awsSecretKey) {
		this.awsSecretKey = awsSecretKey;
	}

	/**
	 * @return the awsAccessKeyId
	 */
	public String getAwsAccessKeyId() {
		return awsAccessKeyId;
	}

	/**
	 * @param awsAccessKeyId the awsAccessKeyId to set
	 */
	public void setAwsAccessKeyId(String awsAccessKeyId) {
		this.awsAccessKeyId = awsAccessKeyId;
	}

	/**
	 * 
	 * 
	 * @param group
	 * @param name
	 */
	public DynamoDBApp(String group, String name) {
		super(group, name);

		AWSCredentials cred = new AWSCredentials() {

			@Override
			public String getAWSSecretKey() {
				return awsSecretKey;
			}

			@Override
			public String getAWSAccessKeyId() {
				return awsAccessKeyId;
			}
		};

		dynamoDB = new AmazonDynamoDBClient(cred);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.api.AbstractRifidiApp#initialize()
	 */
	@Override
	public void initialize() {
		
		//Set departure time from properties
		setDepartureTime( Float.parseFloat(getProperty("departureTime",	null)) );
		
		//Set awsAccessKeyId from properties
		setAwsAccessKeyId(getProperty("awsAccessKeyId",	null));
		
		//Set awsSecretKey from properties
		setAwsSecretKey(getProperty("awsSecretKey",	null));
	}

	/**
	 * Start the application. This method submits the esper statements to the
	 * esper runtime.
	 */
	@Override
	public void _start() {
		super._start();

		String tableName = "epc-tags";

		waitForTableToBecomeAvailable(tableName);

		DynamoDBSubscriber sub = new DynamoDBSubscriber(this.dynamoDB,
				tableName);
		this.subscriberList = new LinkedList<ReadZoneSubscriber>();
		this.subscriberList.add(sub);
		this.readZoneMonitoringService.subscribe(sub,
				new LinkedList<ReadZone>(), departureTime, TimeUnit.SECONDS, true);
	}

	/**
	 * Iterate through all statements and stop them.
	 */
	@Override
	public void _stop() {
		for (ReadZoneSubscriber s : this.subscriberList) {
			this.readZoneMonitoringService.unsubscribe(s);
		}
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

	private void waitForTableToBecomeAvailable(String tableName) {
		System.out.println("Waiting for " + tableName + " to become ACTIVE...");

		long startTime = System.currentTimeMillis();
		long endTime = startTime + (10 * 60 * 1000);
		while (System.currentTimeMillis() < endTime) {
			try {
				Thread.sleep(1000 * 20);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				DescribeTableRequest request = new DescribeTableRequest()
						.withTableName(tableName);
				TableDescription tableDescription = dynamoDB.describeTable(
						request).getTable();
				String tableStatus = tableDescription.getTableStatus();
				System.out.println("  - current state: " + tableStatus);
				if (tableStatus.equals(TableStatus.ACTIVE.toString()))
					return;
			} catch (AmazonServiceException ase) {
				if (ase.getErrorCode().equalsIgnoreCase(
						"ResourceNotFoundException") == false) {
					throw ase;
				}
			}
		}

		throw new RuntimeException("Table " + tableName + " never went active");
	}

}

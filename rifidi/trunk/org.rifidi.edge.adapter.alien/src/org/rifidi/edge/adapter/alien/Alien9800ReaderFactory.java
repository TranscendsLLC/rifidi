/*
 * 
 * Alien9800ReaderFactory.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
/**
 * 
 */
package org.rifidi.edge.adapter.alien;

import java.util.Map;

import javax.management.MBeanInfo;

import org.rifidi.edge.exceptions.InvalidStateException;
import org.rifidi.edge.notification.NotifierService;
import org.rifidi.edge.sensors.AbstractCommandConfiguration;
import org.rifidi.edge.sensors.AbstractSensor;
import org.rifidi.edge.sensors.AbstractSensorFactory;

/**
 * The Factory for producing Alien9800 Readers
 * 
 * @author Jochen Mader - jochen@pramari.com
 */
public class Alien9800ReaderFactory extends
		AbstractSensorFactory<Alien9800Reader> {

	/** The Unique FACTORY_ID for this Factory */
	public static final String FACTORY_ID = "Alien";
	/** Description of the sensorSession. */
	private static final String description = "The Rifidi Alien adapter supports the Alien ALR "
			+ "protocol on the 9900, 9800, 8800 readers. ";
	private static final String displayname = "Alien";
	/** A JMS event notification sender */
	private volatile NotifierService notifierService;

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.sensors.base.AbstractSensorFactory#
	 * bindCommandConfiguration
	 * (org.rifidi.edge.sensors.commands.AbstractCommandConfiguration,
	 * java.util.Map)
	 */
	@Override
	public void bindCommandConfiguration(
			AbstractCommandConfiguration<?> commandConfiguration,
			Map<?, ?> properties) {
		// TODO: ignored for now.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.sensors.base.AbstractSensorFactory#
	 * unbindCommandConfiguration
	 * (org.rifidi.edge.sensors.commands.AbstractCommandConfiguration,
	 * java.util.Map)
	 */
	@Override
	public void unbindCommandConfiguration(
			AbstractCommandConfiguration<?> commandConfiguration,
			Map<?, ?> properties) {
		if (sensors != null) {
			for (AbstractSensor<?> reader : sensors) {
				reader.unbindCommandConfiguration(commandConfiguration,
						properties);
			}
		}
	}

	/**
	 * Called by spring
	 * 
	 * @param wrapper
	 */
	public void setNotifierService(NotifierService notifierService) {
		this.notifierService = notifierService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.configuration.ServiceFactory#getFactoryID()
	 */
	@Override
	public String getFactoryID() {
		return FACTORY_ID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.base.AbstractSensorFactory#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.base.AbstractSensorFactory#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return displayname;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.configuration.ServiceFactory#createInstance(java
	 * .lang.String)
	 */
	@Override
	public void createInstance(String serviceID)
			throws IllegalArgumentException, InvalidStateException {
		if (serviceID == null) {
			throw new IllegalArgumentException("ServiceID is null");
		}
		if (notifierService == null) {
			throw new InvalidStateException("All services are not set");
		}
		Alien9800Reader instance = new Alien9800Reader(commands);
		instance.setID(serviceID);
		instance.setNotifiyService(notifierService);
		instance.register(getContext(), FACTORY_ID);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.configuration.ServiceFactory#getServiceDescription
	 * (java.lang .String)
	 */
	@Override
	public MBeanInfo getServiceDescription(String factoryID) {
		return (MBeanInfo) Alien9800Reader.mbeaninfo.clone();
	}

}

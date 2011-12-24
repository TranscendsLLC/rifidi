/*
 * 
 * AlienAutonomousSensorFactory.java
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
package org.rifidi.edge.adapter.alien.autonomous;

import java.util.Map;

import javax.management.MBeanInfo;

import org.rifidi.edge.configuration.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.notification.NotifierService;
import org.rifidi.edge.sensors.AbstractCommandConfiguration;
import org.rifidi.edge.sensors.AbstractSensor;
import org.rifidi.edge.sensors.AbstractSensorFactory;

/**
 * This is a factory that registers and produces AlienAutonomousSensors, which
 * are used to receive data from Alien Readers which send data using the
 * autonomous mode
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AlienAutonomousSensorFactory extends
		AbstractSensorFactory<AlienAutonomousSensor> {
	public static final String FACTORY_ID = "Alien-Autonomous";
	/** The service used to send out notifications to the client */
	private NotifierService notifierService;
	/** Blueprint for new readers. */
	private final MBeanInfo readerInfo;

	/**
	 * Constructor.
	 */
	public AlienAutonomousSensorFactory() {
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		readerInfo = strategy.getMBeanInfo(AlienAutonomousSensor.class);
	}

	/**
	 * Called by spring
	 * 
	 * @param notifierService
	 *            the notifierService to set
	 */
	public void setNotifierService(NotifierService notifierService) {
		this.notifierService = notifierService;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.base.AbstractSensorFactory#getDescription()
	 */
	@Override
	public String getDescription() {
		return "The Rifidi Alien autonomous adapter is an endpoint to passively receive tag reads from an "
				+ "Alien reader operating in autonomous mode. You cannot send any commands on the alien autonomous session";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.base.AbstractSensorFactory#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return "Alien Autonomous";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.configuration.ServiceFactory#createInstance(java
	 * .lang.String)
	 */
	@Override
	public void createInstance(String serviceID) {
		AlienAutonomousSensor instance = new AlienAutonomousSensor(commands);
		instance.setID(serviceID);
		instance.setNotifierService(this.notifierService);
		instance.register(getContext(), FACTORY_ID);
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
	 * org.rifidi.edge.configuration.ServiceFactory#getServiceDescription
	 * (java.lang .String)
	 */
	@Override
	public MBeanInfo getServiceDescription(String factoryID) {
		return (MBeanInfo) readerInfo.clone();
	}

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
		for (AbstractSensor<?> sensor : sensors) {
			sensor.unbindCommandConfiguration(commandConfiguration, properties);
		}
	}

}

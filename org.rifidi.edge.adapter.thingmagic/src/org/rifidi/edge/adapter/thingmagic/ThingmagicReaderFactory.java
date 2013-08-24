/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.adapter.thingmagic;

import java.util.Map;

import javax.management.MBeanInfo;

import org.rifidi.edge.exceptions.InvalidStateException;
import org.rifidi.edge.notification.NotifierService;
import org.rifidi.edge.sensors.AbstractCommandConfiguration;
import org.rifidi.edge.sensors.AbstractSensor;
import org.rifidi.edge.sensors.AbstractSensorFactory;

/**
 * 
 * 
 * @author Matthew Dean
 */
public class ThingmagicReaderFactory extends AbstractSensorFactory<ThingmagicReader> {

	/** The Unique FACTORY_ID for this Factory */
	public static final String FACTORY_ID = "ThingMagic";
	/** A JMS event notification sender */
	private volatile NotifierService notifierService;
		
	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.sensors.base.AbstractSensorFactory#bindCommandConfiguration(org.rifidi.edge.sensors.commands.AbstractCommandConfiguration, java.util.Map)
	 */
	@Override
	public void bindCommandConfiguration(
			AbstractCommandConfiguration<?> commandConfiguration,
			Map<?, ?> properties) {
		// TODO: ignored for now.
	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.sensors.base.AbstractSensorFactory#getDescription()
	 */
	@Override
	public String getDescription() {
		return "The Rifidi ThingMagic adapater supports the ThingMagic RQL protocol on the Mercury 4 and Mercury 5 readers";
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
	 * @see org.rifidi.edge.sensors.base.AbstractSensorFactory#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return "ThingMagic";
	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.sensors.base.AbstractSensorFactory#unbindCommandConfiguration(org.rifidi.edge.sensors.commands.AbstractCommandConfiguration, java.util.Map)
	 */
	@Override
	public void unbindCommandConfiguration(
			AbstractCommandConfiguration<?> commandConfiguration,
			Map<?, ?> properties) {
		for (AbstractSensor<?> reader : sensors) {
			reader.unbindCommandConfiguration(commandConfiguration, properties);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.configuration.ServiceFactory#createInstance(java.lang.String)
	 */
	@Override
	public void createInstance(String serviceID) throws IllegalArgumentException, InvalidStateException {
		if (serviceID == null) {
			throw new IllegalArgumentException("ServiceID is null");
		}
		if (notifierService == null) {
			throw new InvalidStateException("All services are not set");
		}
		ThingmagicReader instance = new ThingmagicReader(commands);
		instance.setID(serviceID);
		instance.setNotifiyService(notifierService);
		instance.register(getContext(), FACTORY_ID);
	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.configuration.ServiceFactory#getFactoryID()
	 */
	@Override
	public String getFactoryID() {
		return FACTORY_ID;
	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.configuration.ServiceFactory#getServiceDescription(java.lang.String)
	 */
	@Override
	public MBeanInfo getServiceDescription(String factoryID) {
		return (MBeanInfo) ThingmagicReader.mbeaninfo.clone();
	}

}

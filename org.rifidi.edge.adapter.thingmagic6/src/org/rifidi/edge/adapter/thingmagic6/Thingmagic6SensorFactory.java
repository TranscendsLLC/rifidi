/**
 * 
 */
package org.rifidi.edge.adapter.thingmagic6;

import java.util.Map;

import javax.management.MBeanInfo;

import org.rifidi.edge.exceptions.InvalidStateException;
import org.rifidi.edge.notification.NotifierService;
import org.rifidi.edge.sensors.AbstractCommandConfiguration;
import org.rifidi.edge.sensors.AbstractSensorFactory;

/**
 * 
 * 
 * 
 * @author Matthew Dean
 */
public class Thingmagic6SensorFactory extends
		AbstractSensorFactory<Thingmagic6Sensor> {

	/** A JMS event notification sender */
	private volatile NotifierService notifierService;

	/** The ID for this factory */
	public static final String FACTORY_ID = "Thingmagic6";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.AbstractSensorFactory#bindCommandConfiguration
	 * (org.rifidi.edge.sensors.AbstractCommandConfiguration, java.util.Map)
	 */
	@Override
	public void bindCommandConfiguration(
			AbstractCommandConfiguration<?> commandConfiguration,
			Map<?, ?> properties) {
		// Nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.AbstractSensorFactory#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Sensor plugin for Thingmagic 6 readers";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.AbstractSensorFactory#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return FACTORY_ID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.AbstractSensorFactory#unbindCommandConfiguration
	 * (org.rifidi.edge.sensors.AbstractCommandConfiguration, java.util.Map)
	 */
	@Override
	public void unbindCommandConfiguration(
			AbstractCommandConfiguration<?> commandConfiguration,
			Map<?, ?> properties) {
		// Nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.configuration.ServiceFactory#createInstance(java.lang
	 * .String)
	 */
	@Override
	public void createInstance(String serviceID)
			throws IllegalArgumentException, InvalidStateException {
		if (serviceID == null) {
			throw new IllegalArgumentException("Service ID is null");
		}
		if (notifierService == null) {
			throw new InvalidStateException("Required services are null");
		}
		Thingmagic6Sensor instance = new Thingmagic6Sensor(commands);
		instance.setID(serviceID);
		instance.setNotifiyService(notifierService);
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
	 * @see org.rifidi.edge.configuration.ServiceFactory#getServiceDescription
	 * (java.lang .String)
	 */
	@Override
	public MBeanInfo getServiceDescription(String factoryID) {
		return (MBeanInfo) Thingmagic6Sensor.mbeaninfo.clone();
	}

	/**
	 * Called by spring.
	 * 
	 * @param wrapper
	 */
	public void setNotifierService(NotifierService wrapper) {
		this.notifierService = wrapper;
	}

}

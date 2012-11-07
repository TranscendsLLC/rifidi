/**
 * 
 */
package org.rifidi.edge.adapter.thinkify50;

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
 * @author Matthew Dean - matt@transcends.co
 */
public class Thinkify50SensorFactory extends
		AbstractSensorFactory<Thinkify50Sensor> {

	/** A JMS event notification sender */
	private volatile NotifierService notifierService;

	/** The ID for this factory */
	public static final String FACTORY_ID = Thinkify50Constants.FACTORY_ID;

	@Override
	public void createInstance(String serviceID)
			throws IllegalArgumentException, InvalidStateException {
		if (serviceID == null) {
			throw new IllegalArgumentException("Service ID is null");
		}
		if (notifierService == null) {
			throw new InvalidStateException("Required services are null");
		}
		Thinkify50Sensor instance = new Thinkify50Sensor(commands);
		instance.setID(serviceID);
		instance.setNotifiyService(notifierService);
		instance.register(getContext(), FACTORY_ID);
	}

	@Override
	public MBeanInfo getServiceDescription(String factoryID) {
		return (MBeanInfo) Thinkify50Sensor.mbeaninfo.clone();
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
	 * @see org.rifidi.edge.sensors.AbstractSensorFactory#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return FACTORY_ID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.AbstractSensorFactory#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Sensor plugin for Thinkify TR50 readers";
	}

	@Override
	public void bindCommandConfiguration(
			AbstractCommandConfiguration<?> commandConfiguration,
			Map<?, ?> properties) {
		// Nothing

	}

	@Override
	public void unbindCommandConfiguration(
			AbstractCommandConfiguration<?> commandConfiguration,
			Map<?, ?> properties) {
		for (AbstractSensor<?> reader : sensors) {
			reader.unbindCommandConfiguration(commandConfiguration, properties);
		}
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

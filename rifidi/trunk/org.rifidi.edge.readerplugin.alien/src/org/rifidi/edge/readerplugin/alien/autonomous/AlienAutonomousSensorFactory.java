/**
 * 
 */
package org.rifidi.edge.readerplugin.alien.autonomous;

import java.util.Map;

import javax.management.MBeanInfo;

import org.rifidi.edge.core.configuration.mbeanstrategies.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.base.AbstractSensorFactory;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.services.notification.NotifierService;
import org.springframework.jms.core.JmsTemplate;

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
	public static final String FACTORY_ID = "AlienAutonomous";
	/** The service used to send out notifications to the client */
	private NotifierService notifierService;
	/** The template used to send out tags */
	private JmsTemplate template;
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

	/**
	 * called by spring
	 * 
	 * @param template
	 *            the template to set
	 */
	public void setTemplate(JmsTemplate template) {
		this.template = template;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.AbstractSensorFactory#getDescription()
	 */
	@Override
	public String getDescription() {
		return "A sensor adapter that recieves data from Alien readers using autonomous mode";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.AbstractSensorFactory#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return "Alien Autonomous Sensor";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.configuration.ServiceFactory#createInstance(java
	 * .lang.String)
	 */
	@Override
	public void createInstance(String serviceID) {
		AlienAutonomousSensor instance = new AlienAutonomousSensor(commands);
		instance.setID(serviceID);
		instance.setTemplate((JmsTemplate) template);
		instance.setNotifierService(this.notifierService);
		instance.register(getContext(), FACTORY_ID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.configuration.ServiceFactory#getFactoryID()
	 */
	@Override
	public String getFactoryID() {
		return FACTORY_ID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.configuration.ServiceFactory#getServiceDescription
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
	 * (org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration,
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
	 * (org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration,
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

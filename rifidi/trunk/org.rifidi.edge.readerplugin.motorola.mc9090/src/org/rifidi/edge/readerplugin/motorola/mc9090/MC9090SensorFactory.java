/**
 * 
 */
package org.rifidi.edge.readerplugin.motorola.mc9090;

import java.util.Map;

import javax.management.MBeanInfo;

import org.rifidi.edge.core.configuration.mbeanstrategies.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.core.exceptions.InvalidStateException;
import org.rifidi.edge.core.sensors.base.AbstractSensorFactory;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.services.notification.NotifierService;
import org.springframework.jms.core.JmsTemplate;

/**
 * Creates adapters for Motorola MC9090 handheld readers that the rifidi
 * software installed on them
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class MC9090SensorFactory extends AbstractSensorFactory<MC9090Sensor> {

	public static final String FACTORY_ID = "MC9090";
	/** The service used to send out notifications to the client */
	private NotifierService notifierService;
	/** The template used to send out tags */
	private JmsTemplate template;
	/** Blueprint for new readers. */
	private final MBeanInfo readerInfo;

	public MC9090SensorFactory() {
		super();
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		readerInfo = strategy.getMBeanInfo(MC9090Sensor.class);
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
		return "A sensor adapter that recieves data from Motorola MC9090 "
				+ "handheld readers running the Rifidi application";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.AbstractSensorFactory#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return "Motorola MC9090";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.configuration.ServiceFactory#createInstance(java
	 * .lang.String, java.lang.String)
	 */
	@Override
	public void createInstance(String serviceID)
			throws IllegalArgumentException, InvalidStateException {
		if (serviceID == null) {
			throw new IllegalArgumentException("ServiceID is null");
		}
		if (template == null || notifierService == null) {
			throw new InvalidStateException("All services are not set");
		}
		MC9090Sensor instance = new MC9090Sensor(commands);
		instance.setID(serviceID);
		instance.setTemplate((JmsTemplate) template);
		instance.setNotifierService(notifierService);
		instance.register(getContext(), getFactoryID());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.configuration.ServiceFactory#getServiceDescription
	 * (java.lang.String)
	 */
	@Override
	public MBeanInfo getServiceDescription(String factoryID) {
		return this.readerInfo;
	}

	@Override
	public void bindCommandConfiguration(
			AbstractCommandConfiguration<?> commandConfiguration,
			Map<?, ?> properties) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unbindCommandConfiguration(
			AbstractCommandConfiguration<?> commandConfiguration,
			Map<?, ?> properties) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getFactoryID() {
		return FACTORY_ID;
	}

}

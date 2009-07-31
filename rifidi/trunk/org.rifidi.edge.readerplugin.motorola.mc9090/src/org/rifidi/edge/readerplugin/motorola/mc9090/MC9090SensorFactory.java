/**
 * 
 */
package org.rifidi.edge.readerplugin.motorola.mc9090;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanInfo;

import org.rifidi.edge.core.configuration.ConfigurationType;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.base.AbstractSensorFactory;
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
		this.readerInfo = (new MC9090Sensor()).getMBeanInfo();
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
	 * org.rifidi.edge.core.configuration.impl.AbstractServiceFactory#getClazz()
	 */
	@Override
	public Class<MC9090Sensor> getClazz() {
		return MC9090Sensor.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.configuration.ServiceFactory#getFactoryIDs()
	 */
	@Override
	public List<String> getFactoryIDs() {
		List<String> ids = new ArrayList<String>();
		ids.add(FACTORY_ID);
		return ids;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.configuration.ServiceFactory#createInstance(java
	 * .lang.String, java.lang.String)
	 */
	@Override
	public void createInstance(String factoryID, String serviceID) {
		assert (factoryID.equals(FACTORY_ID));
		MC9090Sensor instance = new MC9090Sensor();
		instance.setID(serviceID);
		instance.setTemplate((JmsTemplate) template);
		instance.setNotifierService(notifierService);
		Set<String> interfaces = new HashSet<String>();
		interfaces.add(AbstractSensor.class.getName());
		Map<String, String> parms = new HashMap<String, String>();
		parms.put("type", ConfigurationType.READER.toString());
		instance.register(getContext(), interfaces, parms);

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

}

/**
 * 
 */
package org.rifidi.edge.readerplugin.alien;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanInfo;

import org.rifidi.configuration.ConfigurationType;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.base.AbstractSensorFactory;
import org.rifidi.edge.core.services.notification.NotifierService;
import org.springframework.jms.core.JmsTemplate;

/**
 * The Factory for producing Alien9800 Readers
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class Alien9800ReaderFactory extends
		AbstractSensorFactory<Alien9800Reader> {

	/** JMS template for sending tag data to JMS Queue */
	private JmsTemplate template;
	/** The Unique FACTORY_ID for this Factory */
	public static final String FACTORY_ID = "Alien9800";
	/** Description of the sensorSession. */
	private static final String description = "The Alien 9800 is an IP based RFID SensorSession using a telnet interface.";
	/** Name of the sensorSession. */
	private static final String name = "Alien9800";
	/** A JMS event notification sender */
	private NotifierService notifierService;
	/** Blueprint for a reader. */
	private final MBeanInfo readerInfo;

	/**
	 * Constructor.
	 */
	public Alien9800ReaderFactory() {
		super();
		readerInfo = (new Alien9800Reader()).getMBeanInfo();
	}

	/**
	 * Called by spring
	 * 
	 * @param wrapper
	 */
	public void setNotifierService(NotifierService notifierService) {
		this.notifierService = notifierService;
	}

	/**
	 * @return the template
	 */
	public JmsTemplate getTemplate() {
		return template;
	}

	/**
	 * Called by spring
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
	 * @see org.rifidi.configuration.AbstractServiceFactory#getClazz()
	 */
	@Override
	public Class<Alien9800Reader> getClazz() {
		return Alien9800Reader.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.configuration.ServiceFactory#getFactoryID()
	 */
	@Override
	public List<String> getFactoryIDs() {
		List<String> ret = new ArrayList<String>();
		ret.add(FACTORY_ID);
		return ret;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getDisplayName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.configuration.ServiceFactory#createInstance(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void createInstance(String factoryID, String serviceID) {
		assert (factoryID.equals(FACTORY_ID));
		Alien9800Reader instance = new Alien9800Reader();
		instance.setID(serviceID);
		instance.setTemplate((JmsTemplate) template);
		instance.setNotifiyService(notifierService);
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
	 * org.rifidi.configuration.ServiceFactory#getServiceDescription(java.lang
	 * .String)
	 */
	@Override
	public MBeanInfo getServiceDescription(String factoryID) {
		return (MBeanInfo) readerInfo.clone();
	}

}

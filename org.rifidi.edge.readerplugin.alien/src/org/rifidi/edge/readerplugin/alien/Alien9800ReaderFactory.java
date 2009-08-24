/**
 * 
 */
package org.rifidi.edge.readerplugin.alien;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.management.MBeanInfo;

import org.rifidi.edge.core.configuration.ConfigurationType;
import org.rifidi.edge.core.configuration.mbeanstrategies.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.base.AbstractSensorFactory;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;
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
	/** Provided by spring. */
	private AtomicReference<Set<AbstractCommandConfiguration<AbstractAlien9800Command>>> commands = new AtomicReference<Set<AbstractCommandConfiguration<AbstractAlien9800Command>>>();

	/**
	 * Constructor.
	 */
	public Alien9800ReaderFactory() {
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		readerInfo = strategy.getMBeanInfo(Alien9800Reader.class);
	}

	/**
	 * @param commands
	 *            the commands to set
	 */
	public void setCommands(
			Set<AbstractCommandConfiguration<AbstractAlien9800Command>> commands) {
		this.commands.set(commands);
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
	 * @see org.rifidi.edge.core.configuration.ServiceFactory#getFactoryID()
	 */
	@Override
	public String getFactoryID() {
		return FACTORY_ID;
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
	 * org.rifidi.edge.core.configuration.ServiceFactory#createInstance(java
	 * .lang.String)
	 */
	@Override
	public void createInstance(String serviceID) {
		Alien9800Reader instance = new Alien9800Reader(commands.get());
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
	 * org.rifidi.edge.core.configuration.ServiceFactory#getServiceDescription
	 * (java.lang .String)
	 */
	@Override
	public MBeanInfo getServiceDescription(String factoryID) {
		return (MBeanInfo) readerInfo.clone();
	}

}

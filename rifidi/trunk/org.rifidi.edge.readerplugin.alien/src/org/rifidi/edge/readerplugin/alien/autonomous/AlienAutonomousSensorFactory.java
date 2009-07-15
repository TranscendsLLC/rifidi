/**
 * 
 */
package org.rifidi.edge.readerplugin.alien.autonomous;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.base.AbstractSensorFactory;
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
	public static final String ID = "AlienAutonomous";
	/** The service used to send out notifications to the client */
	private NotifierService notifierService;
	/** The template used to send out tags */
	private JmsTemplate template;

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
	 * org.rifidi.configuration.impl.AbstractServiceFactory#customConfig(org
	 * .rifidi.configuration.RifidiService)
	 */
	@Override
	public void customConfig(AlienAutonomousSensor instance) {
		instance.setNotifierService(notifierService);
		instance.setTemplate(template);
		Set<String> interfaces = new HashSet<String>();
		interfaces.add(AbstractSensor.class.getName());
		instance.register(getContext(), interfaces);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.configuration.impl.AbstractServiceFactory#getClazz()
	 */
	@Override
	public Class<AlienAutonomousSensor> getClazz() {
		return AlienAutonomousSensor.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.configuration.ServiceFactory#getFactoryIDs()
	 */
	@Override
	public List<String> getFactoryIDs() {
		List<String> ids = new ArrayList<String>();
		ids.add(ID);
		return ids;
	}

}

/*
 *  LLRPReaderFactory.java
 *
 *  Created:	Mar 9, 2009
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.llrp;

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
 * A factory class to create LLRPReaders.
 * 
 * @author Matthew Dean
 */
public class LLRPReaderFactory extends AbstractSensorFactory<LLRPReader> {

	/** Description of the sensorSession. */
	private static final String description = "The LLRP is an EPC standard protocol";
	/** Name of the sensorSession. */
	private static final String name = "LLRP";
	/** A JMS event notification sender */
	private NotifierService notifierService;
	/** The ID for this factory */
	public static final String FACTORY_ID = "LLRP";
	/** Template for sending jms messages. */
	private volatile JmsTemplate template;
	/** Blueprint for the reader. */
	private final MBeanInfo readerInfo;

	/**
	 * The constructor for the factory class.
	 */
	public LLRPReaderFactory() {
		readerInfo = (new LLRPReader()).getMBeanInfo();
	}

	/**
	 * @param template
	 *            the template to set
	 */
	public void setTemplate(JmsTemplate template) {
		this.template = template;
	}

	/**
	 * Called by spring.
	 * 
	 * @param wrapper
	 */
	public void setNotifierService(NotifierService wrapper) {
		this.notifierService = wrapper;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.configuration.impl.AbstractServiceFactory#getClazz()
	 */
	@Override
	public Class<LLRPReader> getClazz() {
		return LLRPReader.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.configuration.ServiceFactory#getFactoryIDs()
	 */
	@Override
	public List<String> getFactoryIDs() {
		List<String> ret = new ArrayList<String>();
		ret.add(FACTORY_ID);
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readers.AbstractReader#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readers.AbstractReaderFactory#getDisplayName()
	 */
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
		LLRPReader instance = new LLRPReader();
		instance.setID(serviceID);
		instance.setDestination(template.getDefaultDestination());
		instance.setTemplate(template);
		instance.setNotifiyService(this.notifierService);
		Set<String> interfaces = new HashSet<String>();
		interfaces.add(AbstractSensor.class.getCanonicalName());
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

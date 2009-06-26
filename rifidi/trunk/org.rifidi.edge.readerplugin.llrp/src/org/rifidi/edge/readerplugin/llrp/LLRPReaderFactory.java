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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.rifidi.edge.core.readers.AbstractReader;
import org.rifidi.edge.core.readers.AbstractReaderFactory;
import org.rifidi.edge.core.services.notification.NotifierService;
import org.springframework.jms.core.JmsTemplate;

/**
 * A factory class to create LLRPReaders.
 * 
 * @author Matthew Dean
 */
public class LLRPReaderFactory extends AbstractReaderFactory<LLRPReader> {

	/** Description of the readerSession. */
	private static final String description = "The LLRP is an EPC standard protocol";
	/** Name of the readerSession. */
	private static final String name = "LLRP";
	/** A JMS event notification sender */
	private NotifierService notifierService;
	/** JMS Template */
	private JmsTemplate template;
	/** The ID for this factory */
	public static final String FACTORY_ID = "LLRP";

	/**
	 * The constructor for the factory class.
	 */
	public LLRPReaderFactory() {
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
	 * @see org.rifidi.configuration.AbstractServiceFactory#customConfig(java
	 * .lang.Object)
	 */
	@Override
	public void customConfig(LLRPReader instance) {
		instance.setDestination(template.getDefaultDestination());
		instance.setTemplate(template);
		instance.setNotifiyService(this.notifierService);
		Set<String> interfaces = new HashSet<String>();
		interfaces.add(AbstractReader.class.getName());
		instance.register(getContext(), interfaces);
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

	/**
	 * Sets the JMS template.
	 * 
	 * @param template
	 *            the template to set
	 */
	public void setTemplate(JmsTemplate template) {
		this.template = template;
	}

}

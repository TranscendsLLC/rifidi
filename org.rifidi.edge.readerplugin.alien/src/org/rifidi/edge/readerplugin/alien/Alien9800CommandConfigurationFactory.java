/*
 *  Alien9800CommandConfigurationFactory.java
 *
 *  Created:	Mar 9, 2009
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.alien;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rifidi.edge.core.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.commands.AbstractCommandConfigurationFactory;
import org.rifidi.edge.readerplugin.alien.commands.AlienGetTagListCommandConfiguration;

/**
 * The factory for Alien Command classes. 
 * 
 * TODO: Comments
 * 
 * @author Jochen Mader - jochen@pramari.com
 */
public class Alien9800CommandConfigurationFactory extends
		AbstractCommandConfigurationFactory {

	private Map<String, Class<?>> factoryIdToClass;
	/** The gloabaly unique name for this factory */
	public static final String uniqueID = "Alien9800CommandConfigurationFactory";

	/**
	 * Constructor for Alien9800CommandConfigurationFactory.  
	 */
	public Alien9800CommandConfigurationFactory() {
		super();
		factoryIdToClass = new HashMap<String, Class<?>>();
		factoryIdToClass.put("Alien9800-GetTagList",
				AlienGetTagListCommandConfiguration.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.configuration.AbstractMultiServiceFactory#customInit(java.
	 * lang.Object)
	 */
	@Override
	public void customInit(Object instance) {
		if (instance instanceof AbstractCommandConfiguration<?>) {
			AbstractCommandConfiguration<?> cc = (AbstractCommandConfiguration<?>) instance;
			Set<String> intefaces = new HashSet<String>();
			intefaces.add(AbstractCommandConfiguration.class.getName());
			cc.register(getContext(), intefaces);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.configuration.AbstractMultiServiceFactory#getFactoryIDToClass
	 * ()
	 */
	@Override
	public Map<String, Class<?>> getFactoryIDToClass() {
		return new HashMap<String, Class<?>>(factoryIdToClass);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.configuration.ServiceFactory#getFactoryIDs()
	 */
	@Override
	public List<String> getFactoryIDs() {
		return new ArrayList<String>(factoryIdToClass.keySet());
	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.core.commands.AbstractCommandConfigurationFactory#getReaderFactoryID()
	 */
	@Override
	public String getReaderFactoryID() {
		return Alien9800ReaderFactory.FACTORY_ID;
	}

}

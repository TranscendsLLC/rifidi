/*
 *  LLRPCommandConfigurationFactory.java
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

import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfigurationFactory;
import org.rifidi.edge.readerplugin.llrp.commands.LLRPDeleteROSpecCommandConfiguration;
import org.rifidi.edge.readerplugin.llrp.commands.LLRPGetTagListCommandConfiguration;
import org.rifidi.edge.readerplugin.llrp.commands.LLRPROSpecCommandConfiguration;

/**
 * The configuration factory for LLRP commands.  
 * 
 * @author Matthew Dean
 */
public class LLRPCommandConfigurationFactory extends
		AbstractCommandConfigurationFactory {

	private Map<String, Class<?>> factoryIdToClass;

	/**
	 * LLRPCommandConfigurationFactory 
	 */
	public LLRPCommandConfigurationFactory() {
		super();
		factoryIdToClass = new HashMap<String, Class<?>>();
		factoryIdToClass.put("LLRP-GetTagList",
				LLRPGetTagListCommandConfiguration.class);
		factoryIdToClass.put("LLRP-CreateROSpec", LLRPROSpecCommandConfiguration.class);
		factoryIdToClass.put("LLRP-DeleteROSpec", LLRPDeleteROSpecCommandConfiguration.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.configuration.impl.AbstractMultiServiceFactory#customInit(
	 * java.lang.Object)
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
	 * org.rifidi.configuration.impl.AbstractMultiServiceFactory#getFactoryIDToClass
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
	 * 
	 * @seeorg.rifidi.edge.core.commands.AbstractCommandConfigurationFactory#
	 * getReaderFactoryID()
	 */
	@Override
	public String getReaderFactoryID() {
		return LLRPReaderFactory.FACTORY_ID;
	}

}

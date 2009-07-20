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

import javax.management.MBeanInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.configuration.ConfigurationType;
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
	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(LLRPCommandConfigurationFactory.class);
	private final Map<String, Class<?>> factoryIdToClass;
	private final Map<String, MBeanInfo> commandIdToInfo;

	/**
	 * LLRPCommandConfigurationFactory
	 */
	public LLRPCommandConfigurationFactory() {
		super();
		factoryIdToClass = new HashMap<String, Class<?>>();
		factoryIdToClass.put("LLRP-GetTagList",
				LLRPGetTagListCommandConfiguration.class);
		factoryIdToClass.put("LLRP-CreateROSpec",
				LLRPROSpecCommandConfiguration.class);
		factoryIdToClass.put("LLRP-DeleteROSpec",
				LLRPDeleteROSpecCommandConfiguration.class);
		commandIdToInfo = new HashMap<String, MBeanInfo>();
		commandIdToInfo.put("LLRP-GetTagList",
				(new LLRPGetTagListCommandConfiguration()).getMBeanInfo());
		commandIdToInfo.put("LLRP-CreateROSpec",
				(new LLRPROSpecCommandConfiguration()).getMBeanInfo());
		commandIdToInfo.put("LLRP-DeleteROSpec",
				(new LLRPDeleteROSpecCommandConfiguration()).getMBeanInfo());
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.configuration.ServiceFactory#createInstance(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void createInstance(String factoryID, String serviceID) {
		try {
			AbstractCommandConfiguration command = (AbstractCommandConfiguration) factoryIdToClass
					.get(factoryID).newInstance();
			command.setID(serviceID);
			Set<String> interfaces = new HashSet<String>();
			interfaces.add(AbstractCommandConfiguration.class
					.getCanonicalName());
			Map<String, String> parms = new HashMap<String, String>();
			parms.put("type", ConfigurationType.COMMAND.toString());
			command.register(getContext(), interfaces, parms);
		} catch (InstantiationException e) {
			logger.error(e);
		} catch (IllegalAccessException e) {
			logger.error(e);
		}
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
		return commandIdToInfo.get(factoryID);
	}
}

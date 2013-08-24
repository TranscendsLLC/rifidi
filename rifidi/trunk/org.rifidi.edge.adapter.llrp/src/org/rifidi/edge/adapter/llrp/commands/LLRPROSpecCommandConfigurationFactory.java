/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.adapter.llrp.commands;

import javax.management.MBeanInfo;

import org.rifidi.edge.adapter.llrp.LLRPReaderFactory;
import org.rifidi.edge.configuration.AbstractCommandConfigurationFactory;
import org.rifidi.edge.configuration.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.sensors.AbstractCommandConfiguration;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class LLRPROSpecCommandConfigurationFactory extends
		AbstractCommandConfigurationFactory<AbstractCommandConfiguration<?>> {
	/** MBeanInfo for this class. */
	public static final MBeanInfo mbeaninfo;
	static {
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		mbeaninfo = strategy.getMBeanInfo(LLRPROSpecCommandConfiguration.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.configuration.impl.AbstractCommandConfigurationFactory
	 * #getReaderFactoryID()
	 */
	@Override
	public String getReaderFactoryID() {
		return LLRPReaderFactory.FACTORY_ID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.configuration.ServiceFactory#createInstance(java
	 * .lang.String)
	 */
	@Override
	public void createInstance(String serviceID) {
		LLRPROSpecCommandConfiguration commandConfig = new LLRPROSpecCommandConfiguration();
		commandConfig.setID(serviceID);
		commandConfig.register(getContext(), LLRPReaderFactory.FACTORY_ID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.configuration.ServiceFactory#getFactoryID()
	 */
	@Override
	public String getFactoryID() {
		return "LLRP-Configure";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.configuration.ServiceFactory#getServiceDescription
	 * (java.lang.String)
	 */
	@Override
	public MBeanInfo getServiceDescription(String factoryID) {
		return (MBeanInfo) mbeaninfo.clone();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.configuration.impl.AbstractCommandConfigurationFactory
	 * #getCommandDescription()
	 */
	@Override
	public String getCommandDescription() {
		return "Configure the LLRP reader to collect tags.  By default, " +
				"this command will cause the LLRP reader to push back tag reads " +
				"automatically.  If you would rather use the Poll mode, you " +
				"will need to use the LLRP Poll command. To use the LLRP Configure " +
				"command, submit it for a one-time execution";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.configuration.impl.AbstractCommandConfigurationFactory
	 * #getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return "LLRP Configure";
	}

}

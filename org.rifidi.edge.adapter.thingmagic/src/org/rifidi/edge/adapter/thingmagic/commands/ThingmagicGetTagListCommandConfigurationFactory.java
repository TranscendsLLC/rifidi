/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.adapter.thingmagic.commands;

import javax.management.MBeanInfo;

import org.rifidi.edge.adapter.thingmagic.ThingmagicReaderFactory;
import org.rifidi.edge.configuration.AbstractCommandConfigurationFactory;
import org.rifidi.edge.configuration.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.exceptions.InvalidStateException;

/**
 * 
 * 
 * @author Matthew Dean
 */
public class ThingmagicGetTagListCommandConfigurationFactory
		extends
		AbstractCommandConfigurationFactory<ThingmagicGetTagListCommandConfiguration> {

	/** Name of the command. */
	public static final String name = "ThingMagic-Poll";
	/** Mbeaninfo for this class. */
	public static final MBeanInfo mbeaninfo;
	static {
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		mbeaninfo = strategy
				.getMBeanInfo(ThingmagicGetTagListCommandConfiguration.class);
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
		return ThingmagicReaderFactory.FACTORY_ID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.configuration.ServiceFactory#createInstance(java
	 * .lang.String)
	 */
	@Override
	public void createInstance(String serviceID)
			throws IllegalArgumentException, InvalidStateException {
		ThingmagicGetTagListCommandConfiguration commandConfig = new ThingmagicGetTagListCommandConfiguration();
		commandConfig.setID(serviceID);
		commandConfig
				.register(getContext(), ThingmagicReaderFactory.FACTORY_ID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.configuration.ServiceFactory#getFactoryID()
	 */
	@Override
	public String getFactoryID() {
		return name;
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
		return "Poll the ThingMagic reader for its tags. "
				+ "For monitoring of the read zone, submit this command for "
				+ "recurring execution.";
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
		return "ThingMagic Poll";
	}

}

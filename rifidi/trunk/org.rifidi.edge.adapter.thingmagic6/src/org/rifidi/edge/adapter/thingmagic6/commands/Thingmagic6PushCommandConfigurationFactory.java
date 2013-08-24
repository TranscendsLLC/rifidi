/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.adapter.thingmagic6.commands;

import javax.management.MBeanInfo;

import org.rifidi.edge.adapter.thingmagic6.Thingmagic6SensorFactory;
import org.rifidi.edge.configuration.AbstractCommandConfigurationFactory;
import org.rifidi.edge.configuration.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.exceptions.InvalidStateException;

/**
 * @author matt
 *
 */
public class Thingmagic6PushCommandConfigurationFactory
		extends
		AbstractCommandConfigurationFactory<Thingmagic6PushCommandConfiguration> {
	
	/** Name of the command. */
	public static final String ID = "Thingmagic6-Push";
	/** Mbeaninfo for this class. */
	public static final MBeanInfo mbeaninfo;
	static {
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		mbeaninfo = strategy
				.getMBeanInfo(Thingmagic6PushCommandConfiguration.class);
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.configuration.AbstractCommandConfigurationFactory#getCommandDescription()
	 */
	@Override
	public String getCommandDescription() {
		return "Sets the Thingmagic to push tags back to the edge server";
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.configuration.AbstractCommandConfigurationFactory#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return "Thingmagic6 Push";
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.configuration.AbstractCommandConfigurationFactory#getReaderFactoryID()
	 */
	@Override
	public String getReaderFactoryID() {
		return Thingmagic6SensorFactory.FACTORY_ID;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.configuration.ServiceFactory#createInstance(java.lang.String)
	 */
	@Override
	public void createInstance(String serviceID)
			throws IllegalArgumentException, InvalidStateException {
		System.out.println("Creating an instance of Thingmagic6PushCommand!  ");
		Thingmagic6PushCommandConfiguration config = new Thingmagic6PushCommandConfiguration();
		config.setID(serviceID);
		config.register(getContext(), Thingmagic6SensorFactory.FACTORY_ID);
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.configuration.ServiceFactory#getFactoryID()
	 */
	@Override
	public String getFactoryID() {
		return ID;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.configuration.ServiceFactory#getServiceDescription(java.lang.String)
	 */
	@Override
	public MBeanInfo getServiceDescription(String factoryID) {
		return (MBeanInfo) mbeaninfo.clone();
	}

}

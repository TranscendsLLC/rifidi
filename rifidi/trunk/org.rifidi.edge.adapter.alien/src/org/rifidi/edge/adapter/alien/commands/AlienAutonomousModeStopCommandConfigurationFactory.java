/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
/**
 * 
 */
package org.rifidi.edge.adapter.alien.commands;

import javax.management.MBeanInfo;

import org.rifidi.edge.adapter.alien.Alien9800ReaderFactory;
import org.rifidi.edge.configuration.AbstractCommandConfigurationFactory;
import org.rifidi.edge.configuration.AnnotationMBeanInfoStrategy;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class AlienAutonomousModeStopCommandConfigurationFactory
		extends
		AbstractCommandConfigurationFactory<AlienGetTagListCommandConfiguration> {
	/** Name of the command. */
	public static final String ID = "Alien-Push-Stop";
	/** Mbeaninfo for this class. */
	public static final MBeanInfo mbeaninfo;
	static {
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		mbeaninfo = strategy
				.getMBeanInfo(AlienAutonomousModeStopCommandConfiguration.class);
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
		return Alien9800ReaderFactory.FACTORY_ID;
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
		AlienAutonomousModeStopCommandConfiguration commandConfig = new AlienAutonomousModeStopCommandConfiguration();
		commandConfig.setID(serviceID);
		commandConfig.register(getContext(), Alien9800ReaderFactory.FACTORY_ID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.configuration.ServiceFactory#getFactoryID()
	 */
	@Override
	public String getFactoryID() {
		return ID;
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
		return "Command the Alien reader to stop sending back tags in autonomous mode.  "
				+ "To use, submit this command for a one-time execution.";
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
		return "Alien Push Stop";
	}

}

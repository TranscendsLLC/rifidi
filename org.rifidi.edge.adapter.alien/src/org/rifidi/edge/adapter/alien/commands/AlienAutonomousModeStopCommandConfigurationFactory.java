/*
 * AlienAutonomousModeStopCommandConfigurationFactory.java
 * 
 * Created:     July 22nd, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:     The software in this package is published under the terms of the EPL License
 *                   A copy of the license is included in this distribution under Rifidi-License.txt 
 */
/**
 * 
 */
package org.rifidi.edge.adapter.alien.commands;

import javax.management.MBeanInfo;

import org.rifidi.edge.adapter.alien.Alien9800ReaderFactory;
import org.rifidi.edge.core.configuration.impl.AbstractCommandConfigurationFactory;
import org.rifidi.edge.core.configuration.mbeanstrategies.AnnotationMBeanInfoStrategy;

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
	 * org.rifidi.edge.core.configuration.impl.AbstractCommandConfigurationFactory
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
	 * org.rifidi.edge.core.configuration.ServiceFactory#createInstance(java
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
	 * @see org.rifidi.edge.core.configuration.ServiceFactory#getFactoryID()
	 */
	@Override
	public String getFactoryID() {
		return ID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.configuration.ServiceFactory#getServiceDescription
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
	 * org.rifidi.edge.core.configuration.impl.AbstractCommandConfigurationFactory
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
	 * org.rifidi.edge.core.configuration.impl.AbstractCommandConfigurationFactory
	 * #getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return "Alien Push Stop";
	}

}

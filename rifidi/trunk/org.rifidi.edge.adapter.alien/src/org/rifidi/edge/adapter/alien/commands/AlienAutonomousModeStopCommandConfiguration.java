/*
 * AlienAutonomousModeStopCommandConfiguration.java
 * 
 * Created:     July 22nd, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:     The software in this package is published under the terms of the EPL License
 *                   A copy of the license is included in this distribution under Rifidi-License.txt 
 */

package org.rifidi.edge.adapter.alien.commands;

import javax.management.MBeanInfo;

import org.rifidi.edge.configuration.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.sensors.AbstractCommandConfiguration;

/**
 * @author kyle
 * 
 */
public class AlienAutonomousModeStopCommandConfiguration
		extends
			AbstractCommandConfiguration<AlienAutonomousModeStopCommand> {

	/** The name of this command type */
	public static final String name = "Alien-Autonomous-Stop";
	public static final MBeanInfo mbeaninfo;
	static{
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		mbeaninfo = strategy.getMBeanInfo(AlienAutonomousModeStopCommandConfiguration.class);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.commands.AbstractCommandConfiguration#getCommand
	 * (java.lang.String)
	 */
	@Override
	public AlienAutonomousModeStopCommand getCommand(String readerID) {
		return new AlienAutonomousModeStopCommand(super.getID());
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.configuration.RifidiService#getMBeanInfo()
	 */
	@Override
	public MBeanInfo getMBeanInfo() {
		return (MBeanInfo)mbeaninfo.clone();
	}

}

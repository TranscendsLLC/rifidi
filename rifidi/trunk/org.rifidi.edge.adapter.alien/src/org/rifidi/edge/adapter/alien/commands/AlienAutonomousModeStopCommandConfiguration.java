/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
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

/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.adapter.thingmagic6.commands;

import javax.management.MBeanInfo;

import org.rifidi.edge.configuration.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.configuration.JMXMBean;
import org.rifidi.edge.sensors.AbstractCommandConfiguration;

/**
 * 
 * 
 * 
 * @author Matthew Dean
 */
@JMXMBean
public class Thingmagic6PushCommandConfiguration extends
		AbstractCommandConfiguration<Thingmagic6PushCommand> {
	
	
	public Thingmagic6PushCommandConfiguration() {
		System.out.println("In the push command constructor!  ");
	}
	
	public static final MBeanInfo mbeaninfo;
	static {
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		mbeaninfo = strategy
				.getMBeanInfo(Thingmagic6PushCommandConfiguration.class);
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.sensors.AbstractCommandConfiguration#getCommand(java.lang.String)
	 */
	@Override
	public Thingmagic6PushCommand getCommand(String readerID) {
		Thingmagic6PushCommand command = new Thingmagic6PushCommand(super.getID());
		System.out.println("Called getCommand");
		return command;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.configuration.RifidiService#getMBeanInfo()
	 */
	@Override
	public MBeanInfo getMBeanInfo() {
		return (MBeanInfo) mbeaninfo.clone();
	}

}

/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
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

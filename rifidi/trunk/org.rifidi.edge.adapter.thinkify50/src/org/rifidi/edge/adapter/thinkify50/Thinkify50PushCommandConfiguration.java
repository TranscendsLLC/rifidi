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
package org.rifidi.edge.adapter.thinkify50;

import javax.management.MBeanInfo;

import org.rifidi.edge.configuration.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.configuration.JMXMBean;
import org.rifidi.edge.sensors.AbstractCommandConfiguration;

/**
 * 
 * 
 * @author matt
 */
@JMXMBean
public class Thinkify50PushCommandConfiguration extends
		AbstractCommandConfiguration<Thinkify50PushCommand> {

	public static final MBeanInfo mbeaninfo;
	static {
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		mbeaninfo = strategy
				.getMBeanInfo(Thinkify50PushCommandConfiguration.class);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.sensors.AbstractCommandConfiguration#getCommand(java.lang.String)
	 */
	@Override
	public Thinkify50PushCommand getCommand(String readerID) {
		Thinkify50PushCommand command = new Thinkify50PushCommand(super.getID());
		return command;
	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.configuration.RifidiService#getMBeanInfo()
	 */
	@Override
	public MBeanInfo getMBeanInfo() {
		return (MBeanInfo) mbeaninfo.clone();
	}

}

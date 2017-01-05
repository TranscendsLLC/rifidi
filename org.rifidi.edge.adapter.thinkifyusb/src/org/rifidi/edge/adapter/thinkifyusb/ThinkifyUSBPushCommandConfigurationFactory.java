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
package org.rifidi.edge.adapter.thinkifyusb;

import javax.management.MBeanInfo;

import org.rifidi.edge.configuration.AbstractCommandConfigurationFactory;
import org.rifidi.edge.configuration.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.exceptions.InvalidStateException;

public class ThinkifyUSBPushCommandConfigurationFactory extends
		AbstractCommandConfigurationFactory<ThinkifyUSBPushCommandConfiguration> {
	
	/** Name of the command. */
	public static final String ID = "ThinkifyUSB-Push";
	/** Mbeaninfo for this class. */
	public static final MBeanInfo mbeaninfo;
	static {
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		mbeaninfo = strategy
				.getMBeanInfo(ThinkifyUSBPushCommandConfiguration.class);
	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.configuration.ServiceFactory#createInstance(java.lang.String)
	 */
	@Override
	public void createInstance(String serviceID)
			throws IllegalArgumentException, InvalidStateException {
		ThinkifyUSBPushCommandConfiguration config = new ThinkifyUSBPushCommandConfiguration();
		config.setID(serviceID);
		config.register(getContext(), ThinkifyUSBSensorFactory.FACTORY_ID);
	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.configuration.ServiceFactory#getServiceDescription(java.lang.String)
	 */
	@Override
	public MBeanInfo getServiceDescription(String factoryID) {
		return (MBeanInfo) mbeaninfo.clone();
	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.configuration.ServiceFactory#getFactoryID()
	 */
	@Override
	public String getFactoryID() {
		return ID;
	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.configuration.AbstractCommandConfigurationFactory#getReaderFactoryID()
	 */
	@Override
	public String getReaderFactoryID() {
		return ThinkifyUSBSensorFactory.FACTORY_ID;
	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.configuration.AbstractCommandConfigurationFactory#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return "ThinkifyUSB Push";
	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.configuration.AbstractCommandConfigurationFactory#getCommandDescription()
	 */
	@Override
	public String getCommandDescription() {
		return "Sets the Thinkify to push tags back to the edge server";
	}

}

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
package org.rifidi.edge.adapter.awid.commands.awid2010;

import javax.management.MBeanInfo;

import org.rifidi.edge.adapter.awid.awid2010.Awid2010SensorFactory;
import org.rifidi.edge.configuration.AbstractCommandConfigurationFactory;
import org.rifidi.edge.exceptions.InvalidStateException;

public class Awid2010PortalIDWithMaskCommandConfigurationFactory
		extends
		AbstractCommandConfigurationFactory<AwidPortalIDWithMaskCommandConfiguration> {

	public static final String FACTORY_ID = "Awid-Mask-Poll";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.configuration.ServiceFactory#getFactoryID()
	 */
	@Override
	public String getFactoryID() {
		return FACTORY_ID;
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
		return Awid2010SensorFactory.FACTORY_ID;
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
		return (MBeanInfo) AwidPortalIDWithMaskCommandConfiguration.mbeaninfo
				.clone();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.configuration.ServiceFactory#createInstance(java
	 * .lang.String)
	 */
	@Override
	public void createInstance(String serviceID)
			throws IllegalArgumentException, InvalidStateException {
		AwidPortalIDWithMaskCommandConfiguration config = new AwidPortalIDWithMaskCommandConfiguration();
		config.setID(serviceID);
		config.register(super.getContext(), getReaderFactoryID());

	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.configuration.impl.AbstractCommandConfigurationFactory#getCommandDescription()
	 */
	@Override
	public String getCommandDescription() {
		return "Configure the Awid reader to send back tags using the Gen 2 Portal ID With Mask command. "
		+ "To monitor a read zone and read a specific memory bank, submit this command for a one-time execution.";
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.configuration.impl.AbstractCommandConfigurationFactory#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return "Awid Mask Push Start";
	}
}

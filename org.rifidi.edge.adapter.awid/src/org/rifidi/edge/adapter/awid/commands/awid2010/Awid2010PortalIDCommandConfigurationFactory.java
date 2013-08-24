/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.adapter.awid.commands.awid2010;

import javax.management.MBeanInfo;

import org.rifidi.edge.adapter.awid.awid2010.Awid2010SensorFactory;
import org.rifidi.edge.configuration.AbstractCommandConfigurationFactory;
import org.rifidi.edge.exceptions.InvalidStateException;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class Awid2010PortalIDCommandConfigurationFactory
		extends
		AbstractCommandConfigurationFactory<AwidPortalIDCommandConfiguration> {

	public static final String FACTORY_ID = "Awid2010-Poll";

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
		return (MBeanInfo) AwidPortalIDCommandConfiguration.mbeaninfo
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
		AwidPortalIDCommandConfiguration config = new AwidPortalIDCommandConfiguration();
		config.setID(serviceID);
		config.register(super.getContext(), getReaderFactoryID());

	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.configuration.impl.AbstractCommandConfigurationFactory#getCommandDescription()
	 */
	@Override
	public String getCommandDescription() {
		return "Configure the Awid reader to send back tags using the Gen 2 Portal ID command. "
		+ "To monitor a read zone, submit this command for a one-time execution.";
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.configuration.impl.AbstractCommandConfigurationFactory#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return "Awid Push Start";
	}
}

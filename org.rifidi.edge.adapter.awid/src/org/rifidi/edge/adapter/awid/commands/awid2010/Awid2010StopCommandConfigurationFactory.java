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
 * @author Owner
 * 
 */
public class Awid2010StopCommandConfigurationFactory extends
		AbstractCommandConfigurationFactory<AwidStopCommandConfiguration> {

	public static final String FACTORY_ID = "Awid2010-Push-Stop";

	@Override
	public String getReaderFactoryID() {
		return Awid2010SensorFactory.FACTORY_ID;
	}

	@Override
	public void createInstance(String serviceID)
			throws IllegalArgumentException, InvalidStateException {
		AwidStopCommandConfiguration config = new AwidStopCommandConfiguration();
		config.setID(serviceID);
		config.register(super.getContext(), getReaderFactoryID());
	}

	@Override
	public String getFactoryID() {
		return FACTORY_ID;
	}

	@Override
	public MBeanInfo getServiceDescription(String factoryID) {
		return (MBeanInfo) AwidStopCommandConfiguration.mbeaninfo.clone();
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.configuration.impl.AbstractCommandConfigurationFactory#getCommandDescription()
	 */
	@Override
	public String getCommandDescription() {
		return "Command the Awid reader to stop sending back tags. "
		+ "To use, submit this command for a one-time execution.";
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.configuration.impl.AbstractCommandConfigurationFactory#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return "Awid Push Stop";
	}
	
	

}

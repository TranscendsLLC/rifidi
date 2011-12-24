/*
 * Awid2010StopCommandConfigurationFactory.java
 * 
 * Created:     Oct 20th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:     The software in this package is published under the terms of the EPL License
 *                   A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.adapter.awid.commands.awid2010;

import javax.management.MBeanInfo;

import org.rifidi.edge.configuration.AbstractCommandConfigurationFactory;
import org.rifidi.edge.exceptions.InvalidStateException;
import org.rifidi.edge.adapter.awid.awid2010.Awid2010SensorFactory;

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

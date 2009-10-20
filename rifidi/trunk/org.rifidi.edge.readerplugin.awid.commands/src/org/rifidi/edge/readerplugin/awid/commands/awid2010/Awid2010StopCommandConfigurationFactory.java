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
package org.rifidi.edge.readerplugin.awid.commands.awid2010;

import javax.management.MBeanInfo;

import org.rifidi.edge.core.configuration.impl.AbstractCommandConfigurationFactory;
import org.rifidi.edge.core.exceptions.InvalidStateException;
import org.rifidi.edge.readerplugin.awid.awid2010.Awid2010SensorFactory;

/**
 * @author Owner
 * 
 */
public class Awid2010StopCommandConfigurationFactory extends
		AbstractCommandConfigurationFactory<Awid2010StopCommandConfiguration> {

	public static final String FACTORY_ID = "AwidStop";

	@Override
	public String getReaderFactoryID() {
		return Awid2010SensorFactory.FACTORY_ID;
	}

	@Override
	public void createInstance(String serviceID)
			throws IllegalArgumentException, InvalidStateException {
		Awid2010StopCommandConfiguration config = new Awid2010StopCommandConfiguration();
		config.setID(serviceID);
		config.register(super.getContext(), getReaderFactoryID());
	}

	@Override
	public String getFactoryID() {
		return FACTORY_ID;
	}

	@Override
	public MBeanInfo getServiceDescription(String factoryID) {
		return (MBeanInfo) Awid2010StopCommandConfiguration.mbeaninfo.clone();
	}

}

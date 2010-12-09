/*
 * Awid2010PortalIDCommandConfigurationFactory.java
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

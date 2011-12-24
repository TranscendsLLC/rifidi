/*
 *  LLRPROSpecFromFileCommandConfigurationFactory.java
 *
 *  Created:	Apr 26, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.adapter.llrp.commands;

import javax.management.MBeanInfo;

import org.rifidi.edge.adapter.llrp.LLRPReaderFactory;
import org.rifidi.edge.configuration.AbstractCommandConfigurationFactory;
import org.rifidi.edge.exceptions.InvalidStateException;
import org.rifidi.edge.sensors.AbstractCommandConfiguration;

/**
 * Factory for the ROSpecFromFileConfiguration.  
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class LLRPROSpecFromFileCommandConfigurationFactory extends
		AbstractCommandConfigurationFactory<AbstractCommandConfiguration<?>> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.configuration.impl.AbstractCommandConfigurationFactory
	 * #getCommandDescription()
	 */
	@Override
	public String getCommandDescription() {
		return "Configure the LLRP reader via an XML file.  "
				+ "Any changes made to the reader are determined by "
				+ "the contents of the file.  To generate xml code for"
				+ " the commands you want to submit, check out LLRP Commander"
				+ " here: http://www.fosstrak.org/llrp/index.html";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.configuration.impl.AbstractCommandConfigurationFactory
	 * #getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return "ADD_ROSPEC From File";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.configuration.impl.AbstractCommandConfigurationFactory
	 * #getReaderFactoryID()
	 */
	@Override
	public String getReaderFactoryID() {
		return LLRPReaderFactory.FACTORY_ID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.configuration.ServiceFactory#createInstance(java
	 * .lang.String)
	 */
	@Override
	public void createInstance(String serviceID)
			throws IllegalArgumentException, InvalidStateException {
		LLRPROSpecFromFileCommandConfiguration commandconfig = new LLRPROSpecFromFileCommandConfiguration();
		commandconfig.setID(serviceID);
		commandconfig.register(getContext(), LLRPReaderFactory.FACTORY_ID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.configuration.ServiceFactory#getFactoryID()
	 */
	@Override
	public String getFactoryID() {
		return "LLRP-ADD_ROSPEC-File";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.configuration.ServiceFactory#getServiceDescription
	 * (java.lang.String)
	 */
	@Override
	public MBeanInfo getServiceDescription(String factoryID) {
		return (MBeanInfo) LLRPROSpecFromFileCommandConfiguration.mbeaninfo.clone();
	}

}

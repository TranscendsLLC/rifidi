/*
 *  ThingmagicReaderFactory.java
 *
 *  Created:	Sep 15, 2009
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.thingmagic;

import java.util.Map;

import javax.management.MBeanInfo;

import org.rifidi.edge.core.sensors.base.AbstractSensorFactory;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;

/**
 * @author Matthew Dean
 *
 */
public class ThingmagicReaderFactory extends AbstractSensorFactory<ThingmagicReader> {

	@Override
	public void bindCommandConfiguration(
			AbstractCommandConfiguration<?> commandConfiguration,
			Map<?, ?> properties) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unbindCommandConfiguration(
			AbstractCommandConfiguration<?> commandConfiguration,
			Map<?, ?> properties) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createInstance(String serviceID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getFactoryID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MBeanInfo getServiceDescription(String factoryID) {
		// TODO Auto-generated method stub
		return null;
	}

}

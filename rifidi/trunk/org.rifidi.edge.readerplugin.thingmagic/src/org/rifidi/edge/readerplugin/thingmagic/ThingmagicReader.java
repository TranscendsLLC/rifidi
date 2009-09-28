/*
 *  ThingmagicReader.java
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

import org.rifidi.edge.api.rmi.dto.SessionDTO;
import org.rifidi.edge.core.sensors.SensorSession;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;

/**
 * 
 * 
 * @author Matthew Dean
 */
public class ThingmagicReader extends AbstractSensor<ThingmagicReaderSession> {

	@Override
	public void applyPropertyChanges() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String createSensorSession() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createSensorSession(SessionDTO sessionDTO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void destroySensorSession(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, SensorSession> getSensorSessions() {
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
	public MBeanInfo getMBeanInfo() {
		// TODO Auto-generated method stub
		return null;
	}

}

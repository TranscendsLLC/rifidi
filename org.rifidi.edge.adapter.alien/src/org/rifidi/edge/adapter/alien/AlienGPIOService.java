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
package org.rifidi.edge.adapter.alien;

import java.util.BitSet;
import java.util.Collection;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.sensors.AbstractGPIOService;
import org.rifidi.edge.sensors.AbstractSensor;
import org.rifidi.edge.sensors.CannotExecuteException;
import org.rifidi.edge.sensors.SensorSession;

/**
 * This class is a service that lets applications access the GPIO functionality
 * of registered Alien readers. It should be registered with a service property
 * called 'reader' with a value of 'Alien'
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AlienGPIOService extends AbstractGPIOService<Alien9800ReaderSession> {

	private static final Log logger = LogFactory.getLog(AlienGPIOService.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.sessions.interfaces.AbstractGPIOService#
	 * setGPO(java.lang.String, java.util.Collection)
	 */
	@Override
	public void setGPO(String readerID, Collection<Integer> ports) throws CannotExecuteException {
		Alien9800ReaderSession session = getSession(readerID);
		BitSet bits = new BitSet(8);
		for (Integer port : ports) {
			bits.set(port);
		}
		logger.info("Setting GPO on " + readerID + " Ports: " + ports);
		session.setOutputPort(bits);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.sessions.interfaces.AbstractGPIOService#
	 * flashGPO(java.lang.String, int, java.util.Set)
	 */
	@Override
	public void flashGPO(String readerID, int flashTime, Set<Integer> ports) throws CannotExecuteException {
		Alien9800ReaderSession session = getSession(readerID);
		BitSet bits = new BitSet(8);
		for (Integer port : ports) {
			bits.set(port);
		}
		logger.info("Flashing GPO on " + readerID + " Ports: " + ports + " for " + flashTime + " seconds");
		session.flashOutput(bits, new BitSet(), flashTime, 0, 1);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.management.AbstractGPIOService#testGPI(java
	 * .lang.String, int)
	 */
	@Override
	public boolean testGPI(String readerID, int port) throws CannotExecuteException {
		Alien9800ReaderSession session = getSession(readerID);
		return session.testInputPort(port);
	}

	/**
	 * 
	 */
	@Override
	public boolean isReaderAvailable(String readerID) {
		AbstractSensor<?> sensor = this.readerDAO.getReaderByID(readerID);
		if(sensor == null) {
			return false;
		}
		SensorSession cursession = null;
		for(SensorSession session : sensor.getSensorSessions().values()) {
			cursession = session;
			break;
		}
		if (cursession instanceof Alien9800ReaderSession) {
			return true;
		}
		return false;
	}
}

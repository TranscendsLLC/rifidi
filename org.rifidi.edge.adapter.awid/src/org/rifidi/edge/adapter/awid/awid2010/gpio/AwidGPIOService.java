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
package org.rifidi.edge.adapter.awid.awid2010.gpio;

import java.io.IOException;
import java.util.BitSet;
import java.util.Collection;
import java.util.Set;

import org.rifidi.edge.adapter.awid.awid2010.AwidSession;
import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.sensors.AbstractGPIOService;
import org.rifidi.edge.sensors.AbstractSensor;
import org.rifidi.edge.sensors.CannotExecuteException;
import org.rifidi.edge.sensors.SensorSession;

/**
 * This class is a service that lets applications access the GPIO functionality
 * of registered Awid readers. It should be registered with a service property
 * called 'reader' and a value called 'Awid'
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AwidGPIOService extends AbstractGPIOService<AwidSession> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.management.AbstractGPIOService#flashGPO(
	 * java.lang.String, int, java.util.Set)
	 */
	@Override
	public void flashGPO(String readerID, int flashTime, Set<Integer> ports) throws CannotExecuteException {
		getGPIOSession(readerID).flashOutput(getBitsForSet(ports), new BitSet(), flashTime * 10, 0, 1);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.management.AbstractGPIOService#setGPO(java
	 * .lang.String, java.util.Collection)
	 */
	@Override
	public void setGPO(String readerID, Collection<Integer> ports) throws CannotExecuteException {

		getGPIOSession(readerID).setOutputPort(getBitsForSet(ports));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.management.AbstractGPIOService#testGPI(java
	 * .lang.String, int)
	 */
	@Override
	public boolean testGPI(String readerID, int port) throws CannotExecuteException {
		return getGPIOSession(readerID).testInputPort(port);
	}

	/**
	 * A helper method that gets a bit set for a collection of ports numbers
	 * 
	 * @param ports
	 * @return
	 * @throws CannotExecuteException
	 */
	private BitSet getBitsForSet(Collection<Integer> ports) throws CannotExecuteException {
		BitSet bits = new BitSet(4);
		for (Integer port : ports) {
			if (port >= 0 && port <= 3)
				bits.set(port);
			else
				throw new CannotExecuteException("AWID ports must be between 0-3");
		}
		return bits;
	}

	/**
	 * A helper method that gets and starts the AwidGPIOSession given the ID of the
	 * AwidReader
	 * 
	 * @param readerID The ID of the awid reader
	 * @return A live AwidGPIOSession
	 * @throws CannotExecuteException If there is a problem when getting the reader.
	 */
	private AwidGPIOSession getGPIOSession(String readerID) throws CannotExecuteException {
		AwidGPIOSession gpioSession = super.getSession(readerID).getGPIOSession();
		if (gpioSession.getStatus() != SessionStatus.PROCESSING) {
			try {
				gpioSession.connect();
			} catch (IOException e) {
				gpioSession.disconnect();
				throw new CannotExecuteException(e);
			}
		}
		return gpioSession;
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
		if (cursession instanceof AwidSession) {
			return true;
		}
		return false;
	}

}

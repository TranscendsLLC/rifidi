/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.adapter.alien;

import java.util.BitSet;
import java.util.Collection;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.sensors.AbstractGPIOService;
import org.rifidi.edge.sensors.CannotExecuteException;

/**
 * This class is a service that lets applications access the GPIO functionality
 * of registered Alien readers. It should be registered with a service property
 * called 'reader' with a value of 'Alien'
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AlienGPIOService extends
		AbstractGPIOService<Alien9800ReaderSession> {

	private static final Log logger = LogFactory.getLog(AlienGPIOService.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.sessions.interfaces.AbstractGPIOService#
	 * setGPO(java.lang.String, java.util.Collection)
	 */
	@Override
	public void setGPO(String readerID, Collection<Integer> ports)
			throws CannotExecuteException {
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
	 * @see
	 * org.rifidi.edge.sensors.sessions.interfaces.AbstractGPIOService#
	 * flashGPO(java.lang.String, int, java.util.Set)
	 */
	@Override
	public void flashGPO(String readerID, int flashTime, Set<Integer> ports)
			throws CannotExecuteException {
		Alien9800ReaderSession session = getSession(readerID);
		BitSet bits = new BitSet(8);
		for (Integer port : ports) {
			bits.set(port);
		}
		logger.info("Flashing GPO on " + readerID + " Ports: " + ports
				+ " for " + flashTime + " seconds");
		session.flashOutput(bits, new BitSet(), flashTime, 0, 1);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.management.AbstractGPIOService#testGPI(java
	 * .lang.String, int)
	 */
	@Override
	public boolean testGPI(String readerID, int port)
			throws CannotExecuteException {
		Alien9800ReaderSession session = getSession(readerID);
		return session.testInputPort(port);
	}

}

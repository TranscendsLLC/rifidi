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
/**
 * 
 */
package org.rifidi.edge.services;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.sensors.NotSubscribedException;
import org.rifidi.edge.sensors.Sensor;

import com.espertech.esper.client.EPRuntime;

/**
 * Receive and handle ReadCycles from readers. Publish them to Esper.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class EsperReceiver implements Runnable {
	/** Logger for this class. */
	private static final Log logger = LogFactory.getLog(EsperReceiver.class);
	/** Set containing the sensors the receiver currently handles. */
	private final Set<Sensor> sensors;
	/** The esper runtime. */
	private final EPRuntime runtime;

	/**
	 * Constructor.
	 * 
	 * @param runtime
	 */
	public EsperReceiver(final EPRuntime runtime) {
		sensors = new CopyOnWriteArraySet<Sensor>();
		this.runtime = runtime;
	}

	/**
	 * Add a new sensor to the receiver.
	 * 
	 * @param sensor
	 */
	public void addSensor(final Sensor sensor) {
		sensors.add(sensor);
	}

	/**
	 * Remove a sensor from the receiver.
	 * 
	 * @param sensor
	 */
	public void removeSensor(final Sensor sensor) {
		sensors.add(sensor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			for (Sensor sensor : sensors) {
				try {
					EsperEventContainer container = sensor.receive(this);
					runtime.sendEvent(container.getReadCycle());
					for (Object event : container.getOtherEvents()) {
						runtime.sendEvent(event);
					}
				} catch (NotSubscribedException e) {
					throw new RuntimeException(e);
				}
				// when a service becomes unavailable the proxy throws a runtime
				// exception
				catch (RuntimeException re) {
					logger.debug("A sensor went away. " + re);
					sensors.remove(sensor);
				}
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			}
		}
	}

}

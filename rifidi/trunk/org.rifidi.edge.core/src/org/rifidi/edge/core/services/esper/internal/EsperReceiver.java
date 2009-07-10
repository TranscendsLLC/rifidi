/**
 * 
 */
package org.rifidi.edge.core.services.esper.internal;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.rifidi.edge.core.sensors.Sensor;
import org.rifidi.edge.core.sensors.exceptions.NotSubscribedException;
import org.rifidi.edge.core.services.notification.data.ReadCycle;

import com.espertech.esper.client.EPRuntime;

/**
 * Receive and handle ReadCycles from readers. Publish them to Esper.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class EsperReceiver implements Runnable {
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
					ReadCycle cycle = sensor.receive(this);
					runtime.sendEvent(cycle);
				} catch (NotSubscribedException e) {
					throw new RuntimeException(e);
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

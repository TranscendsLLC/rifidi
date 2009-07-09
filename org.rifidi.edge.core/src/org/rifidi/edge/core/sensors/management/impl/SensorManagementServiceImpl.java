/**
 * 
 */
package org.rifidi.edge.core.sensors.management.impl;

import java.util.Collection;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.sensors.CompositeSensor;
import org.rifidi.edge.core.sensors.CompositeSensorUpdate;
import org.rifidi.edge.core.sensors.Sensor;
import org.rifidi.edge.core.sensors.SensorUpdate;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.exceptions.DuplicateSensorNameException;
import org.rifidi.edge.core.sensors.exceptions.DuplicateSubscriptionException;
import org.rifidi.edge.core.sensors.exceptions.ImmutableException;
import org.rifidi.edge.core.sensors.exceptions.InUseException;
import org.rifidi.edge.core.sensors.exceptions.NoSuchSensorException;
import org.rifidi.edge.core.sensors.exceptions.NotSubscribedException;
import org.rifidi.edge.core.sensors.impl.SensorImpl;
import org.rifidi.edge.core.sensors.management.SensorManagementService;

/**
 * Thread asfe implementation of the {@link SensorManagementService}
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class SensorManagementServiceImpl implements SensorManagementService {
	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(SensorManagementServiceImpl.class);
	/**
	 * All operations that modify ANY part of the sensor tree or a sensor need
	 * to acquire this lock.
	 */
	private final Lock sensorLock;
	/** Sensors created by this service. */
	private final Map<String, SensorUpdate> sensors;
	/** Sensors created outside the service are all treated as physical sensors. */
	// TODO: Handle reader that disappear.
	private final Map<String, AbstractSensor<?>> physicalSensors;

	/**
	 * Constructor.
	 */
	public SensorManagementServiceImpl() {
		this.sensorLock = new ReentrantLock();
		this.sensors = new ConcurrentHashMap<String, SensorUpdate>();
		this.physicalSensors = new ConcurrentHashMap<String, AbstractSensor<?>>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.management.SensorManagementService#createSensor
	 * (java.lang.String, java.util.Collection)
	 */
	@Override
	public void createSensor(final String sensorName,
			final Collection<String> childSensors)
			throws DuplicateSensorNameException, NoSuchSensorException {
		sensorLock.lock();
		try {
			if (sensors.get(sensorName) != null
					|| physicalSensors.get(sensorName) != null) {
				throw new DuplicateSensorNameException("A sensor named "
						+ sensorName + " already exists.");
			}
			Set<SensorUpdate> children = new HashSet<SensorUpdate>();
			for (String child : childSensors) {
				if (sensors.get(child) != null) {
					children.add(sensors.get(child));
				} else if (physicalSensors.get(child) != null) {
					children.add(physicalSensors.get(child));
				}
			}
			SensorImpl sensor = new SensorImpl(sensorName, children, false);
			sensors.put(sensorName, sensor);
			for (SensorUpdate child : children) {
				child.addReceiver(sensor);
			}
		} finally {
			sensorLock.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.management.SensorManagementService#destroySensor
	 * (java.lang.String)
	 */
	@Override
	public void destroySensor(final String sensorName)
			throws NoSuchSensorException, ImmutableException, InUseException {
		sensorLock.lock();
		try {
			if (sensors.get(sensorName) == null
					&& physicalSensors.get(sensorName) == null) {
				throw new NoSuchSensorException("No sensor named " + sensorName
						+ " available.");
			}
			if (sensors.get(sensorName) == null
					&& physicalSensors.get(sensorName) != null) {
				throw new ImmutableException("Sensor named " + sensorName
						+ " is immutable.");
			}
			SensorUpdate sensor = sensors.get(sensorName);
			sensors.remove(sensor);

			if (sensor instanceof CompositeSensorUpdate) {

				for (String child : ((CompositeSensor) sensor).getChildren()) {
					SensorUpdate childSensors = sensors.get(child);
					if (childSensors == null) {
						childSensors = physicalSensors.get(child);
					}
					if (childSensors == null) {
						logger.error("Trying to remove " + child + " from "
								+ sensorName + " but it doesn't exist.");
					}
					childSensors.removeReceiver(sensor);
				}
			}
			sensors.remove(sensorName);
		} finally {
			sensorLock.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.management.SensorManagementService#createSensor
	 * (java.lang.String)
	 */
	@Override
	public void createSensor(final String sensorName)
			throws DuplicateSensorNameException {
		try {
			createSensor(sensorName, new HashSet<String>());
		} catch (NoSuchSensorException e) {
			logger.fatal("That should never happen: " + e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.management.impl.SensorManagementService#
	 * subscribe(java.lang.Object, java.lang.String)
	 */
	@Override
	public Sensor subscribe(final Object subscriber, final String sensorName)
			throws NoSuchSensorException, DuplicateSubscriptionException {
		sensorLock.lock();
		try {
			SensorUpdate sensor = sensors.get(sensorName);
			if (sensor == null) {
				throw new NoSuchSensorException("A sensor named " + sensorName
						+ " doesn't exist.");
			}
			sensor.subscribe(subscriber);
			return sensor;
		} finally {
			sensorLock.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.management.impl.SensorManagementService#
	 * unsubscribe(java.lang.Object, java.lang.String)
	 */
	@Override
	public void unsubscribe(final Object subscriber, final String sensorName)
			throws NoSuchSensorException, NotSubscribedException {
		sensorLock.lock();
		try {
			SensorUpdate sensor = sensors.get(sensorName);
			if (sensor == null) {
				sensor = physicalSensors.get(sensorName);
			}
			if (sensor == null) {
				throw new NoSuchSensorException("A sensor named " + sensorName
						+ " doesn't exist.");
			}
			sensor.unsubscribe(subscriber);
		} finally {
			sensorLock.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.management.impl.SensorManagementService#
	 * renameSensor(java.lang.String, java.lang.String)
	 */
	@Override
	public void renameSensor(final String oldName, final String newName)
			throws NoSuchSensorException, ImmutableException, InUseException {
		sensorLock.lock();
		try {
			SensorUpdate sensor = sensors.get(oldName);
			if (sensor == null && physicalSensors.get(oldName) != null) {
				throw new ImmutableException(oldName + " is immutable.");
			}
			if (sensor == null) {
				throw new NoSuchSensorException("A sensor named " + oldName
						+ " doesn't exist.");
			}
			sensor.setName(newName);
			sensors.remove(oldName);
			sensors.put(newName, sensor);
		} finally {
			sensorLock.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.management.impl.SensorManagementService#
	 * addChild(java.lang.String, java.lang.String)
	 */
	@Override
	public void addChild(final String sensorName, final String childName)
			throws ImmutableException, InUseException, NoSuchSensorException {
		sensorLock.lock();
		try {
			SensorUpdate sensor = sensors.get(sensorName);
			SensorUpdate child = sensors.get(childName);
			if (sensor == null && physicalSensors.get(sensorName) != null) {
				throw new ImmutableException(sensorName + " is immutable.");
			}
			if (sensor == null) {
				throw new NoSuchSensorException("A sensor named " + sensorName
						+ " doesn't exist.");
			}
			if (child == null) {
				throw new NoSuchSensorException("A sensor named " + childName
						+ " doesn't exist.");
			}
			if (!(sensor instanceof CompositeSensorUpdate)) {
				throw new ImmutableException(sensorName
						+ " is not a composite sensor and can't be modified.");
			}
			((CompositeSensorUpdate) sensor).addChild(child);
			child.addReceiver(sensor);
		} finally {
			sensorLock.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.management.SensorManagementService#addChildren
	 * (java.lang.String, java.util.Collection)
	 */
	@Override
	public void addChildren(final String sensorName,
			final Collection<String> childNames) throws ImmutableException,
			InUseException, NoSuchSensorException {
		sensorLock.lock();
		try {
			SensorUpdate sensor = sensors.get(sensorName);
			if (sensor == null && physicalSensors.get(sensorName) != null) {
				throw new ImmutableException(sensorName + " is immutable.");
			}
			if (sensor == null) {
				throw new NoSuchSensorException("A sensor named " + sensorName
						+ " doesn't exist.");
			}

			if (!(sensor instanceof CompositeSensorUpdate)) {
				throw new ImmutableException(sensorName
						+ " is not a composite sensor and can't be modified.");
			}
			Set<SensorUpdate> children = new HashSet<SensorUpdate>();
			for (String childName : childNames) {
				SensorUpdate child = sensors.get(childName);
				if (child == null) {
					throw new NoSuchSensorException("A sensor named "
							+ childName + " doesn't exist.");
				}
			}
			for (SensorUpdate child : children) {
				((CompositeSensorUpdate) sensor).addChild(child);
				child.addReceiver(sensor);
			}
		} finally {
			sensorLock.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.management.SensorManagementService#setChildren
	 * (java.lang.String, java.util.Collection)
	 */
	@Override
	public void setChildren(final String sensorName,
			final Collection<String> childNames) throws ImmutableException,
			InUseException, NoSuchSensorException {

		SensorUpdate sensor = sensors.get(sensorName);
		if (sensor == null && physicalSensors.get(sensorName) != null) {
			throw new ImmutableException(sensorName + " is immutable.");
		}
		if (sensor == null) {
			throw new NoSuchSensorException("A sensor named " + sensorName
					+ " doesn't exist.");
		}
		// TODO: finish
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.management.impl.SensorUpdate#removeChild
	 * (org.rifidi.edge.core.sensors.management.Sensor)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.management.impl.SensorManagementService#
	 * removeChild(java.lang.String, java.lang.String)
	 */
	@Override
	public void removeChild(final String sensorName, final String childName)
			throws ImmutableException, InUseException, NoSuchSensorException {
		sensorLock.lock();
		try {
			SensorUpdate sensor = sensors.get(sensorName);
			SensorUpdate child = sensors.get(childName);
			if (sensor == null && physicalSensors.get(sensorName) != null) {
				throw new ImmutableException(sensorName + " is immutable.");
			}
			if (sensor == null) {
				throw new NoSuchSensorException("A sensor named " + sensorName
						+ " doesn't exist.");
			}
			if (child == null) {
				throw new NoSuchSensorException("A sensor named " + childName
						+ " doesn't exist.");
			}
			if (!(sensor instanceof CompositeSensorUpdate)) {
				throw new ImmutableException(sensorName
						+ " is not a composite sensor and can't be modified.");
			}
			child.removeReceiver(sensor);
			((CompositeSensorUpdate) sensor).removeChild(child);
		} finally {
			sensorLock.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.management.impl.SensorManagementService#
	 * removeChildren(java.lang.String, java.util.Collection)
	 */
	@Override
	public void removeChildren(final String sensorName,
			final Collection<String> childrenNames) throws ImmutableException,
			InUseException, NoSuchSensorException {
		sensorLock.lock();
		try {
			SensorUpdate sensor = sensors.get(sensorName);
			if (sensor == null && physicalSensors.get(sensorName) != null) {
				throw new ImmutableException(sensorName + " is immutable.");
			}
			if (sensor == null) {
				throw new NoSuchSensorException("A sensor named " + sensorName
						+ " doesn't exist.");
			}
			if (!(sensor instanceof CompositeSensorUpdate)) {
				throw new ImmutableException(sensorName
						+ " is not a composite sensor and can't be modified.");
			}
			Set<SensorUpdate> children = new HashSet<SensorUpdate>();
			for (String childName : childrenNames) {
				SensorUpdate child = sensors.get(childName);
				if (child == null) {
					throw new NoSuchSensorException("A sensor named "
							+ childName + " doesn't exist.");
				}
				children.add(child);
			}
			for (SensorUpdate child : children) {
				child.removeReceiver(sensor);
			}
			((CompositeSensorUpdate) sensor).removeChildren(children);
		} finally {
			sensorLock.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.management.SensorManagementService#getSensors
	 * ()
	 */
	@Override
	public Set<String> getSensors() {
		Set<String> ret = new HashSet<String>();
		ret.addAll(sensors.keySet());
		ret.addAll(physicalSensors.keySet());
		return ret;
	}

	/**
	 * Used by spring to bind a new AbstractSensor to this service.
	 * 
	 * @param reader
	 *            the configuration to bind
	 * @param parameters
	 */
	public void bindReader(final AbstractSensor<?> reader,
			final Dictionary<String, String> parameters) {
		logger.info("Reader bound:" + reader.getID());
		this.physicalSensors.put(reader.getID(), reader);
	}

	/**
	 * Used by spring to unbind a disappearing AbstractSensor service from this
	 * service.
	 * 
	 * @param reader
	 *            the AbstractSensor to unbind
	 * @param parameters
	 */
	public void unbindReader(final AbstractSensor<?> reader,
			final Dictionary<String, String> parameters) {
		logger.info("Reader unbound:" + reader.getID());
		physicalSensors.remove(reader.getID());
	}

	/**
	 * Used by spring to give the initial list of readers
	 * 
	 * @param readers
	 *            the initial list of available readers
	 */
	public void setReader(final Set<AbstractSensor<?>> readers) {
		for (AbstractSensor<?> reader : readers) {
			physicalSensors.put(reader.getID(), reader);
		}
	}

}

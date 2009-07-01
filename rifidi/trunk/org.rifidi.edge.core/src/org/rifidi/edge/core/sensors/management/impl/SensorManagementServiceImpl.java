/**
 * 
 */
package org.rifidi.edge.core.sensors.management.impl;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.sensors.LogicalSensor;
import org.rifidi.edge.core.sensors.PhysicalSensor;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.exceptions.DuplicateSensorNameException;
import org.rifidi.edge.core.sensors.impl.LogicalSensorImpl;
import org.rifidi.edge.core.sensors.management.SensorManagementService;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class SensorManagementServiceImpl implements SensorManagementService {
	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(SensorManagementServiceImpl.class);
	/** The available set of sensorSession configurations */
	private Map<String, AbstractSensor<?>> abstractSensors;
	/** Logical sensors. */
	private Map<String, LogicalSensor> logicalSensors;

	/**
	 * Constructor.
	 */
	public SensorManagementServiceImpl() {
		logicalSensors = new ConcurrentHashMap<String, LogicalSensor>();
		abstractSensors = new ConcurrentHashMap<String, AbstractSensor<?>>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.sensors.management.SensorManagementService#
	 * createLogicalSensor(java.lang.String)
	 */
	@Override
	public synchronized LogicalSensor createLogicalSensor(String name)
			throws DuplicateSensorNameException {
		if (!abstractSensors.containsKey(name)
				&& !logicalSensors.containsKey(name)) {
			logicalSensors.put(name, new LogicalSensorImpl());
			return logicalSensors.get(name);
		} else
			throw new DuplicateSensorNameException("A sensor named " + name
					+ " already exists.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.sensors.management.SensorManagementService#
	 * destroyLogicalSensor(java.lang.String)
	 */
	@Override
	public void destroyLogicalSensor(String name) {
		LogicalSensor sensor = logicalSensors.remove(name);
		sensor.destroy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.sensors.management.SensorManagementService#
	 * destroyLogicalSensor(org.rifidi.edge.core.sensors.LogicalSensor)
	 */
	@Override
	public void destroyLogicalSensor(LogicalSensor reader) {
		for (String readerName : logicalSensors.keySet()) {
			if (logicalSensors.get(readerName).equals(reader)) {
				LogicalSensor sensor = logicalSensors.remove(logicalSensors
						.get(readerName));
				sensor.destroy();
				return;
			}
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
	public Set<PhysicalSensor> getSensors() {
		Set<PhysicalSensor> ret = new HashSet<PhysicalSensor>();
		ret.addAll(abstractSensors.values());
		ret.addAll(logicalSensors.values());
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.management.SensorManagementService#getSensor
	 * (java.lang.String)
	 */
	@Override
	public PhysicalSensor getSensor(String name) {
		PhysicalSensor ret = abstractSensors.get(name);
		if (ret == null) {
			ret = logicalSensors.get(name);
		}
		return ret;
	}

	/**
	 * Used by spring to bind a new AbstractSensor to this service.
	 * 
	 * @param reader
	 *            the configuration to bind
	 * @param parameters
	 */
	public void bindReader(AbstractSensor<?> reader,
			Dictionary<String, String> parameters) {
		logger.info("Reader bound:" + reader.getID());
		this.abstractSensors.put(reader.getID(), reader);
	}

	/**
	 * Used by spring to unbind a disappearing AbstractSensor service from this
	 * service.
	 * 
	 * @param reader
	 *            the AbstractSensor to unbind
	 * @param parameters
	 */
	public void unbindReader(AbstractSensor<?> reader,
			Dictionary<String, String> parameters) {
		logger.info("Reader unbound:" + reader.getID());
		abstractSensors.remove(reader.getID());
	}
	
	/**
	 * Used by spring to give the initial list of readers
	 * 
	 * @param readers
	 *            the initial list of available readers
	 */
	public void setReader(Set<AbstractSensor<?>> readers) {
		for (AbstractSensor<?> reader : readers) {
			abstractSensors.put(reader.getID(), reader);
		}
	}
}

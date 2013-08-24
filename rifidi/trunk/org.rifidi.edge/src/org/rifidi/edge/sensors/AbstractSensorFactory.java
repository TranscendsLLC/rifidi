/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
/**
 * 
 */
package org.rifidi.edge.sensors;

import java.util.Map;
import java.util.Set;

import org.rifidi.edge.api.ReaderFactoryDTO;
import org.rifidi.edge.configuration.AbstractServiceFactory;

/**
 * An abstract class for all ReaderConfigurationFactories to extend.
 * ReaderConfigurationFactories should register themselves to osgi under both
 * the AbstractSensorFactory and the
 * org.rifidi.edge.configuration.ServiceFactory interfaces
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class AbstractSensorFactory<T extends AbstractSensor<?>>
		extends AbstractServiceFactory<T> {
	/** Provided by spring. */
	protected volatile Set<AbstractCommandConfiguration<?>> commands;
	/** Provided by spring. */
	protected volatile Set<AbstractSensor<?>> sensors;

	/**
	 * This constructor is only for CGLIB. DO NOT OVERWRITE!
	 */
	public AbstractSensorFactory() {
		super();
	}

	/**
	 * Construct a DTO for this ReaderFactory
	 * 
	 * @return
	 */
	public ReaderFactoryDTO getReaderFactoryDTO() {
		return new ReaderFactoryDTO(getFactoryID(), getDisplayName(),
				getDescription());
	}

	/**
	 * Get the display name for Readers produced by this factory
	 * 
	 * @return
	 */
	public abstract String getDisplayName();

	/**
	 * Get a description for reader produced by this factory
	 * 
	 * @return
	 */
	public abstract String getDescription();

	/**
	 * Used by spring to provide the sensor instances registered to OSGi.
	 * 
	 * @param commands
	 *            a thread safe set containing all currently available sensor
	 *            instances
	 */
	public void setReaderInstances(Set<AbstractSensor<?>> sensors) {
		this.sensors = sensors;
	}

	/**
	 * Used by spring to provide the currently available command configurations
	 * 
	 * @param commands
	 *            a thread safe set containing all currently available command
	 *            configurations
	 */
	public void setCommandConfigurations(
			Set<AbstractCommandConfiguration<?>> commandConfigurations) {
		this.commands = commandConfigurations;
	}

	/**
	 * Called when a configuration appears.
	 * 
	 * @param commandConfiguration
	 * @param properties
	 */
	public abstract void bindCommandConfiguration(
			AbstractCommandConfiguration<?> commandConfiguration,
			Map<?, ?> properties);

	/**
	 * Called when a configuration disappears.
	 * 
	 * @param commandConfiguration
	 * @param properties
	 */
	public abstract void unbindCommandConfiguration(
			AbstractCommandConfiguration<?> commandConfiguration,
			Map<?, ?> properties);
}

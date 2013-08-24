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
package org.rifidi.edge.configuration;

import org.osgi.framework.BundleContext;
import org.rifidi.edge.api.CommandConfigFactoryDTO;

/**
 * Base class for a service factory. This class is meant for scenarios where
 * there is one service class that can exist in several configurations. The
 * service gets registered using a generated name of the form
 * <factoryid>-<counter>.
 * 
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public abstract class AbstractCommandConfigurationFactory<T> implements
		ServiceFactory<T> {
	/** Context of the registering bundle. */
	private BundleContext context;

	/**
	 * @param context
	 *            the context to set
	 */
	public void setContext(BundleContext context) {
		this.context = context;
	}

	/**
	 * Get the bundle context for this factory.
	 * 
	 * @return
	 */
	protected BundleContext getContext() {
		return context;
	}

	/**
	 * Get the ID of the reader factory that this command factory is associated
	 * with
	 * 
	 * @return The ID of the reader factory that this CommandFactory produces
	 *         commands for
	 */
	public abstract String getReaderFactoryID();

	/**
	 * Get the Data Transfer Object for the CommandConfigFactory.
	 * 
	 * TODO: Should be moved out of here
	 * 
	 * @return The DTO for this object
	 */
	public CommandConfigFactoryDTO getDTO() {
		return new CommandConfigFactoryDTO(getReaderFactoryID(),
				getFactoryID(), getDisplayName(), getCommandDescription());
	}

	/**
	 * Get the name of the command
	 * 
	 * @return
	 */
	public abstract String getDisplayName();

	/**
	 * Get the description of the command.
	 * 
	 * @return
	 */
	public abstract String getCommandDescription();
}

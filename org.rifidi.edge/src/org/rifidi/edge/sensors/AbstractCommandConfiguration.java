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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.rifidi.edge.api.CommandConfigurationDTO;
import org.rifidi.edge.configuration.Configuration;
import org.rifidi.edge.configuration.ConfigurationType;
import org.rifidi.edge.configuration.RifidiService;

/**
 * Command configurations represent all properties of a command and will create
 * instances of the commands with those properties.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public abstract class AbstractCommandConfiguration<T extends Command> extends
		RifidiService {
	/**
	 * Get a new instance of the command.
	 * 
	 * @return
	 */
	public abstract T getCommand(String readerID);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.configuration.RifidiService#destroy()
	 */
	@Override
	protected void destroy() {
		// TODO: we also need to figure out how to tell commands that have been
		// produced to not run the next time they are executed
		super.unregister();
	}

	/**
	 * Get the DTO.
	 * 
	 * TODO:Move this out of this object into a DTO service
	 * 
	 * @param configuration
	 * @return
	 */
	public CommandConfigurationDTO getDTO(Configuration configuration) {
		return new CommandConfigurationDTO(this.getID(), configuration
				.getFactoryID(), configuration.getAttributes(configuration
				.getAttributeNames()));
	}
	

	
	/**
	 * Register the reader to OSGi.
	 * 
	 * @param context
	 * @param readerType
	 */
	public void register(BundleContext context, String readerType) {
		Map<String, String> parms = new HashMap<String, String>();
		parms.put("type", ConfigurationType.COMMAND.toString());
		parms.put("reader", readerType);
		Set<String> interfaces = new HashSet<String>();
		interfaces.add(AbstractCommandConfiguration.class.getCanonicalName());
		register(context, interfaces, parms);
	}
}

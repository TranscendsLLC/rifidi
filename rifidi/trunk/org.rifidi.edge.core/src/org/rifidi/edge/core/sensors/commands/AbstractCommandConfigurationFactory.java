/**
 * 
 */
package org.rifidi.edge.core.sensors.commands;

import java.util.HashSet;

import org.rifidi.edge.api.rmi.dto.CommandConfigFactoryDTO;
import org.rifidi.edge.core.configuration.impl.AbstractMultiServiceFactory;

/**
 * A base class that all CommandConfigurationFactories should extend. Concrete
 * implementations should register themselves to OSGi under both the
 * AbstractCommandConfigurationFactory and
 * org.rifidi.edge.core.configuration.ServiceFactory interfaces.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class AbstractCommandConfigurationFactory<T> extends
		AbstractMultiServiceFactory<T> {
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
				new HashSet<String>(this.getFactoryIDs()));
	}
}

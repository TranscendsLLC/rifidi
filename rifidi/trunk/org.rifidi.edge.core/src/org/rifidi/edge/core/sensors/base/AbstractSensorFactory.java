/**
 * 
 */
package org.rifidi.edge.core.sensors.base;

import org.rifidi.edge.api.rmi.dto.ReaderFactoryDTO;
import org.rifidi.edge.core.configuration.impl.AbstractServiceFactory;

/**
 * An abstract class for all ReaderConfigurationFactories to extend.
 * ReaderConfigurationFactories should register themselves to osgi under both
 * the AbstractSensorFactory and the org.rifidi.edge.core.configuration.ServiceFactory
 * interfaces
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class AbstractSensorFactory<T extends AbstractSensor<?>>
		extends AbstractServiceFactory<T> {

	/**
	 * Construct a DTO for this ReaderFactory
	 * 
	 * @return
	 */
	public ReaderFactoryDTO getReaderFactoryDTO() {
		return new ReaderFactoryDTO(this.getFactoryIDs().get(0),
				getDisplayName(), getDescription());
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
	
}

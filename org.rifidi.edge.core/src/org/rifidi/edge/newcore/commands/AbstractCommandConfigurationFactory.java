/**
 * 
 */
package org.rifidi.edge.newcore.commands;

import org.rifidi.configuration.AbstractMultiServiceFactory;

/**
 * A base class that all CommandConfigurationFactories should extend. Concrete
 * implementations should register themselves to OSGi under both the
 * AbstractCommandConfigurationFactory and
 * org.rifidi.configuration.ServiceFactory interfaces
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class AbstractCommandConfigurationFactory extends
		AbstractMultiServiceFactory {
	
	/**
	 * Get the globally unique ID of this CommandConfigurationFactory
	 * @return
	 */
	abstract public String getID();
}

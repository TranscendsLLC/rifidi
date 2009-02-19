/**
 * 
 */
package org.rifidi.edge.newcore.commands;

import org.rifidi.configuration.AbstractMultiServiceFactory;

/**
 * A base class that all CommandConfigurationFactories should extend. Concrete
 * implementations should register themselves to OSGi under both the
 * AbstractCommandConfigurationFactory and
 * org.rifidi.configuration.ServiceFactory interfaces. The protected value id
 * needs to be set to a global uniqeu value. It's not allowed to change that
 * value during runtime.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class AbstractCommandConfigurationFactory extends
		AbstractMultiServiceFactory {
	public abstract String getID(); 
}

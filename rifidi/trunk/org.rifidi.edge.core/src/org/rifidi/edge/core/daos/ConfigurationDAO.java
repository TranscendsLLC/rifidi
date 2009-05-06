/**
 * 
 */
package org.rifidi.edge.core.daos;
//TODO: Comments
import java.util.Set;

import org.rifidi.configuration.Configuration;

/**
 * This service listens for configurations on the osgi registry
 * @author Kyle Neumeier - kyle@pramari.com
 *
 */
public interface ConfigurationDAO {
	
	public Configuration getConfiguration(String serviceID);
	
	public Set<Configuration> getConfigurations();

}

/**
 * 
 */
package org.rifidi.edge.esper;
//TODO: Comments
import com.espertech.esper.client.EPServiceProvider;

/**
 * Service for handling Esper instances.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface EsperManagementService {
	/**
	 * Get an esper service provider.
	 * 
	 * @return
	 */
	EPServiceProvider getProvider();
}

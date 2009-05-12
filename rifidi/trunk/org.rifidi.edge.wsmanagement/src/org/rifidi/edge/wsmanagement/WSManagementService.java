
package org.rifidi.edge.wsmanagement;

import java.util.Dictionary;

/**
 * Service for managing web services.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface WSManagementService {
	/**
	 * Called to register a web service.
	 * 
	 * @param webService
	 * @param parameters
	 */
	void registerService(WebService webService, Dictionary<String, String> parameters);

	/**
	 * Called to unregister a web service.
	 * 
	 * @param webService
	 * @param parameters
	 */
	void unregisterService(WebService webService, Dictionary<String, String> parameters);
}

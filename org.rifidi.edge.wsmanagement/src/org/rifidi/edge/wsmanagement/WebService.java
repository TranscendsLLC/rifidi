/**
 * 
 */
package org.rifidi.edge.wsmanagement;
//TODO: Comments
import java.net.URL;

/**
 * Classes implementing this interface and registering to the OSGi registry will
 * be automatically published as web services.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface WebService {
	/**
	 * Get the URL this service is supposed to be bound to.
	 * 
	 * @return
	 */
	URL getUrl();

	/**
	 * Get the actual service object to be published as a web service.
	 * 
	 * @return
	 */
	Object getService();
}

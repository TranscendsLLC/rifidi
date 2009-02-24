/**
 * 
 */
package org.rifidi.configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.ServiceRegistration;

/**
 * Interface for rifidi services.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public abstract class RifidiService {

	/** The object that allows us to unregister the OSGi service */
	private ServiceRegistration serviceRegistration;
	/** The ID for this service */
	private String ID;
	/** The logger for this class */
	private static final Log logger = LogFactory.getLog(RifidiService.class);

	/**
	 * Id of the service. This ID has be unique for the whole application.
	 * 
	 * @return
	 */
	public String getID() {
		return ID;
	}

	/**
	 * Set the ID of the service. This can only be done once. Further calls have
	 * to be ignored.
	 * 
	 * @param id
	 */
	public void setID(String id) {
		if (ID == null) {
			this.ID = id;
		} else {
			logger.warn("Already set the ID for this Service " + ID);
		}
	}

	/**
	 * @param serviceRegistration
	 *            the serviceRegistration to set
	 */
	public void setServiceRegistration(ServiceRegistration serviceRegistration) {
		this.serviceRegistration = serviceRegistration;
	}

	/**
	 * Unregister this service from the OSGi registry
	 */
	public void destroy() {
		if (serviceRegistration != null) {
			serviceRegistration.unregister();
		} else {
			logger.warn("Service has not been registered " + ID);
		}
	}
}

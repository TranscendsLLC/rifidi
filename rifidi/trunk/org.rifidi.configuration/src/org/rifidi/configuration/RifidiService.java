
package org.rifidi.configuration;

import java.util.Hashtable;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * Interface for rifidi services.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * @author Kyle Neumeier - kyle@pramari.com
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
	 * This method should be called to register the service to OSGi
	 * 
	 * @param context
	 *            The BundleContext to use to register the service
	 * @param interfaces
	 *            The Interfaces to register the service under
	 */
	public void register(BundleContext context, Set<String> interfaces) {
		interfaces.add(RifidiService.class.getName());
		String[] serviceInterfaces = new String[interfaces.size()];
		serviceInterfaces = interfaces.toArray(serviceInterfaces);
		this.serviceRegistration = context.registerService(serviceInterfaces,
				this, new Hashtable<String, String>());
	}

	/**
	 * Unregister this service from the OSGi registry
	 */
	protected void unregister() {
		if (serviceRegistration != null) {
			serviceRegistration.unregister();
		} else {
			logger.warn("Service has not been registered " + ID);
		}
	}

	/**
	 * TODO: Method level comment.  
	 */
	public abstract void destroy();
}

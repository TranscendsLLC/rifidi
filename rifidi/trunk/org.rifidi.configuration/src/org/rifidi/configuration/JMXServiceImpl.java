/**
 * 
 */
package org.rifidi.configuration;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.configuration.annotations.Operation;
import org.rifidi.configuration.annotations.Property;
import org.rifidi.configuration.mbeans.ConfigurationControl;
import org.rifidi.configuration.mbeans.ConfigurationControlMBean;
import org.rifidi.configuration.mbeans.RifidiDynamicMBean;
import org.springframework.osgi.service.importer.OsgiServiceLifecycleListener;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class JMXServiceImpl implements JMXService, OsgiServiceLifecycleListener {
	/** Logger for this instance. */
	private static final Log logger = LogFactory.getLog(JMXServiceImpl.class);
	/** The JMX server used by thi service instance. */
	private MBeanServer mbs;
	/** Set containing the currently registered services. */
	private Map<PersistentService, ObjectName> persistentServicesToObjectNames; 

	/**
	 * Constructor.
	 */
	public JMXServiceImpl() {
		// register the MBeans server
		mbs = ManagementFactory.getPlatformMBeanServer();
		persistentServicesToObjectNames = new HashMap<PersistentService, ObjectName>();
		logger.debug("JMXServiceImpl created.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.osgi.service.importer.OsgiServiceLifecycleListener
	 * #bind(java.lang.Object, java.util.Map)
	 */
	@Override
	public void bind(Object service, Map arg1) throws Exception {
		logger.debug("Trying to bind to JMX: " + service);
		// avoid double init
		if (!persistentServicesToObjectNames.containsKey(service)) {
			// watch out, because of spring we are getting proxies, not the
			// actual services
			try {
				RifidiDynamicMBean mbean = ((PersistentService) service)
						.getRifidiDynamicMBean();
				ObjectName obname = new ObjectName("rifidi:type="
						+ mbean.getName());
				mbs.registerMBean(mbean, obname);
				persistentServicesToObjectNames.put(
						(PersistentService) service, obname);
			} catch (InstanceAlreadyExistsException e) {
				logger.error("Tried to register object twice: "
						+ service.toString() + " " + e);
			} catch (MBeanRegistrationException e) {
				logger.error(e);
			} catch (NotCompliantMBeanException e) {
				logger.error(e);
			} catch (MalformedObjectNameException e) {
				logger.error(e);
			} catch (IllegalArgumentException e) {
				logger.error(e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.osgi.service.importer.OsgiServiceLifecycleListener
	 * #unbind(java.lang.Object, java.util.Map)
	 */
	@Override
	public void unbind(Object service, Map arg1) throws Exception {
		logger.debug("removing " + service + " "
				+ persistentServicesToObjectNames.containsKey(service));
		if (persistentServicesToObjectNames.containsKey(service)) {
			// unregister from jmx
			try {
				mbs.unregisterMBean(persistentServicesToObjectNames
						.get(service));
				persistentServicesToObjectNames.remove(service);
			} catch (InstanceNotFoundException e) {
				logger.error(e);
			} catch (MBeanRegistrationException e) {
				logger.error(e);
			}
		}
	}

	/**
	 * Used to store results of reflections calls.
	 * 
	 * @author Jochen Mader - jochen@pramari.com
	 * 
	 */
	protected class CachedReflectionsResults {
		public Map<String, Property> nameToProperty = new HashMap<String, Property>();
		public Map<String, Operation> nameToOperation = new HashMap<String, Operation>();
		public Method name;
	}

	/**
	 * @param persistentServices
	 *            the persistentServices to set
	 */
	public void setPersistentServices(Set<PersistentService> persistentServices) {
		// take a snapshot an initialize
		Set<PersistentService> tempServices = new HashSet<PersistentService>(
				persistentServices);
		for (PersistentService persistentService : tempServices) {
			try {
				bind(persistentService, null);
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}

	/**
	 * Set the reference to the configuration control mbean.
	 * 
	 * @param mbean
	 */
	public void setConfigurationControlMBean(ConfigurationControlMBean mbean) {
		ObjectName obname;
		try {
			obname = new ObjectName("rifidi:type=ConfigurationControl");
			mbs.registerMBean(mbean, obname);
			logger.debug("ConfigurationControl registered to JMX.");
		} catch (MalformedObjectNameException e) {
			logger.error("Problem binding ConfigurationControl: " + e);
		} catch (NullPointerException e) {
			logger.error("Problem binding ConfigurationControl: " + e);
		} catch (InstanceAlreadyExistsException e) {
			logger.error("Problem binding ConfigurationControl: " + e);
		} catch (MBeanRegistrationException e) {
			logger.error("Problem binding ConfigurationControl: " + e);
		} catch (NotCompliantMBeanException e) {
			logger.error("Problem binding ConfigurationControl: " + e);
		}
	}
}

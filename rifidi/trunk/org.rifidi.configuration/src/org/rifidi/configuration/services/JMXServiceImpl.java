
package org.rifidi.configuration.services;

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
import org.rifidi.configuration.Configuration;
import org.rifidi.configuration.annotations.Operation;
import org.rifidi.configuration.annotations.Property;
import org.rifidi.configuration.mbeans.ConfigurationControlMBean;
import org.springframework.osgi.service.importer.OsgiServiceLifecycleListener;

/**
 * TODO: Class level comment.  
 * 
 * @author Jochen Mader - jochen@pramari.com
 */
public class JMXServiceImpl implements JMXService, OsgiServiceLifecycleListener {
	/** Logger for this instance. */
	private static final Log logger = LogFactory.getLog(JMXServiceImpl.class);
	/** The JMX server used by thi service instance. */
	private MBeanServer mbs;
	/** Set containing the currently registered services. */
	private Map<Configuration, ObjectName> persistentServicesToObjectNames;

	/**
	 * Constructor.
	 */
	public JMXServiceImpl() {
		// register the MBeans server
		mbs = ManagementFactory.getPlatformMBeanServer();
		persistentServicesToObjectNames = new HashMap<Configuration, ObjectName>();
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
		// avoid double init
		synchronized (persistentServicesToObjectNames) {
			if (!persistentServicesToObjectNames.containsKey(service)) {
				logger.debug("Binding " + service + " to JMX.");
				// watch out, because of spring we are getting proxies, not the
				// actual services
				try {
					Configuration mbean = (Configuration) service;
					ObjectName obname = new ObjectName("rifidi:type="
							+ mbean.getServiceID());
					mbs.registerMBean(mbean, obname);
					persistentServicesToObjectNames.put(
							(Configuration) service, obname);
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
		synchronized (persistentServicesToObjectNames) {
			if (persistentServicesToObjectNames.containsKey(service)) {
				// unregister from jmx
				logger.debug("Unbinding " + service + " from JMX.");
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
	}

	/**
	 * Used to store results of reflections calls.
	 * 
	 * @author Jochen Mader - jochen@pramari.com
	 */
	protected class CachedReflectionsResults {
		public Map<String, Property> nameToProperty = new HashMap<String, Property>();
		public Map<String, Operation> nameToOperation = new HashMap<String, Operation>();
		public Method name;
	}

	/**
	 * Sets the configuration services.  
	 * 
	 * TODO: Method level comment.  
	 * 
	 * @param persistentServices
	 *            the persistentServices to set
	 */
	public void setConfigurations(Set<Configuration> configurationServices) {
		// take a snapshot an initialize
		Set<Configuration> tempServices = new HashSet<Configuration>(
				configurationServices);
		for (Configuration configurationService : tempServices) {
			try {
				bind(configurationService, null);
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

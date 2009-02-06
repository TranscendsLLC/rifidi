package org.rifidi.configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {
	/** Logger for this class. */
	private static final Log logger = LogFactory.getLog(Activator.class);
//	/** Registration object of the ConfigurationService instance. */
//	private ServiceRegistration configurationServiceRegistration;
//	/** Instance of the configuration service */
//	private ConfigurationServiceImpl ser;
//	/** Registration object of the JMXService instance. */
//	private ServiceRegistration jmxServiceRegistration;
//	/** Instance of the registry service. */
//	private JMXServiceImpl jmx;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		// if (System.getProperty("org.rifidi.edge.configuration") != null
		// && (new File(System
		// .getProperty("org.rifidi.edge.configuration")))
		// .exists()) {
			// configure and register jmx service
//			jmx = new JMXServiceImpl();
//			jmx.setContext(context);
//			jmxServiceRegistration = context.registerService(JMXService.class
//					.getName(), jmx, null);
//			context.addServiceListener(jmx, "(" + Constants.OBJECTCLASS + "="
//					+ PersistentService.class.getName() + ")");

			// get the config admin
//			ServiceReference ref = context
//					.getServiceReference(ConfigurationAdmin.class.getName());
//			ConfigurationAdmin confAdmin = (ConfigurationAdmin) context
//					.getService(ref);

			// initialize service
//			ConfigurationServiceImpl ser = new ConfigurationServiceImpl();
//			ser.setConfigurationAdminService(confAdmin);
//			ser.setContext(context);
			// register service
//			configurationServiceRegistration = context.registerService(
//					ConfigurationService.class.getName(), ser, null);
//			context.addServiceListener(ser, "(" + Constants.OBJECTCLASS + "="
//					+ ManagedServiceFactory.class.getName() + ")");
//			return;
//		}
//		logger.fatal("Missing configuration file");
//		throw new RuntimeException("Missing configuration file");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
//		jmxServiceRegistration.unregister();
//		context.removeServiceListener(jmx);
//		configurationServiceRegistration.unregister();
	}

}

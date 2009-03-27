/**
 * 
 */
package org.rifidi.edge.core.daos;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.configuration.Configuration;
import org.rifidi.edge.core.notifications.internal.NotifierServiceWrapper;
import org.rifidi.edge.notifications.NotifierService;

/**
 * This is the implementation that listens for OSGi services for Configurations.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ConfigurationDAOImpl implements ConfigurationDAO {

	/** The available Configurations */
	private Map<String, Configuration> configurations;
	private final static Log logger = LogFactory
			.getLog(ConfigurationDAOImpl.class);
	/** A notifier for JMS. Remove once we have aspects */
	private NotifierServiceWrapper notifierService;
	/** reader dao. Won't be needed once we have aspects */
	private ReaderDAO readerDAO;
	/** commanddao. won't be needed once we have aspects */
	private CommandDAO commandDAO;

	/**
	 * constructor
	 */
	public ConfigurationDAOImpl() {
		configurations = new HashMap<String, Configuration>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.internal.ConfigurationDAO#getConfiguration(java.
	 * lang.String)
	 */
	@Override
	public Configuration getConfiguration(String serviceID) {
		return configurations.get(serviceID);
	}

	/**
	 * Called by spring
	 * 
	 * @param readerDAO
	 *            the readerDAO to set
	 */
	public void setReaderDAO(ReaderDAO readerDAO) {
		this.readerDAO = readerDAO;
	}

	/**
	 * called by spring
	 * 
	 * @param commandDAO
	 *            the commandDAO to set
	 */
	public void setCommandDAO(CommandDAO commandDAO) {
		this.commandDAO = commandDAO;
	}

	/**
	 * Called by Spring
	 * 
	 * @param notifierService
	 *            the notifierService to set
	 */
	public void setNotifierService(NotifierServiceWrapper notifierService) {
		this.notifierService = notifierService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.daos.ConfigurationDAO#getConfigurations()
	 */
	@Override
	public Set<Configuration> getConfigurations() {
		return new HashSet<Configuration>(configurations.values());
	}

	/**
	 * Used by spring to bind a new Configuration to this service.
	 * 
	 * @param Configuration
	 *            the configuration to bind
	 * @param parameters
	 */
	public void bindConfiguration(Configuration configuration,
			Dictionary<String, String> parameters) {
		logger.info("config bound: " + configuration.getServiceID());
		configurations.put(configuration.getServiceID(), configuration);

		// TODO: Get rid of this code once we get aspects!!!!!
		if (this.notifierService == null) {
			return;
		}
		NotifierService service = this.notifierService.getService();
		if (service != null) {
			String id = configuration.getServiceID();
			if (null != readerDAO.getReaderByID(id)) {
				service.addReaderEvent(id);
			} else if (null != commandDAO.getCommandByID(id)) {
				// TODO: Add command notification
			}
		}
	}

	/**
	 * Used by spring to unbind a disappearing Configuration service from this
	 * service.
	 * 
	 * @param configuration
	 *            the Configuration to unbind
	 * @param parameters
	 */
	public void unbindConfiguration(Configuration configuration,
			Dictionary<String, String> parameters) {
		logger.info("config unbound: " + configuration.getServiceID());
		configurations.remove(configuration.getServiceID());

		// TODO: Get rid of this code once we get aspects!!!!!
		if (this.notifierService == null) {
			return;
		}
		NotifierService service = this.notifierService.getService();
		if (service != null) {
			String id = configuration.getServiceID();
			if (null != readerDAO.getReaderByID(id)) {
				service.removeReaderEvent(id);
			} else if (null != commandDAO.getCommandByID(id)) {
				// TODO: Remove command notification
			}
		}
	}

	/**
	 * Used by spring to give the initial list of readerSession configuration
	 * 
	 * 
	 * @param configurations
	 *            the initial list of available configurations
	 */
	public void setConfigurations(Set<Configuration> configurations) {
		for (Configuration configuration : configurations) {
			this.configurations
					.put(configuration.getServiceID(), configuration);
		}
	}

}

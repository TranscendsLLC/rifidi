
package org.rifidi.configuration.mbeans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.configuration.services.ConfigurationService;

/**
 * TODO: Class level comment.  
 * 
 * @author Jochen Mader - jochen@pramari.com
 */
public class ConfigurationControl implements ConfigurationControlMBean {
	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(ConfigurationControl.class);
	/** Reference to the JMX service. */
	private ConfigurationService impl;

	/**
	 * Constructor.  
	 * 
	 * @param impl
	 */
	public ConfigurationControl() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.configuration.mbeans.ConfigurationControlMBean#reload()
	 */
	@Override
	public void reload() {
		logger.debug("Please implement reload.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.configuration.mbeans.ConfigurationControlMBean#save()
	 */
	@Override
	public void save() {
		impl.storeConfiguration();
	}

	/**
	 * Sets the configuration service for this class.  
	 * 
	 * @param impl
	 *            the impl to set
	 */
	public void setConfigurationService(ConfigurationService impl) {
		this.impl = impl;
	}

}

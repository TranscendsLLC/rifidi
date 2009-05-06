/**
 * 
 */
package org.rifidi.configuration.mbeans;
//TODO: Comments
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.configuration.services.ConfigurationService;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class ConfigurationControl implements ConfigurationControlMBean {
	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(ConfigurationControl.class);
	/** Reference to the JMX service. */
	private ConfigurationService impl;

	/**
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
	 * @param impl
	 *            the impl to set
	 */
	public void setConfigurationService(ConfigurationService impl) {
		this.impl = impl;
	}

}

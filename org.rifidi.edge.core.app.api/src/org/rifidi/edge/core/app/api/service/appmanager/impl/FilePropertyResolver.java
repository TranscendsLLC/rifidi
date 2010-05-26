/**
 * 
 */
package org.rifidi.edge.core.app.api.service.appmanager.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.app.api.service.appmanager.RifidiAppPropertyResolver;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.core.io.UrlResource;

/**
 * Resolve the properties of an application using a file.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class FilePropertyResolver implements RifidiAppPropertyResolver {

	private static final Log logger = LogFactory
			.getLog(FilePropertyResolver.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.app.api.service.appmanager.RifidiAppPropertyResolver
	 * #reolveProperties(java.lang.String, java.lang.String)
	 */
	@Override
	public Properties reolveProperties(String appGroup, String appName) {
		PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
		try {
			UrlResource resource = new UrlResource("file:"
					+ System.getProperty("user.dir") + File.separator
					+ System.getProperty("org.rifidi.edge.applications")
					+ File.separator + appGroup + File.separator + appName
					+ ".properties");

			if (!resource.exists()) {
				throw new IOException("Properties file does not exist: "
						+ resource);
			}
			propertiesFactoryBean.setLocation(resource);
			propertiesFactoryBean.afterPropertiesSet();
			Properties properties = (Properties) propertiesFactoryBean
					.getObject();
			return new Properties(properties);

		} catch (MalformedURLException ex) {
			logger.debug(ex);
		} catch (IOException ex) {
			logger.debug(ex);
		}
		return new Properties();
	}

}

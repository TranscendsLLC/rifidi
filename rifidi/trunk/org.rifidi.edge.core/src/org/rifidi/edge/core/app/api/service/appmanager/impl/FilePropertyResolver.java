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
		Properties retVal = new Properties();
		try {
			UrlResource groupResource = new UrlResource("file:"
					+ System.getProperty("org.rifidi.home") + File.separator
					+ System.getProperty("org.rifidi.edge.applications")
					+ File.separator + appGroup + File.separator + appGroup
					+ ".properties");
			UrlResource appResource = new UrlResource("file:"
					+ System.getProperty("org.rifidi.home") + File.separator
					+ System.getProperty("org.rifidi.edge.applications")
					+ File.separator + appGroup + File.separator + appName
					+ ".properties");
			
			try{				
				retVal.putAll(getProperties(groupResource));
			}catch(IOException ex){
				logger.debug(ex);
			}
			try{
				retVal.putAll(getProperties(appResource));
			}catch(IOException ex){
				logger.debug(ex);
			}

		} catch (MalformedURLException ex) {
			logger.debug(ex);
		}
		return retVal;
	}

	private Properties getProperties(UrlResource resource) throws IOException{
		PropertiesFactoryBean appPropertiesFactoryBean = new PropertiesFactoryBean();
		appPropertiesFactoryBean.setLocation(resource);
		appPropertiesFactoryBean.afterPropertiesSet();
		return (Properties) appPropertiesFactoryBean.getObject();
	}
}

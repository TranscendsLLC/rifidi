/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
package org.rifidi.edge.api.service.appmanager;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
	 * org.rifidi.edge.api.service.appmanager.RifidiAppPropertyResolver
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

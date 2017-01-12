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
package org.rifidi.edge.rmi;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.api.EdgeServerManagerService;
import org.rifidi.edge.configuration.ConfigurationService;

/**
 * This is the implementation for the Edge Server RMI Stub
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class EdgeServerManagerServiceImpl implements EdgeServerManagerService {

	/** The configuration service for the edge server */
	private ConfigurationService configurationService;
	/** The logger for this class */
	private static Log logger = LogFactory.getLog(EdgeServerManagerServiceImpl.class);
	/** The timestamp of when the edgeserver was started up */
	private Long startupDate;

	/**
	 * Constructor.  
	 */
	public EdgeServerManagerServiceImpl() {
		// TODO: this should probably really be the startup of the core plugin
		// and not the startup of the RMI plugin
		startupDate = System.currentTimeMillis();
	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.api.rmi.EdgeServerStub#save()
	 */
	@Override
	public void save() {
		logger.debug("RMI Call: Save");
		configurationService.storeConfiguration();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.api.rmi.EdgeServerStub#getStartupTime()
	 */
	@Override
	public Long getStartupTime() {
		logger.debug("RMI Call: getStartupTime");
		return startupDate;
	}

	/**
	 * Sets the configuration service for this class.  
	 * 
	 * @param configurationService
	 *            the configurationService to set
	 */
	public void setConfigurationService(
			ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

}

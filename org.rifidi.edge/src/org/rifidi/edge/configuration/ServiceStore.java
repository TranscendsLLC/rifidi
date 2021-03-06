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
/**
 * 
 */
package org.rifidi.edge.configuration;

import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlType;

import org.rifidi.edge.api.SessionDTO;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
@XmlType
public class ServiceStore {
	private String factoryID;
	private String serviceID;
	private Map<String, String> attributes;
	private Set<SessionDTO> sessionDTOs;
	/**
	 * @return the sessionDTO
	 */
	public Set<SessionDTO> getSessionDTOs() {
		return sessionDTOs;
	}

	/**
	 * @param sessionDTO the sessionDTO to set
	 */
	public void setSessionDTOs(Set<SessionDTO> sessionDTOs) {
		this.sessionDTOs = sessionDTOs;
	}

	/**
	 * @return the factoryID
	 */
	public String getFactoryID() {
		return factoryID;
	}

	/**
	 * @param factoryID
	 *            the factoryID to set
	 */
	public void setFactoryID(String factoryID) {
		this.factoryID = factoryID;
	}

	/**
	 * @return the serviceID
	 */
	public String getServiceID() {
		return serviceID;
	}

	/**
	 * @param serviceID
	 *            the serviceID to set
	 */
	public void setServiceID(String serviceID) {
		this.serviceID = serviceID;
	}

	/**
	 * @return the attributes
	 */
	public Map<String, String> getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes
	 *            the attributes to set
	 */
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

}

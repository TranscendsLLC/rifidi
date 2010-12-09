/*
 * 
 * ServiceStore.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
/**
 * 
 */
package org.rifidi.edge.configuration;

import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlType;

import org.rifidi.edge.api.rmi.dto.SessionDTO;

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

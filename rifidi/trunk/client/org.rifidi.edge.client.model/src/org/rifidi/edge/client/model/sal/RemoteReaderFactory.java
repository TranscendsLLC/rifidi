/*
 * RemoteReaderFactory.java
 * 
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the EPL License
 *                    A copy of the license is included in this distribution under Rifidi-License.txt 
 */

package org.rifidi.edge.client.model.sal;

import javax.management.MBeanInfo;

import org.rifidi.edge.api.rmi.dto.ReaderFactoryDTO;

/**
 * Model object that represents a ReaderFactory on the server
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class RemoteReaderFactory {

	/** The DTO associated with this factory */
	private ReaderFactoryDTO dto;
	/** The DTO that describes this factory */
	private MBeanInfo mbeanInfo;

	/**
	 * Constructor
	 * 
	 * @param dto
	 *            The ReaderFactoryDTO for this object
	 * @param mbeanInfo
	 *            The MBeanInfo that describes this factory
	 */
	public RemoteReaderFactory(ReaderFactoryDTO dto, MBeanInfo mbeanInfo) {
		super();
		this.dto = dto;
		this.mbeanInfo = mbeanInfo;
	}

	/**
	 * Returns the ID of the readerFactory.  
	 * 
	 * @return The ID of this factory
	 */
	public String getID() {
		return dto.getReaderFactoryID();
	}

	/**
	 * Returns the display name.  
	 * 
	 * @return The display name for this factory
	 */
	public String getDisplayName() {
		return dto.getReaderFactoryDisplayName();
	}

	/**
	 * Returns the description of the reader factory.  .  
	 * 
	 * @return The description of this reader factory
	 */
	public String getDescription() {
		return dto.getReaderFactoryDescription();
	}

	/**
	 * Returns the mbeanInfo.  
	 * 
	 * @return The MBeanInfo that describes this factory
	 */
	public MBeanInfo getMbeanInfo() {
		return mbeanInfo;
	}

}

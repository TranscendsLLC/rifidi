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
package org.rifidi.edge.adapter.llrp.commands;

import javax.management.MBeanInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.configuration.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.configuration.JMXMBean;
import org.rifidi.edge.configuration.Property;
import org.rifidi.edge.configuration.PropertyType;
import org.rifidi.edge.sensors.AbstractCommandConfiguration;

/**
 * 
 * 
 * @author Matthew Dean
 */
@JMXMBean
public class LLRPDeleteROSpecCommandConfiguration extends
		AbstractCommandConfiguration<LLRPDeleteROSpecCommand> {
	/** Logger for this class. */
	private static final Log logger=LogFactory.getLog(LLRPDeleteROSpecCommandConfiguration.class);
	
	/** ID of the RoSpec*/
	private int roSpecID = 1;
	

	/** The name of this command type */
	public static final String name = "DeleteROSpec-Configuration";
	
	public static final MBeanInfo mbeaninfo;
	static {
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		mbeaninfo = strategy
				.getMBeanInfo(LLRPDeleteROSpecCommandConfiguration.class);
	}
	
	public LLRPDeleteROSpecCommandConfiguration() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.commands.AbstractCommandConfiguration#getCommand
	 * (java.lang.String)
	 */
	@Override
	public LLRPDeleteROSpecCommand getCommand(String readerID) {
		LLRPDeleteROSpecCommand llrpdrc = new LLRPDeleteROSpecCommand(super
				.getID());
		llrpdrc.setROSPecID(roSpecID);
		return llrpdrc;
	}

	/**
	 * Sets the ROSpecID.
	 * 
	 * @param roSpecID
	 *            the roSpecID to set
	 */
	public void setROSpecID(Integer roSpecID) {
		if(logger.isTraceEnabled()){
			logger.trace("Called the setROSpecID in LLRPGetTagList");	
		}
		this.roSpecID = roSpecID;
	}

	/**
	 * Gets the ROSpecID.
	 * 
	 * @return the roSpecID
	 */
	@Property(displayName = "RO Spec ID", defaultValue = "1", description = "The ID of the RO Spec to delete", writable = true, type = PropertyType.PT_INTEGER, minValue="1", maxValue="16535")
	public int getROSpecID() {
		return roSpecID;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.configuration.RifidiService#getMBeanInfo()
	 */
	@Override
	public MBeanInfo getMBeanInfo() {
		return (MBeanInfo) mbeaninfo.clone();
	}
	
	
}

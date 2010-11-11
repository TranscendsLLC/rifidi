/*
 *  LLRPDeleteROSpecCommandConfiguration.java
 *
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
package org.rifidi.edge.adapter.llrp.commands;

import javax.management.MBeanInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.configuration.annotations.JMXMBean;
import org.rifidi.edge.core.configuration.annotations.Property;
import org.rifidi.edge.core.configuration.annotations.PropertyType;
import org.rifidi.edge.core.configuration.mbeanstrategies.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;

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
	 * org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration#getCommand
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
	 * @see org.rifidi.edge.core.configuration.RifidiService#getMBeanInfo()
	 */
	@Override
	public MBeanInfo getMBeanInfo() {
		return (MBeanInfo) mbeaninfo.clone();
	}
	
	
}

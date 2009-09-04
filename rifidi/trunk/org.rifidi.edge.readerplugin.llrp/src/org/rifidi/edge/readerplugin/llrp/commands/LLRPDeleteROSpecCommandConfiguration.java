/*
 *  LLRPDeleteROSpecCommandConfiguration.java
 *
 *  Created:	Jun 18, 2009
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.llrp.commands;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration#
	 * getCommandDescription()
	 */
	@Override
	public String getCommandDescription() {
		return "Delete the ROSpec with the given ID.";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration#
	 * getCommandName()
	 */
	@Override
	public String getCommandName() {
		return name;
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
	@Property(displayName = "roSpecID", defaultValue = "1", description = "The ID of the ROSpec for the DeleteROSpec "
			+ "command", writable = true, type = PropertyType.PT_INTEGER, minValue="1", maxValue="16535")
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

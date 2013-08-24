/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.adapter.llrp.commands;

import javax.management.MBeanInfo;

import org.rifidi.edge.configuration.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.configuration.JMXMBean;
import org.rifidi.edge.configuration.Property;
import org.rifidi.edge.configuration.PropertyType;
import org.rifidi.edge.sensors.AbstractCommandConfiguration;

/**
 * This class configures a GetTagList command.
 * 
 * @author Matthew Dean
 */
@JMXMBean
public class LLRPGetTagListCommandConfiguration extends
		AbstractCommandConfiguration<LLRPGetTagListCommand> {
	private int roSpecID = 1;
	
	public static final MBeanInfo mbeaninfo;
	static {
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		mbeaninfo = strategy
				.getMBeanInfo(LLRPGetTagListCommandConfiguration.class);
	}
	/**
	 * Constructor of LLRPGetTagListCommandConfiguration.
	 */
	public LLRPGetTagListCommandConfiguration() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.commands.AbstractCommandConfiguration#getCommand
	 * ()
	 */
	@Override
	public LLRPGetTagListCommand getCommand(String readerID) {
		LLRPGetTagListCommand llrpgtlc = new LLRPGetTagListCommand(super
				.getID());
		return llrpgtlc;
	}

	/**
	 * Sets the ROSpecID.
	 * 
	 * @param roSpecID
	 *            the roSpecID to set
	 */
	public void setROSpecID(Integer roSpecID) {
		this.roSpecID = roSpecID;
	}

	/**
	 * Gets the ROSpecID.
	 * 
	 * @return the roSpecID
	 */
	@Property(displayName = "RO Spec ID", description = "The ID of the ROSpec for the "
			+ "Poll command", writable = true, type = PropertyType.PT_INTEGER, defaultValue = ""
			+ "1", minValue = "1", maxValue = "16535")
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

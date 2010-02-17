/*
 * Awid2010PortalIDCommandConfiguration.java
 * 
 * Created:     Oct 20th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:     The software in this package is published under the terms of the EPL License
 *                   A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.readerplugin.awid.commands.awid2010;

import javax.management.MBeanInfo;

import org.rifidi.edge.core.configuration.annotations.JMXMBean;
import org.rifidi.edge.core.configuration.annotations.Property;
import org.rifidi.edge.core.configuration.annotations.PropertyType;
import org.rifidi.edge.core.configuration.mbeanstrategies.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 * @author Daniel Gómez - dgomez@idlinksolutions.com
 * 
 */
@JMXMBean
public class AwidReadBlockDataCommandConfiguration extends
		AbstractCommandConfiguration<AwidReadBlockDataCommand> {
	
	private byte memoryBank=0x01;
	public static final MBeanInfo mbeaninfo;
	static {
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		mbeaninfo = strategy
				.getMBeanInfo(AwidReadBlockDataCommandConfiguration.class);
	}

	@Override
	public AwidReadBlockDataCommand getCommand(String readerID) {
		return new AwidReadBlockDataCommand(super.getID(),  memoryBank);
	}

	@Override
	public MBeanInfo getMBeanInfo() {
		return (MBeanInfo)mbeaninfo.clone();
	}

	/**
	 * @return the memoryBank
	 */
	@Property(category = "Memory Bank", defaultValue = "01", description = "0x01 EPC bank, 0x02 TID bank, 0x03 User bank", displayName = "Memory Bank", orderValue = 2, type = PropertyType.PT_INTEGER, writable = true, minValue="0", maxValue="3")
	public Integer getMemoryBank() {
		return new Integer(memoryBank);
	}

	/**
	 * @param memoryBank the memoryBank to set
	 */
	public void setMemoryBank(Integer memoryBank) {
		//TODO: bounds check
		this.memoryBank = memoryBank.byteValue();
	}
}

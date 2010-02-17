/*
 * Awid2010StopCommandConfiguration.java
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
import org.rifidi.edge.core.configuration.mbeanstrategies.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
@JMXMBean
public class AwidStopCommandConfiguration extends
		AbstractCommandConfiguration<AwidStopCommand> {

	public static final MBeanInfo mbeaninfo;
	static {
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		mbeaninfo = strategy
				.getMBeanInfo(AwidStopCommandConfiguration.class);
	}

	@Override
	public AwidStopCommand getCommand(String readerID) {
		return new AwidStopCommand(super.getID());
	}

	@Override
	public MBeanInfo getMBeanInfo() {
		return (MBeanInfo)mbeaninfo.clone();
	}

}

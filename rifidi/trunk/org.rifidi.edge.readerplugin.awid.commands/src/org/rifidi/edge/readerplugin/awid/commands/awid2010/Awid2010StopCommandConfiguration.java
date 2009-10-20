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
public class Awid2010StopCommandConfiguration extends
		AbstractCommandConfiguration<Awid2010StopCommand> {

	public static final MBeanInfo mbeaninfo;
	static {
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		mbeaninfo = strategy
				.getMBeanInfo(Awid2010StopCommandConfiguration.class);
	}

	@Override
	public Awid2010StopCommand getCommand(String readerID) {
		return new Awid2010StopCommand(super.getID());
	}

	@Override
	public String getCommandDescription() {
		return "Stop the current executing command on the Awid";
	}

	@Override
	public String getCommandName() {
		return "Awid Stop Command";
	}

	@Override
	public MBeanInfo getMBeanInfo() {
		return (MBeanInfo)mbeaninfo.clone();
	}

}

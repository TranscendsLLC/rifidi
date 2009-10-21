/*
 *  ThingmagicGetTagListCommandConfiguration.java
 *
 *  Created:	Sep 30, 2009
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.thingmagic.commands;

import javax.management.MBeanInfo;

import org.rifidi.edge.core.configuration.annotations.JMXMBean;
import org.rifidi.edge.core.configuration.mbeanstrategies.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;

/**
 * 
 * 
 * @author Matthew Dean
 */
@JMXMBean
public class ThingmagicGetTagListCommandConfiguration extends
		AbstractCommandConfiguration<ThingmagicGetTagListCommand> {
	
	/** Description of the command. */
	public static final MBeanInfo mbeaninfo;
	static {
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		mbeaninfo = strategy
				.getMBeanInfo(ThingmagicGetTagListCommandConfiguration.class);
	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration#getCommand(java.lang.String)
	 */
	@Override
	public ThingmagicGetTagListCommand getCommand(String readerID) {
		ThingmagicGetTagListCommand c = new ThingmagicGetTagListCommand(super.getID());
		c.setReader(readerID);
		return c;
	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.core.configuration.RifidiService#getMBeanInfo()
	 */
	@Override
	public MBeanInfo getMBeanInfo() {
		return (MBeanInfo) mbeaninfo.clone();
	}

}

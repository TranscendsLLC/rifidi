/**
 * 
 */
package org.rifidi.edge.adapter.thinkify50;

import javax.management.MBeanInfo;

import org.rifidi.edge.configuration.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.configuration.JMXMBean;
import org.rifidi.edge.sensors.AbstractCommandConfiguration;

/**
 * 
 * 
 * @author matt
 */
@JMXMBean
public class Thinkify50PushCommandConfiguration extends
		AbstractCommandConfiguration<Thinkify50PushCommand> {

	public static final MBeanInfo mbeaninfo;
	static {
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		mbeaninfo = strategy
				.getMBeanInfo(Thinkify50PushCommandConfiguration.class);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.sensors.AbstractCommandConfiguration#getCommand(java.lang.String)
	 */
	@Override
	public Thinkify50PushCommand getCommand(String readerID) {
		Thinkify50PushCommand command = new Thinkify50PushCommand(super.getID());
		return command;
	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.configuration.RifidiService#getMBeanInfo()
	 */
	@Override
	public MBeanInfo getMBeanInfo() {
		return (MBeanInfo) mbeaninfo.clone();
	}

}

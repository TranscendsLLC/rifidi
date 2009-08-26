/**
 * 
 */
package org.rifidi.edge.readerplugin.alien.commands;

import javax.management.MBeanInfo;

import org.rifidi.edge.core.configuration.mbeanstrategies.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;

/**
 * @author kyle
 * 
 */
public class AlienAutonomousModeStopCommandConfiguration
		extends
			AbstractCommandConfiguration<AlienAutonomousModeStopCommand> {

	/** The name of this command type */
	public static final String name = "Alien-Autonomous-Off";
	public static final MBeanInfo mbeaninfo;
	static{
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		mbeaninfo = strategy.getMBeanInfo(AlienAutonomousModeStopCommandConfiguration.class);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration#getCommand
	 * (java.lang.String)
	 */
	@Override
	public AlienAutonomousModeStopCommand getCommand(String readerID) {
		return new AlienAutonomousModeStopCommand(super.getID());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration#
	 * getCommandDescription()
	 */
	@Override
	public String getCommandDescription() {
		return "A command to turn off autonomous mode";
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

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.configuration.RifidiService#getMBeanInfo()
	 */
	@Override
	public MBeanInfo getMBeanInfo() {
		return (MBeanInfo)mbeaninfo.clone();
	}

}

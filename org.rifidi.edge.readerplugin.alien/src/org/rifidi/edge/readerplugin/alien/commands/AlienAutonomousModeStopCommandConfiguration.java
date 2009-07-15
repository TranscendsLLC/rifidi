/**
 * 
 */
package org.rifidi.edge.readerplugin.alien.commands;

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

}

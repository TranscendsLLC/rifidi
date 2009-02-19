/**
 * 
 */
package org.rifidi.edge.readerplugin.alien.commands;

import org.rifidi.configuration.annotations.JMXMBean;
import org.rifidi.configuration.annotations.Property;
import org.rifidi.edge.newcore.commands.CommandFactory;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
@JMXMBean
public class AlienGetTagListCommandFactory implements
		CommandFactory<AlienGetTagListCommand> {
	/** Interval between two reads. */
	private Integer interval = 10;
	/** Name of the command. */
	private static final String name = "Alien9800-GetTagList";
	/** Description of the command. */
	private static final String description = "Get list of tags that appeared in the antenna field since the last call.";
	/** Unique id for this command. */
	private String id;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.newcore.commands.CommandFactory#getCommandDescription()
	 */
	@Override
	public String getCommandDescription() {
		return description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.newcore.commands.CommandFactory#getCommandName()
	 */
	@Override
	public String getCommandName() {
		return name;
	}

	/**
	 * @return the interval
	 */
	@Property(name = "Interval", description = "Interval between two reads", writable = true)
	public Integer getInterval() {
		return interval;
	}

	/**
	 * @param interval
	 *            the interval to set
	 */
	public void setInterval(Integer interval) {
		this.interval = interval;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.newcore.commands.CommandFactory#getCommand()
	 */
	@Override
	public AlienGetTagListCommand getCommand() {
		return new AlienGetTagListCommand();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.configuration.RifidiService#getID()
	 */
	@Override
	public String getID() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.configuration.RifidiService#setID(java.lang.String)
	 */
	@Override
	public void setID(String id) {
		this.id = id;
	}

}

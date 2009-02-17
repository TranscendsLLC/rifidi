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
	private Integer interval = 10;

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
		System.out.println("got " + interval);
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.newcore.commands.CommandFactory#getCommand()
	 */
	@Override
	public AlienGetTagListCommand getCommand() {
		return new AlienGetTagListCommand();
	}

}

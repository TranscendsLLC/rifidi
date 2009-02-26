/**
 * 
 */
package org.rifidi.edge.readerplugin.alien.commands;

import org.rifidi.configuration.annotations.JMXMBean;
import org.rifidi.configuration.annotations.Property;
import org.rifidi.edge.core.commands.AbstractCommandConfiguration;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
@JMXMBean
public class AlienGetTagListCommandConfiguration extends
		AbstractCommandConfiguration<AlienGetTagListCommand> {
	/** Name of the command. */
	private static final String name = "Alien9800-GetTagList";
	/** Description of the command. */
	private static final String description = "Get Tags";
	/** Interval between two reads. */
	private int interval = 10;
	/**Type of tag to read*/
	private int tagType = 0;
	/**Length of time tags stay in taglist*/
	private int persistTime = -1;
	/**Antennas to scan*/
	private String antenna_sequence = "0";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.commands.AbstractCommandConfiguration#getCommandDescription
	 * ()
	 */
	@Override
	public String getCommandDescription() {
		return description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.commands.AbstractCommandConfiguration#getCommandName()
	 */
	@Override
	public String getCommandName() {
		return name;
	}

	/**
	 * @return the interval
	 */
	@Property(name = "Interval", description = "Interval between two reads", writable = true)
	public String getInterval() {
		return Integer.toString(interval);
	}

	/**
	 * @param interval
	 *            the interval to set
	 */
	public void setInterval(String interval) {
		this.interval = Integer.parseInt(interval);
	}

	/**
	 * @return the tagType
	 */
	@Property(name = "Tag Type", description="0=Gen1, 1=Gen2, 2=Both", writable=true)
	public String getTagType() {
		return  Integer.toString(tagType);
	}

	/**
	 * @param tagType the tagType to set
	 */
	public void setTagType(String tagType) {
		this.tagType = Integer.parseInt(tagType);
	}

	/**
	 * @return the persistTime
	 */
	@Property(name="Persist Time", description="Length of time a tag stays in memory", writable=true)
	public String getPersistTime() {
		return  Integer.toString(persistTime);
	}

	/**
	 * @param persistTime the persistTime to set
	 */
	public void setPersistTime(String persistTime) {
		this.persistTime = Integer.parseInt(persistTime);
	}

	/**
	 * @return the antenna_sequence
	 */
	@Property(name="Antenna Sequence", description="Antennas to scan", writable=true)
	public String getAntenna_sequence() {
		return antenna_sequence;
	}

	/**
	 * @param antenna_sequence the antenna_sequence to set
	 */
	public void setAntenna_sequence(String antenna_sequence) {
		this.antenna_sequence = antenna_sequence;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.commands.AbstractCommandConfiguration#getCommand()
	 */
	@Override
	public AlienGetTagListCommand getCommand() {
		AlienGetTagListCommand c = new  AlienGetTagListCommand();
		c.setAntennasequence(antenna_sequence);
		c.setPersistTime(persistTime);
		c.setTagType(tagType);
		c.setPollInterval(interval);
		return c;
	}
}

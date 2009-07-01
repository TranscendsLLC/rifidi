/**
 * 
 */
package org.rifidi.edge.readerplugin.alien.commands;

import org.rifidi.configuration.annotations.JMXMBean;
import org.rifidi.configuration.annotations.Property;
import org.rifidi.configuration.annotations.PropertyType;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;

/**
 * A CommandConfiguration for creating commands that collect tags from an
 * Alien9800 Reader
 * 
 * @author Jochen Mader - jochen@pramari.com
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
	/** Type of tag to read */
	private int tagType = 2;
	/** Length of time tags stay in taglist */
	private int persistTime = -1;
	/** Antennas to scan */
	private String antenna_sequence = "0";

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.commands.AbstractCommandConfiguration#
	 * getCommandDescription ()
	 */
	@Override
	public String getCommandDescription() {
		return description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration#getCommandName
	 * ()
	 */
	@Override
	public String getCommandName() {
		return name;
	}

	/**
	 * @return the interval
	 */
	@Property(displayName = "Interval", description = "Interval between two reads", writable = true)
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
	@Property(displayName = "Tag Type", description = "0=Gen1, 1=Gen2, 2=Both", writable = true, type = PropertyType.PT_INTEGER, minValue = "0", maxValue = "2", defaultValue = "2")
	public Integer getTagType() {
		return tagType;
	}

	/**
	 * @param tagType
	 *            the tagType to set
	 */
	public void setTagType(Integer tagType) {
		this.tagType = tagType;
	}

	/**
	 * @return the persistTime
	 */
	@Property(displayName = "Persist Time", description = "Length of time a tag stays in memory", writable = true, type = PropertyType.PT_INTEGER)
	public Integer getPersistTime() {
		return persistTime;
	}

	/**
	 * @param persistTime
	 *            the persistTime to set
	 */
	public void setPersistTime(Integer persistTime) {
		this.persistTime = persistTime;
	}

	/**
	 * @return the antenna_sequence
	 */
	@Property(displayName = "Antenna Sequence", description = "Antennas to scan", writable = true)
	public String getAntenna_sequence() {
		return antenna_sequence;
	}

	/**
	 * @param antenna_sequence
	 *            the antenna_sequence to set
	 */
	public void setAntenna_sequence(String antenna_sequence) {
		this.antenna_sequence = antenna_sequence;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration#getCommand()
	 */
	@Override
	public AlienGetTagListCommand getCommand(String readerID) {
		AlienGetTagListCommand c = new AlienGetTagListCommand(super.getID(),
				readerID);
		c.setAntennasequence(antenna_sequence);
		c.setPersistTime(persistTime);
		c.setTagType(tagType);
		return c;
	}
}

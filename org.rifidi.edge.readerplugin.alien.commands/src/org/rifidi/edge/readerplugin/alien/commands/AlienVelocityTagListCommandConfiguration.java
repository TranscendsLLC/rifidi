/*
 *  AlienVelocityTagListCommandConfiguration.java
 *
 *  Created:	Dec 17, 2009
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.alien.commands;

import javax.management.MBeanInfo;

import org.rifidi.edge.core.configuration.annotations.JMXMBean;
import org.rifidi.edge.core.configuration.annotations.Property;
import org.rifidi.edge.core.configuration.annotations.PropertyType;
import org.rifidi.edge.core.configuration.mbeanstrategies.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;

/**
 * This is the configuration object for the Alien-Velocity-Poll command.
 * 
 * @author Matthew Dean
 */
@JMXMBean
public class AlienVelocityTagListCommandConfiguration extends
		AbstractCommandConfiguration<AlienVelocityTagListCommand> {

	/** Type of tag to read */
	private int tagType = 2;
	/** Antennas to scan */
	private String antenna_sequence = "0";
	/** Called by Spring */
	public static final MBeanInfo mbeaninfo;
	static {
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		mbeaninfo = strategy
				.getMBeanInfo(AlienGetTagListCommandConfiguration.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration#getCommand
	 * (java.lang.String)
	 */
	@Override
	public AlienVelocityTagListCommand getCommand(String readerID) {
		AlienVelocityTagListCommand c = new AlienVelocityTagListCommand(super
				.getID());
		c.setAntennasequence(antenna_sequence);
		c.setTagType(tagType);
		c.setReader(readerID);
		return c;
	}

	/**
	 * Gets the tag type.
	 * 
	 * @return the tagType
	 */
	@Property(displayName = "Tag Type", description = "0=Gen1, "
			+ "1=Gen2, 2=Both", writable = true, type = PropertyType.PT_INTEGER, minValue = ""
			+ "0", maxValue = "2", defaultValue = "2")
	public Integer getTagType() {
		return tagType;
	}

	/**
	 * Sets the tag type.
	 * 
	 * @param tagType
	 *            the tagType to set
	 */
	public void setTagType(Integer tagType) {
		this.tagType = tagType;
	}

	/**
	 * Gets the antenna sequence.
	 * 
	 * @return the antenna_sequence
	 */
	@Property(displayName = "Antenna Sequence", description = "Antennas to scan, separated by "
			+ "commas (ex \'0,2,1,0\')", writable = true, defaultValue = "0")
	public String getAntennaSequence() {
		return antenna_sequence;
	}

	/**
	 * Sets the antenna sequence.
	 * 
	 * @param antenna_sequence
	 *            the antenna_sequence to set
	 */
	public void setAntennaSequence(String antenna_sequence) {
		if (isValidAntennaSequence(antenna_sequence)) {
			this.antenna_sequence = antenna_sequence;
		}
	}

	/**
	 * Checks to see if the given antenna sequence is valid.
	 * 
	 * @return
	 */
	private boolean isValidAntennaSequence(String antennaSequence) {
		// System.out.println("Setting the antenna sequence!");
		try {
			String splitString[] = antennaSequence.split(",");
			for (String a : splitString) {
				Integer antenna = Integer.parseInt(a);
				if (antenna < 0 || antenna > 3) {
					return false;
				}
			}
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.configuration.RifidiService#getMBeanInfo()
	 */
	@Override
	public MBeanInfo getMBeanInfo() {
		return (MBeanInfo) mbeaninfo.clone();
	}

}

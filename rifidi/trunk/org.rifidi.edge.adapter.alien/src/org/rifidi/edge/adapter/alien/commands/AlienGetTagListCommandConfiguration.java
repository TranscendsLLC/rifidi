/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
package org.rifidi.edge.adapter.alien.commands;

import javax.management.MBeanInfo;

import org.rifidi.edge.configuration.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.configuration.JMXMBean;
import org.rifidi.edge.configuration.Property;
import org.rifidi.edge.configuration.PropertyType;
import org.rifidi.edge.sensors.AbstractCommandConfiguration;

/**
 * A CommandConfiguration for creating commands that collect tags from an
 * Alien9800 Reader
 * 
 * @author Jochen Mader - jochen@pramari.com
 */
@JMXMBean
public class AlienGetTagListCommandConfiguration extends
		AbstractCommandConfiguration<AlienGetTagListCommand> {
	/** Type of tag to read */
	private int tagType = 2;
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
	 * org.rifidi.edge.sensors.commands.AbstractCommandConfiguration#getCommand
	 * ()
	 */
	@Override
	public AlienGetTagListCommand getCommand(String readerID) {
		AlienGetTagListCommand c = new AlienGetTagListCommand(super.getID());
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.configuration.RifidiService#getMBeanInfo()
	 */
	@Override
	public MBeanInfo getMBeanInfo() {
		return (MBeanInfo) mbeaninfo.clone();
	}

}

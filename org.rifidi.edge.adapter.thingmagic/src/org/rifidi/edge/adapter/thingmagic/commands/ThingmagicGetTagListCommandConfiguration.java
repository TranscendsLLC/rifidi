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
package org.rifidi.edge.adapter.thingmagic.commands;

import javax.management.MBeanInfo;

import org.rifidi.edge.configuration.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.configuration.JMXMBean;
import org.rifidi.edge.configuration.Property;
import org.rifidi.edge.configuration.PropertyType;
import org.rifidi.edge.sensors.AbstractCommandConfiguration;

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
	private Integer timeout = 1000;

	private Integer epc0 = 0;
	private Integer epc1 = 0;
	private Integer gen2 = 1;
	private Integer iso860006b = 0;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.commands.AbstractCommandConfiguration#getCommand
	 * (java.lang.String)
	 */
	@Override
	public ThingmagicGetTagListCommand getCommand(String readerID) {
		ThingmagicGetTagListCommand c = new ThingmagicGetTagListCommand(super
				.getID());
		c.setReader(readerID);
		c.setEPC0(this.intToBoolean(epc0));
		c.setEPC1(this.intToBoolean(epc1));
		c.setGEN2(this.intToBoolean(gen2));
		c.setISO180006B(this.intToBoolean(iso860006b));
		return c;
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

	/**
	 * Gets the tag type.
	 * 
	 * @return the tagType
	 */
	@Property(displayName = "Timeout", description = "Sets the amount of time that must pass in milliseconds"
			+ " before the reader stops looking for tags.  If this value is too small, the reader will not "
			+ "read from all of its antennas.  Because of this, it is recommended that the value be at least 500ms.  "
			+ "", writable = true, type = PropertyType.PT_INTEGER, minValue = ""
			+ "500", maxValue = "16535", defaultValue = "1000", category="Timeout")
	public Integer getTimeout() {
		return timeout;
	}

	/**
	 * Sets the tag type.
	 * 
	 * @param tagType
	 *            the tagType to set
	 */
	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	/**
	 * @return the epc0
	 */
	@Property(displayName = "EPC0", description = "Sets whether the reader will look for Class 0 tags.  "
			+ "Setting the value to 0 means the reader will not look for class 0 tags, setting the reader"
			+ " to 1 will means the reader will look for class 0 tags.  If all the tag types are set to 0,"
			+ " the function is ignored and all tag types will be searched for."
			+ "", writable = true, type = PropertyType.PT_INTEGER, minValue = ""
			+ "0", maxValue = "1", defaultValue = "0", category="Protocols", orderValue=0)
	public Integer getEpc0() {
		return epc0;
	}

	/**
	 * @param epc0
	 *            the epc0 to set
	 */
	public void setEpc0(Integer epc0) {
		this.epc0 = epc0;
	}

	/**
	 * @return the epc1
	 */
	@Property(displayName = "EPC1", description = "Sets whether the reader will look for Class 1 tags.  "
			+ "Setting the value to 0 means the reader will not look for class 0 tags, setting the reader"
			+ " to 1 will means the reader will look for class 0 tags.  If all the tag types are set to 0,"
			+ " the function is ignored and all tag types will be searched for."
			+ "", writable = true, type = PropertyType.PT_INTEGER, minValue = ""
			+ "0", maxValue = "1", defaultValue = "0", category="Protocols", orderValue=1)
	public Integer getEpc1() {
		return epc1;
	}

	/**
	 * @param epc1
	 *            the epc1 to set
	 */
	public void setEpc1(Integer epc1) {
		this.epc1 = epc1;
	}

	/**
	 * @return the gen2
	 */
	@Property(displayName = "GEN2", description = "Sets whether the reader will look for Gen 2 tags.  "
			+ "Setting the value to 0 means the reader will not look for class 0 tags, setting the reader"
			+ " to 1 will means the reader will look for class 0 tags.  If all the tag types are set to 0,"
			+ " the function is ignored and all tag types will be searched for."
			+ "", writable = true, type = PropertyType.PT_INTEGER, minValue = ""
			+ "0", maxValue = "1", defaultValue = "1", category="Protocols", orderValue=2)
	public Integer getGen2() {
		return gen2;
	}

	/**
	 * @param gen2
	 *            the gen2 to set
	 */
	public void setGen2(Integer gen2) {
		this.gen2 = gen2;
	}

	/**
	 * @return the iso860006b
	 */
	@Property(displayName = "ISO860006B", description = "Sets whether the reader will look for ISO860006b tags.  "
			+ "Setting the value to 0 means the reader will not look for class 0 tags, setting the reader"
			+ " to 1 will means the reader will look for class 0 tags.  If all the tag types are set to 0,"
			+ " the function is ignored and all tag types will be searched for."
			+ "", writable = true, type = PropertyType.PT_INTEGER, minValue = ""
			+ "0", maxValue = "1", defaultValue = "0", category="Protocols", orderValue=3)
	public Integer getIso860006b() {
		return iso860006b;
	}

	/**
	 * @param iso860006b
	 *            the iso860006b to set
	 */
	public void setIso860006b(Integer iso860006b) {
		this.iso860006b = iso860006b;
	}

	/*
	 * Helper method that will convert an integer to a boolean.
	 */
	private boolean intToBoolean(int arg) {
		if (arg == 0) {
			return false;
		}
		return true;
	}

}

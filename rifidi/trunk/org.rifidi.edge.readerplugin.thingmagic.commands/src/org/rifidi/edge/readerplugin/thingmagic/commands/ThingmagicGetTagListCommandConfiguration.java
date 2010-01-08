/*
 *  ThingmagicGetTagListCommandConfiguration.java
 *
 *  Created:	Sep 30, 2009
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.thingmagic.commands;

import javax.management.MBeanInfo;

import org.rifidi.edge.core.configuration.annotations.JMXMBean;
import org.rifidi.edge.core.configuration.annotations.Property;
import org.rifidi.edge.core.configuration.annotations.PropertyType;
import org.rifidi.edge.core.configuration.mbeanstrategies.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;

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
	 * org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration#getCommand
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
	 * @see org.rifidi.edge.core.configuration.RifidiService#getMBeanInfo()
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
			+ "500", maxValue = "16535", defaultValue = "1000")
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
			+ "0", maxValue = "1", defaultValue = "0")
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
			+ "0", maxValue = "1", defaultValue = "0")
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
			+ "0", maxValue = "1", defaultValue = "1")
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
			+ "0", maxValue = "1", defaultValue = "0")
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

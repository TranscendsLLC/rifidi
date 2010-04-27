/*
 *  LLRPROSpecCommandConfiguration.java
 *
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
package org.rifidi.edge.readerplugin.llrp.commands;

import javax.management.MBeanInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.configuration.annotations.JMXMBean;
import org.rifidi.edge.core.configuration.annotations.Property;
import org.rifidi.edge.core.configuration.annotations.PropertyType;
import org.rifidi.edge.core.configuration.mbeanstrategies.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;

/**
 * The configuration class for the CommandConfiguration.
 * 
 * @author Matthew Dean
 */
@JMXMBean
public class LLRPROSpecCommandConfiguration extends
		AbstractCommandConfiguration<LLRPROSpecCommand> {
	/** The name of this command type */
	public static final String name = "LLRPROSpecCommand-Configuration";

	/**
	 * 
	 */
	private int roSpecID = 1;

	/**
	 * 
	 */
	private String antennaSequence = "0";

	private String triggerType = "PUSH";

	private int duration = 1000;

	private static final String category = "RO Spec";
	private static final Log logger = LogFactory
			.getLog(LLRPDeleteROSpecCommandConfiguration.class);
	public static final MBeanInfo mbeaninfo;
	static {
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		mbeaninfo = strategy
				.getMBeanInfo(LLRPGetTagListCommandConfiguration.class);
	}

	/**
	 * 
	 */
	public LLRPROSpecCommandConfiguration() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration#getCommand
	 * ()
	 */
	@Override
	public LLRPROSpecCommand getCommand(String readerID) {
		LLRPROSpecCommand llrprsc = new LLRPROSpecCommand(super.getID());
		llrprsc.setRoSpecID(roSpecID);
		llrprsc.setAntennaIDs(antennaSequence);
		llrprsc.setRoSpecTrigger(triggerType);
		llrprsc.setRoSpecDuration(this.duration);
		return llrprsc;
	}

	/**
	 * Gets the AntennaID.
	 * 
	 * @return
	 */
	@Property(displayName = "AntennaIDs", description = "Select which"
			+ " antennas to scan.  Use a comma delimited string such"
			+ " as \"1,2,3\"", writable = true, orderValue = 2)
	public String getAntennaIDs() {
		return antennaSequence;
	}

	/**
	 * Sets the AntennaID.
	 */
	public void setAntennaIDs(String antennaIDs) {
		if (isAListOfShorts(antennaIDs)) {
			this.antennaSequence = antennaIDs;
		}
	}

	/*
	 * Checks to see if the string that has come in is a comma delimited list of
	 * integers
	 */
	private boolean isAListOfShorts(String list) {
		String[] strArray = list.split(",");

		boolean retVal = true;

		for (String number : strArray) {
			if (!isANaturalShort(number)) {
				retVal = false;
			}
		}

		return retVal;
	}

	/*
	 * Checks to see if an incoming string is a list of integers.
	 */
	private boolean isANaturalShort(String i) {
		try {
			Short number = Short.valueOf(i);
			if (number < 0) {
				return false;
			}
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	/**
	 * Sets the ROSpecID.
	 * 
	 * @param roSpecID
	 *            the roSpecID to set
	 */
	public void setROSpecID(Integer roSpecID) {
		this.roSpecID = roSpecID;
	}

	/**
	 * Gets teh ROSpecID.
	 * 
	 * @return the roSpecID
	 */
	@Property(displayName = "RO Spec ID", description = "The ID of the "
			+ "ROSpec", writable = true, type = PropertyType.PT_INTEGER, minValue = ""
			+ "1", maxValue = "16535", category = category, orderValue = 1)
	public Integer getROSpecID() {
		return roSpecID;
	}

	/**
	 * @return
	 */
	@Property(displayName = "Read Mode", description = "If read mode is set to POLL, "
			+ "you will need to use the LLRP Poll command to receieve tags. If it is set "
			+ "to PUSH, the reader will send back tags according to the Push Duration"
			+ "", defaultValue = "PUSH", writable = true, type = PropertyType.PT_STRING, category = category, orderValue = 3)
	public String getTriggerType() {
		return triggerType;
	}

	/**
	 * @param triggerType
	 */
	public void setTriggerType(String triggerType) {
		if (triggerType.equals("PUSH")) {
			this.triggerType = "PUSH";
		} else if (triggerType.equals("POLL")) {
			this.triggerType = "POLL";
		} else {
			logger.warn("Read Mode not valid: " + triggerType
					+ " Must be either PUSH or POLL");
		}
	}

	/**
	 * @return
	 */
	@Property(displayName = "Push Duration", description = "Rate that tags are sent back in push mode. "
			+ "Will only be used if you set the Read Mode to " + "\'PUSH\'", writable = true, type = PropertyType.PT_INTEGER, defaultValue = ""
			+ "1000", minValue = "0", maxValue = "65535", category = category, orderValue = 4)
	public Integer getDuration() {
		return this.duration;
	}

	/**
	 * @param duration
	 */
	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	/**
	 * A class that links a trigger type string to a resulting integer value.
	 * 
	 * @author Matthew Dean
	 */
	public class TriggerPair {
		public String getString() {
			return a;
		}

		public void setString(String a) {
			this.a = a;
		}

		public int getInt() {
			return b;
		}

		public void setInt(int b) {
			this.b = b;
		}

		private String a = "";
		private int b = -1;

		public TriggerPair(String a, int b) {
			this.a = a;
			this.b = b;
		}
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

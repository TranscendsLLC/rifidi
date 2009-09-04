/*
 *  LLRPROSpecCommandConfiguration.java
 *
 *  Created:	Mar 9, 2009
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.llrp.commands;

import javax.management.MBeanInfo;

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
	
	public final TriggerPair NULL_TRIGGER_TYPE = new TriggerPair("NULL", 0);
	public final TriggerPair PERIODIC_TRIGGER_TYPE = new TriggerPair("PER", 2);

	/**
	 * 
	 */
	private int roSpecID = 1;

	/**
	 * 
	 */
	private String antennaSequence = "0";

	private String triggerType = "NULL";
	
	private int triggerInt = 0;

	private int duration = 1000;
	
	private static final String category = "RO Spec";
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
	 * org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration#getCommand()
	 */
	@Override
	public LLRPROSpecCommand getCommand(String readerID) {
		LLRPROSpecCommand llrprsc = new LLRPROSpecCommand(super.getID());
		llrprsc.setRoSpecID(roSpecID);
		llrprsc.setAntennaIDs(antennaSequence);
		llrprsc.setRoSpecTrigger(triggerInt);
		llrprsc.setRoSpecDuration(this.duration);
		return llrprsc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.commands.AbstractCommandConfiguration#
	 * getCommandDescription()
	 */
	@Override
	public String getCommandDescription() {
		return "LLRP RoSpec Command";
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
	 * Gets the AntennaID.
	 * 
	 * @return
	 */
	@Property(displayName = "AntennaIDs", description = "Select which"
			+ " antennas to scan.  Use a comma delimited string such"
			+ " as \"1,2,3\".", writable = true)
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
	@Property(displayName = "ROSpec ID", description = "The ID of the "
			+ "ROSpec", writable = true, type = PropertyType.PT_INTEGER, minValue = ""
			+ "1", maxValue = "16535", category=category, orderValue=1)
	public Integer getROSpecID() {
		return roSpecID;
	}

	/**
	 * @return
	 */
	@Property(displayName = "Trigger Type", description = "The type of ROSpec it is, "
		+ "with \'NULL\' being a null trigger and \'PER\' being a periodic trigger.  "
		+ "Periodic triggers will send back tags using the duration value you set. "
		+ " ", defaultValue="NULL", writable = true, type = PropertyType.PT_STRING, category=category, orderValue=2)
	public String getTriggerType() {
		return triggerType;
	}

	/**
	 * @param triggerType
	 */
	public void setTriggerType(String triggerType) {
		if (triggerType.equalsIgnoreCase(this.PERIODIC_TRIGGER_TYPE
				.getString())) {
			this.triggerType = this.PERIODIC_TRIGGER_TYPE.getString();
			triggerInt = this.PERIODIC_TRIGGER_TYPE.getInt();
		} else {
			this.triggerType = this.NULL_TRIGGER_TYPE.getString();
			this.triggerInt = this.NULL_TRIGGER_TYPE.getInt();
		}
	}

	/**
	 * @return
	 */
	@Property(displayName = "Duration", description = "The duration of the ROSpec trigger.  "
			+ "Will only be used if you set the ROSpec trigger type to "
			+ "\'Periodic\'.", writable = true, type = PropertyType.PT_INTEGER, defaultValue = ""
			+ "1000", minValue = "0", maxValue = "65535", category=category, orderValue=3)
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

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.configuration.RifidiService#getMBeanInfo()
	 */
	@Override
	public MBeanInfo getMBeanInfo() {
		return (MBeanInfo) mbeaninfo.clone();
	}
}

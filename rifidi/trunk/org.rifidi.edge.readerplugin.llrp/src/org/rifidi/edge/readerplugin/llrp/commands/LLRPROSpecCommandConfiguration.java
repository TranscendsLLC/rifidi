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

import org.rifidi.configuration.annotations.JMXMBean;
import org.rifidi.configuration.annotations.Property;
import org.rifidi.configuration.annotations.PropertyType;
import org.rifidi.edge.core.commands.AbstractCommandConfiguration;

/**
 * The configuration class for the CommandConfiguration.  
 * 
 * @author Matthew Dean
 */
@JMXMBean
public class LLRPROSpecCommandConfiguration extends
		AbstractCommandConfiguration<LLRPROSpecCommand> {

	/**
	 * 
	 */
	private int roSpecID = 0;

	/**
	 * 
	 */
	private String antennaSequence = "0";

	/**
	 * 
	 */
	public LLRPROSpecCommandConfiguration() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.commands.AbstractCommandConfiguration#getCommand()
	 */
	@Override
	public LLRPROSpecCommand getCommand(String readerID) {
		LLRPROSpecCommand llrprsc = new LLRPROSpecCommand(super.getID());
		llrprsc.setRoSpecID(roSpecID);
		llrprsc.setAntennaIDs(antennaSequence);
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
	 * org.rifidi.edge.core.commands.AbstractCommandConfiguration#getCommandName
	 * ()
	 */
	@Override
	public String getCommandName() {
		return "LLRPROSpecCommand";
	}

	/**
	 * Gets the AntennaID.  
	 * 
	 * @return
	 */
	@Property(displayName = "AntennaIDs", description = "Select which"
			+ " antennas to scan.  Use a comma delimited string such as \"1,2,3\".", writable = true)
	public String getAntennaIDs() {
		return antennaSequence;
	}

	/**
	 * Sets the AntennaID.  
	 */
	public void setAntennaIDs(String antennaIDs) {
		if(isAListOfShorts(antennaIDs)) {
			this.antennaSequence = antennaIDs;
		}
	}

	/*
	 * Checks to see if the string that has come in is a comma delimited list of integers
	 */
	private boolean isAListOfShorts(String list) {
		String[] strArray = list.split(",");
		
		boolean retVal = true;
		
		for(String number:strArray) {
			if(!isANaturalShort(number)) {
				retVal=false;
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
			if(number<0) {
				return false;
			}
		}catch (NumberFormatException e) {
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
	@Property(displayName = "ROSpecID", description = "The ID of the ROSpec", writable = true, type = PropertyType.PT_INTEGER)
	public int getROSpecID() {
		return roSpecID;
	}

}

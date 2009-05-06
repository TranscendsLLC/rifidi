/*
 *  LLRPROSpecCommandConfiguration.java
 *
 *  Created:	Mar 24, 2009
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.readerplugin.llrp.commands;
//TODO: Comments
import java.util.ArrayList;

import org.rifidi.configuration.annotations.JMXMBean;
import org.rifidi.configuration.annotations.Property;
import org.rifidi.configuration.annotations.PropertyType;
import org.rifidi.edge.core.commands.AbstractCommandConfiguration;

/**
 * 
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
	
	private ArrayList<Integer> antennaList = new ArrayList<Integer>();

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
	 * 
	 * @return
	 */
	@Property(displayName = "AntennaIDs", description = "Select which"
			+ " antennas to scan.  Use a comma delimited string such as \"1,2,3\".", writable = true)
	public String getAntennaIDs() {
		return antennaSequence;
	}

	/**
	 * 
	 */
	public void setAntennaIDs(String antennaIDs) {
		this.antennaList = new ArrayList<Integer>();
		if(isAListOfIntegers(antennaIDs)) {
			this.antennaSequence = antennaIDs;
			
			
		}
	}

	/*
	 * Checks to see if the string that has come in is a comma delimited list of integers
	 */
	private boolean isAListOfIntegers(String list) {
		String[] strArray = list.split(",");
		
		boolean retVal = true;
		
		for(String number:strArray) {
			if(!isAnInt(number)) {
				retVal=false;
			}
		}
		
		return retVal;
	}
	
	/*
	 * Checks to see if an incoming string is a list of integers.  
	 */
	private boolean isAnInt(String i) {
		try {
			Integer number = Integer.valueOf(1);
			if(number<0) {
				return false;
			}
		}catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * @param roSpecID
	 *            the roSpecID to set
	 */
	public void setROSpecID(Integer roSpecID) {
		this.roSpecID = roSpecID;
	}

	/**
	 * @return the roSpecID
	 */
	@Property(displayName = "ROSpecID", description = "The ID of the ROSpec", writable = true, type = PropertyType.PT_INTEGER)
	public int getROSpecID() {
		return roSpecID;
	}

}

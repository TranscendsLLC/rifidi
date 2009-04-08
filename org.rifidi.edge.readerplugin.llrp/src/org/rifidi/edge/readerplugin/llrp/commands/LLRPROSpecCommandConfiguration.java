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
	
	private int roSpecID = 0;
	
	/**
	 * 
	 */
	public LLRPROSpecCommandConfiguration() {
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.commands.AbstractCommandConfiguration#getCommand()
	 */
	@Override
	public LLRPROSpecCommand getCommand() {
		LLRPROSpecCommand llrprsc = new LLRPROSpecCommand(super.getID());
		llrprsc.setRoSpecID(roSpecID);
		return llrprsc;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.commands.AbstractCommandConfiguration#getCommandDescription()
	 */
	@Override
	public String getCommandDescription() {
		return "LLRP RoSpec Command";
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.commands.AbstractCommandConfiguration#getCommandName()
	 */
	@Override
	public String getCommandName() {
		return "LLRPROSpecCommand";
	}

	/**
	 * @param roSpecID the roSpecID to set
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

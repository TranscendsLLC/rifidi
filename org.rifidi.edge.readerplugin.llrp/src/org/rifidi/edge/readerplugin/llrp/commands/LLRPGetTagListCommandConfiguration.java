/*
 *  LLRPGetTagListCommandConfiguration.java
 *
 *  Created:	Mar 9, 2009
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
public class LLRPGetTagListCommandConfiguration extends
		AbstractCommandConfiguration<LLRPGetTagListCommand> {
	private int roSpecID = 0;

	/**
	 * 
	 */
	public LLRPGetTagListCommandConfiguration() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.commands.AbstractCommandConfiguration#getCommand()
	 */
	@Override
	public LLRPGetTagListCommand getCommand(String readerID) {
		LLRPGetTagListCommand llrpgtlc = new LLRPGetTagListCommand(super
				.getID());
		return llrpgtlc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.commands.AbstractCommandConfiguration#
	 * getCommandDescription()
	 */
	@Override
	public String getCommandDescription() {
		return "LLRP get tag description";
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
		return "LLRPGetTagList";
	}

	/**
	 * @param roSpecID
	 *            the roSpecID to set
	 */
	public void setROSpecID(Integer roSpecID) {
		System.out.println("Called the setROSpecID in LLRPGetTagList");
		this.roSpecID = roSpecID;
	}

	/**
	 * @return the roSpecID
	 */
	@Property(displayName = "roSpecID", description = "The ID of the ROSpec for the GetTagList command", writable = true, type = PropertyType.PT_INTEGER)
	public int getROSpecID() {
		return roSpecID;
	}

}

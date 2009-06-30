/*
 *  LLRPGetTagListCommandConfiguration.java
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
 * This class configures a GetTagList command.  
 * 
 * @author Matthew Dean
 */
@JMXMBean
public class LLRPGetTagListCommandConfiguration extends
		AbstractCommandConfiguration<LLRPGetTagListCommand> {
	private int roSpecID = 0;

	/**
	 * Constructor of LLRPGetTagListCommandConfiguration.  
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
		return "Returns the taglist as seen by the LLRP via a polling.";
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
	 * Sets the ROSpecID.  
	 * 
	 * @param roSpecID
	 *            the roSpecID to set
	 */
	public void setROSpecID(Integer roSpecID) {
		this.roSpecID = roSpecID;
	}

	/**
	 * Gets the ROSpecID.  
	 * 
	 * @return the roSpecID
	 */
	@Property(displayName = "roSpecID", description = "The ID of the ROSpec for the GetTagList command", writable = true, type = PropertyType.PT_INTEGER)
	public int getROSpecID() {
		return roSpecID;
	}

}

/*
 *  LLRPDeleteROSpecCommandConfiguration.java
 *
 *  Created:	Jun 18, 2009
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
 * 
 * 
 * @author Matthew Dean
 */
@JMXMBean
public class LLRPDeleteROSpecCommandConfiguration extends
		AbstractCommandConfiguration<LLRPDeleteROSpecCommand> {

	private int roSpecID = 0;
	
	public LLRPDeleteROSpecCommandConfiguration() {
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.commands.AbstractCommandConfiguration#getCommand(java.lang.String)
	 */
	@Override
	public LLRPDeleteROSpecCommand getCommand(String readerID) {
		LLRPDeleteROSpecCommand llrpdrc = new LLRPDeleteROSpecCommand(super
				.getID());
		llrpdrc.setROSPecID(roSpecID);
		return llrpdrc;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.commands.AbstractCommandConfiguration#getCommandDescription()
	 */
	@Override
	public String getCommandDescription() {
		return "Delete the ROSpec with the given ID.";
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.commands.AbstractCommandConfiguration#getCommandName()
	 */
	@Override
	public String getCommandName() {
		return "DeleteROSpec";
	}

	
	/**
	 * Sets the ROSpecID.  
	 * 
	 * @param roSpecID
	 *            the roSpecID to set
	 */
	public void setROSpecID(Integer roSpecID) {
		System.out.println("Called the setROSpecID in LLRPGetTagList");
		this.roSpecID = roSpecID;
	}

	/**
	 * Gets the ROSpecID.  
	 * 
	 * @return the roSpecID
	 */
	@Property(displayName = "roSpecID", description = "The ID of the ROSpec for the DeleteROSpec command", writable = true, type = PropertyType.PT_INTEGER)
	public int getROSpecID() {
		return roSpecID;
	}
}

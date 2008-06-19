/*
 *  IReaderPlugin.java
 *
 *  Created:	Jun 19, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.core.readerPlugin;

import java.util.List;

import org.rifidi.edge.core.exception.RifidiIIllegialArgumentException;
import org.rifidi.edge.core.exception.readerConnection.RifidiConnectionIllegalStateException;
import org.rifidi.edge.core.exception.readerConnection.RifidiConnectionException;
import org.rifidi.edge.core.readerPlugin.commands.ICustomCommand;
import org.rifidi.edge.core.readerPlugin.commands.ICustomCommandResult;
import org.rifidi.edge.core.tag.TagRead;

public interface IReaderPlugin {

	/**
	 * Connects to a reader
	 * @throws RifidiConnectionException
	 */
	public void connect() throws RifidiConnectionException;

	/**
	 * Disconnects from a reader
	 * @throws RifidiConnectionException
	 */
	public void disconnect() throws RifidiConnectionException;

	/**
	 * Sends a custom command
	 * @param customCommand The custom command to send.
	 * @return The result of the custom command
	 * @throws RifidiConnectionIllegalStateException
	 * @throws RifidiIIllegialArgumentException
	 */
	public ICustomCommandResult sendCustomCommand(ICustomCommand customCommand)
		throws RifidiConnectionIllegalStateException, RifidiIIllegialArgumentException;

	/**
	 * Gets the list of tags seen by this reader
	 * @return The list of tags
	 * @throws RifidiConnectionIllegalStateException
	 */
	public List<TagRead> getNextTags() throws RifidiConnectionIllegalStateException;
	
	/**
	 * Is this reader blocking or not?
	 * @return True if blocking
	 */
	public boolean isBlocking();
	
}

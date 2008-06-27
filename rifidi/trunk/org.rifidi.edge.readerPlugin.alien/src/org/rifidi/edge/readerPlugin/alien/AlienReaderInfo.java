/*
 *  AlienReaderInfo.java
 *
 *  Created:	Jun 19, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.readerPlugin.alien;

import javax.xml.bind.annotation.XmlRootElement;

import org.rifidi.edge.core.communication.enums.CommunicationType;
import org.rifidi.edge.core.communication.protocol.Protocol;
import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;

/**
 * This class represents information that the alien will need to connect and
 * authenticate.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
@XmlRootElement
public class AlienReaderInfo extends AbstractReaderInfo {

	/**
	 * Generates serial UID.
	 */
	private static final long serialVersionUID = 1794239884061191033L;

	/**
	 * The username for this reader.
	 */
	private String username;

	/**
	 * The password for this reader.
	 */
	private String password;

	/**
	 * Gets the username for this reader.
	 * 
	 * @return The username for this reader
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the username for this reader.
	 * 
	 * @param username
	 *            The username for this reader
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Gets the password for this reader.
	 * 
	 * @return The password for this reader
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password for this reader.
	 * 
	 * @param password
	 *            The password for this reader.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerPlugin.AbstractReaderInfo#getReaderType()
	 */
	@Override
	public String getReaderType() {
		return "Alienreader";
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerPlugin.AbstractReaderInfo#getCommunicationType()
	 */
	@Override
	public CommunicationType getCommunicationType() {
		return CommunicationType.SYNCHRONOUS;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerPlugin.AbstractReaderInfo#getProtocol()
	 */
	@Override
	public Protocol getProtocol() {
		return new AlienProtocol();
	}

}

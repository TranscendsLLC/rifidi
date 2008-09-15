/* 
 * AlienReaderInfo.java
 *  Created:	Jul 7, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.readerplugin.alien;

import javax.xml.bind.annotation.XmlRootElement;

import org.rifidi.dynamicswtforms.xml.annotaions.Form;
import org.rifidi.dynamicswtforms.xml.annotaions.FormElement;
import org.rifidi.dynamicswtforms.xml.constants.FormElementType;
import org.rifidi.edge.core.readerplugin.ReaderInfo;

/**
 * This class represents the reader info needed for an Alien reader. In addition
 * to the things already in ReaderInfo, this class also contains a username and
 * password field used for connnecting to a reader.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
@XmlRootElement(name = "AlienReaderInfo")
@Form(name = "AlienReaderInfo", formElements = {
		@FormElement(type = FormElementType.STRING, elementName = "ipAddress", displayName = "IP Address", defaultValue = "127.0.0.1", regex="(.)+"),
		@FormElement(type = FormElementType.INTEGER, elementName = "port", displayName = "Port", defaultValue = "23", min = 0, max = 65535),
		@FormElement(type = FormElementType.STRING, elementName = "username", displayName = "User Name", defaultValue = "alien", regex="(.)+"),
		@FormElement(type = FormElementType.STRING, elementName = "password", displayName = "Password", defaultValue = "password", regex="(.)+"),
		@FormElement(type = FormElementType.INTEGER, elementName = "reconnectionInterval", displayName = "Reconnect Interval", defaultValue = "1000", min = 0, max = Integer.MAX_VALUE),
		@FormElement(type = FormElementType.INTEGER, elementName = "maxNumConnectionsAttempts", displayName = "Connection Attempts", defaultValue = "3", min = -1, max = Integer.MAX_VALUE) })
public class AlienReaderInfo extends ReaderInfo {

	/**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 9204173142014313800L;

	/**
	 * The username for the reader
	 */
	private String username;

	/**
	 * The password for the reader.
	 */
	private String password;

	/**
	 * Gets the username.
	 * 
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the username.
	 * 
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Gets the password
	 * 
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password.
	 * 
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

}

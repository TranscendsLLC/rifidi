/**
 * 
 */
package org.rifidi.edge.readerPlugin.alien;

import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;

/**
 * This class represents information that the alien will need to connect and
 * authenticate.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
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

}

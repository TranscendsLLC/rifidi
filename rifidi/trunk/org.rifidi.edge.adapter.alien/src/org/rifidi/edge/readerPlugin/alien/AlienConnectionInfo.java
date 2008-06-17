/**
 * 
 */
package org.rifidi.edge.readerPlugin.alien;

import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;

/**
 * @author Matthew Dean - matt@pramari.com
 * 
 */
public class AlienConnectionInfo extends AbstractReaderInfo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1794239884061191033L;

	private String username;

	private String password;

	@Override
	public Class<? extends AbstractReaderInfo> getReaderAdapterType() {
		return this.getClass();
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}

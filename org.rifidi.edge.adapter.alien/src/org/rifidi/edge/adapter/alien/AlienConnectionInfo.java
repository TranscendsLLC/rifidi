/**
 * 
 */
package org.rifidi.edge.adapter.alien;

import org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo;

/**
 * @author Matthew Dean - matt@pramari.com
 * 
 */
public class AlienConnectionInfo extends AbstractConnectionInfo {

	private String username;

	private String password;

	@Override
	public Class<? extends AbstractConnectionInfo> getReaderAdapterType() {
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

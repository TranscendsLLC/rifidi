package org.rifidi.edge.readerplugin.thingmagic.plugin;

import javax.xml.bind.annotation.XmlRootElement;

import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.commands.StringMetadata;

/**
 * @author Jerry Maine - jerry@pramari.com
 *
 */
@XmlRootElement(name="ThingMagicReaderInfo")
public class ThingMagicReaderInfo extends ReaderInfo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7316642594419241797L;

	
	@StringMetadata(defaultValue="password", displayName="Password", editable=true, name="password", regex="")
	String password;
	@StringMetadata(defaultValue="user", displayName="Username", editable=true, name="user", regex="")
	String user;
	boolean ssh;
	
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

}

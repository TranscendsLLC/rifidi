package org.rifidi.edge.readerplugin.thingmagic.plugin;

import javax.xml.bind.annotation.XmlRootElement;

import org.rifidi.dynamicswtforms.xml.annotaions.Form;
import org.rifidi.dynamicswtforms.xml.annotaions.FormElement;
import org.rifidi.dynamicswtforms.xml.constants.FormElementType;
import org.rifidi.edge.core.readerplugin.ReaderInfo;

/**
 * @author Jerry Maine - jerry@pramari.com
 *
 */
@XmlRootElement(name="ThingMagicReaderInfo")
@Form(name = "ThingMagicReaderInfo", formElements = {
		@FormElement(type = FormElementType.STRING, elementName = "ipAddress", displayName = "IP Address", defaultValue = "127.0.0.1"),
		@FormElement(type = FormElementType.INTEGER, elementName = "port", displayName = "Port", defaultValue = "23", min = 0, max = 65535),
		@FormElement(type = FormElementType.STRING, elementName = "user", displayName = "User Name", defaultValue = "user"),
		@FormElement(type = FormElementType.STRING, elementName = "password", displayName = "Password", defaultValue = "password"),
		@FormElement(type = FormElementType.INTEGER, elementName = "reconnectionInterval", displayName = "Reconnect Interval", defaultValue = "1000", min = 0, max = Integer.MAX_VALUE),
		@FormElement(type = FormElementType.INTEGER, elementName = "maxNumConnectionsAttempts", displayName = "Connection Attempts", defaultValue = "3", min = -1, max = Integer.MAX_VALUE) })
public class ThingMagicReaderInfo extends ReaderInfo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7316642594419241797L;

	String password;
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

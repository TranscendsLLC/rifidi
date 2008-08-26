package org.rifidi.edge.readerplugin.thingmagic.plugin;

import javax.xml.bind.annotation.XmlRootElement;

import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.annotations.Widget;
import org.rifidi.edge.core.readerplugin.annotations.WidgetType;
import org.rifidi.edge.core.readerplugin.annotations.Widgets;

/**
 * @author Jerry Maine - jerry@pramari.com
 *
 */
@XmlRootElement(name="ThingMagicReaderInfo")
@Widgets(name = "ThingMagicReaderInfo", widgets = {
		@Widget(type = WidgetType.STRING, elementName = "ipAddress", displayName = "IP Address", defaultValue = "localhost"),
		@Widget(type = WidgetType.INTEGER, elementName = "port", displayName = "Port", defaultValue = "23", min = 0, max = 65535),
		@Widget(type = WidgetType.STRING, elementName = "user", displayName = "User Name", defaultValue = "user"),
		@Widget(type = WidgetType.STRING, elementName = "password", displayName = "Password", defaultValue = "password"),
		@Widget(type = WidgetType.LONG, elementName = "reconnectionInterval", displayName = "Reconnect Interval", defaultValue = "1000", min = 0, max = Long.MAX_VALUE),
		@Widget(type = WidgetType.INTEGER, elementName = "maxNumConnectionsAttempts", displayName = "Connection Attempts", defaultValue = "3", min = -1, max = Integer.MAX_VALUE) })
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

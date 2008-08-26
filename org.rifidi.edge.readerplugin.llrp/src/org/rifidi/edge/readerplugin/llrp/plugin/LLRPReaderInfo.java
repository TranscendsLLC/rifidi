/* 
 * LLRPReaderInfo.java
 *  Created:	Jul 7, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.readerplugin.llrp.plugin;

import javax.xml.bind.annotation.XmlRootElement;

import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.annotations.Widget;
import org.rifidi.edge.core.readerplugin.annotations.WidgetType;
import org.rifidi.edge.core.readerplugin.annotations.Widgets;

/**
 * 
 * 
 * @author Matthew Dean - matt@pramari.com
 */
@XmlRootElement(name="LLRPReaderInfo")
@Widgets(name = "LLRPReaderInfo", widgets = {
		@Widget(type = WidgetType.STRING, elementName = "ipAddress", displayName = "IP Address", defaultValue = "localhost"),
		@Widget(type = WidgetType.INTEGER, elementName = "port", displayName = "Port", defaultValue = "5084", min = 0, max = 65535),
		@Widget(type = WidgetType.LONG, elementName = "reconnectionInterval", displayName = "Reconnect Interval", defaultValue = "1000", min = 0, max = Long.MAX_VALUE),
		@Widget(type = WidgetType.INTEGER, elementName = "maxNumConnectionsAttempts", displayName = "Connection Attempts", defaultValue = "3", min = -1, max = Integer.MAX_VALUE) })
public class LLRPReaderInfo extends ReaderInfo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5322227896857756210L;
	
}

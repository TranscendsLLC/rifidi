package org.rifidi.edge.readerplugin.dummy.plugin;

import javax.xml.bind.annotation.XmlRootElement;

import org.rifidi.dynamicswtforms.xml.annotaions.Form;
import org.rifidi.dynamicswtforms.xml.annotaions.FormElement;
import org.rifidi.dynamicswtforms.xml.constants.FormElementType;
import org.rifidi.edge.core.api.readerplugin.ReaderInfo;

@XmlRootElement(name = "DummyReaderInfo")
@Form(name = "DummyReaderInfo", formElements = {
		@FormElement(type = FormElementType.STRING, elementName = "ipAddress", displayName = "IP Address", defaultValue = "localhost", regex = "(.)*"),
		@FormElement(type = FormElementType.INTEGER, elementName = "port", displayName = "Port", defaultValue = "12345", min = 0, max = 65535),
		@FormElement(type = FormElementType.INTEGER, elementName = "reconnectionInterval", displayName = "Reconnect Interval", defaultValue = "1000", min = 0, max = Integer.MAX_VALUE),
		@FormElement(type = FormElementType.INTEGER, elementName = "maxNumConnectionsAttempts", displayName = "Connection Attempts", defaultValue = "3", min = -1, max = Integer.MAX_VALUE) })
public class DummyReaderInfo extends ReaderInfo {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5486038928636364398L;
}

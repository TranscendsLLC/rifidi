package org.rifidi.edge.core.readerplugin.protocol;

/**
 * Reserved for later use.
 * 
 * @author Andreas Huebner - andreas@pramari.com
 *
 */
public interface MessageProtocol {
	/**
	 * Convert content of message to XML
	 * 
	 * @param message the message to convert
	 * @return the xml representing the message
	 */
	public String toXML(Object message);
}

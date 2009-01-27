package org.rifidi.edge.core.api.readerplugin.messages;

/**
 * The Message Interface is used the common base for all custom messages.
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 * @see TagMessage
 * 
 */
public interface Message {

	/**
	 * Transform the content of the message to xml
	 * 
	 * @return
	 */
	public String toXML();

}

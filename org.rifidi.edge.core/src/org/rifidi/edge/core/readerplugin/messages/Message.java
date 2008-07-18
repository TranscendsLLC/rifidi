package org.rifidi.edge.core.readerplugin.messages;

import org.rifidi.edge.core.readerplugin.messages.impl.TagMessage;

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

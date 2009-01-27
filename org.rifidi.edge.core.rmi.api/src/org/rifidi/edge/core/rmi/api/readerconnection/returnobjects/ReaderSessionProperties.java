/*
 *  ReaderSessionProperties.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.core.rmi.api.readerconnection.returnobjects;

import java.io.Serializable;

/**
 * 
 * This is a value object that contains properties of a reader session for the
 * purpose of making RMI more efficient. The values in this object should not
 * change over the lifetime of the object.
 * 
 * Keep in mind that this object is transmitted over RMI, so it should not
 * contain references to "large" objects with other references. It would be best
 * to keep references in this object limited to Strings and other basic objects
 * 
 * @author kyle
 * 
 */
public class ReaderSessionProperties implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String readerInfoClassName;

	private String messageQueueName;

	private String errorQueueName;

	public String getReaderInfoClassName() {
		return readerInfoClassName;
	}

	public void setReaderInfoClassName(String readerInfoClassName) {
		this.readerInfoClassName = readerInfoClassName;
	}

	public String getMessageQueueName() {
		return messageQueueName;
	}

	public void setMessageQueueName(String messageQueueName) {
		this.messageQueueName = messageQueueName;
	}

	public String getErrorQueueName() {
		return errorQueueName;
	}

	public void setErrorQueueName(String errorQueueName) {
		this.errorQueueName = errorQueueName;
	}

}

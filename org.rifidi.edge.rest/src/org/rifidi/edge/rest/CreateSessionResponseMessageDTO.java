/**
 * 
 */
package org.rifidi.edge.rest;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author matt
 *
 */
public class CreateSessionResponseMessageDTO implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2195666327355438212L;
	
	
	@XmlElement(name = "sessionID")
	private Integer sessionID;

	public Integer getSessionID() {
		return sessionID;
	}

	public void setSessionID(Integer sessionID) {
		this.sessionID = sessionID;
	}
}

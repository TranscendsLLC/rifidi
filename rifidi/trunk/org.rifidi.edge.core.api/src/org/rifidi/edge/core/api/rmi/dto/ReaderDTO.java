/**
 * 
 */
package org.rifidi.edge.core.api.rmi.dto;

import java.util.List;

import javax.management.AttributeList;

/**
 * @author kyle
 * 
 */
public class ReaderDTO {

	/** The ID of the reader */
	private String readerID;
	/** The factory type of this reader */
	private String readerFactoryID;
	/** The list of attributes of the reader */
	private AttributeList attributes;
	/** The list of sessions in the reader */
	private List<SessionDTO> sessions;

	/**
	 * @param readerID
	 *            The ID of the reader
	 * @param readerFactoryID
	 *            The ID of the factory that created this Reader
	 * @param attributes
	 *            The list of properties currently set on the reader
	 * @param sessions
	 *            The Sessions currently created on the reader
	 */
	public ReaderDTO(String readerID, String readerFactoryID,
			AttributeList attributes, List<SessionDTO> sessions) {
		this.readerID = readerID;
		this.readerFactoryID = readerFactoryID;
		this.attributes = attributes;
		this.sessions = sessions;
	}

	/**
	 * @return the readerID
	 */
	public String getReaderID() {
		return readerID;
	}

	/**
	 * @return the readerFactoryID
	 */
	public String getReaderFactoryID() {
		return readerFactoryID;
	}

	/**
	 * @return the attributes
	 */
	public AttributeList getAttributes() {
		return attributes;
	}

	/**
	 * @return the sessions
	 */
	public List<SessionDTO> getSessions() {
		return sessions;
	}

}

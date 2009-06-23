package org.rifidi.edge.api.rmi.dto;

import java.io.Serializable;
import java.util.List;

import javax.management.AttributeList;

/**
 * Data Transfer Object for Readers. For serializing information about a reader
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class ReaderDTO implements Serializable {

	/** SerialVersionID */
	private static final long serialVersionUID = 1L;
	/** The ID of the reader */
	private String readerID;
	/** The factory type of this reader */
	private String readerFactoryID;
	/** The list of attributes of the reader */
	private AttributeList attributes;
	/** The list of sessions in the reader */
	private List<SessionDTO> sessions;

	/**
	 * Constructor.
	 * 
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
	 * Returns the reader ID.
	 * 
	 * @return the readerID
	 */
	public String getReaderID() {
		return readerID;
	}

	/**
	 * Returns the factory ID.
	 * 
	 * @return the readerFactoryID
	 */
	public String getReaderFactoryID() {
		return readerFactoryID;
	}

	/**
	 * Returns the list of attributes.
	 * 
	 * @return the attributes
	 */
	public AttributeList getAttributes() {
		return attributes;
	}

	/**
	 * Returns the sessions.
	 * 
	 * @return the sessions
	 */
	public List<SessionDTO> getSessions() {
		return sessions;
	}

}

/*
 * ReaderDTO.java
 * 
 * Created:     July 22nd, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:     The software in this package is published under the terms of the EPL License
 *                   A copy of the license is included in this distribution under Rifidi-License.txt 
 */
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
	/** The display name of the reader */
	private String displayName;

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
	 * @param displayName
	 */
	public ReaderDTO(String readerID, String readerFactoryID,
			AttributeList attributes, List<SessionDTO> sessions,
			String displayName) {
		this.readerID = readerID;
		this.readerFactoryID = readerFactoryID;
		this.attributes = attributes;
		this.sessions = sessions;
		this.displayName = displayName;
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

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

}

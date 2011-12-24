/*
 * ReaderFactoryDTO.java
 * 
 * Created:     July 22nd, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:     The software in this package is published under the terms of the EPL License
 *                   A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.api;

import java.io.Serializable;

/**
 * Data Transfer Object for Reader Factory. For serializing information about a
 * ReaderFactory
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class ReaderFactoryDTO implements Serializable {

	/** The default SerialVersuinID */
	private static final long serialVersionUID = 1L;
	/** The ID of the reader factory */
	private String readerFactoryID;
	/** The display name of the Reader Factory */
	private String readerFactoryDisplayName;
	/** The description of the reader factory */
	private String readerFactoryDescription;

	/***
	 * Constructor
	 * 
	 * @param readerFactoryID
	 * @param readerFactoryDisplayName
	 * @param readerFactoryDescription
	 */
	public ReaderFactoryDTO(String readerFactoryID,
			String readerFactoryDisplayName, String readerFactoryDescription) {
		super();
		this.readerFactoryID = readerFactoryID;
		this.readerFactoryDisplayName = readerFactoryDisplayName;
		this.readerFactoryDescription = readerFactoryDescription;
	}

	/**
	 * Return the ID for the factory.
	 * 
	 * @return the readerFactoryID
	 */
	public String getReaderFactoryID() {
		return readerFactoryID;
	}

	/**
	 * Gets the displayname for the readerfactory.
	 * 
	 * @return the readerFactoryDisplayName
	 */
	public String getReaderFactoryDisplayName() {
		return readerFactoryDisplayName;
	}

	/**
	 * Returns the description for this readerfactory.
	 * 
	 * @return the readerFactoryDescription
	 */
	public String getReaderFactoryDescription() {
		return readerFactoryDescription;
	}

}

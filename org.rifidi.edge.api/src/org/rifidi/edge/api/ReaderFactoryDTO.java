/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
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

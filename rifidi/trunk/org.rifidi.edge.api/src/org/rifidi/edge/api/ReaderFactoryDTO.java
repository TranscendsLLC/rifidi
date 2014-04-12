/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
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

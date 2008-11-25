/* 
 * ReaderObject.java
 *  Created:	Jul 24, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.sitewizard.creator;

/**
 * This class holds the name, description, and location of a reader jar.  
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class ReaderObject {

	/**
	 * The name of the reader.
	 */
	private String name;

	/**
	 * The description of the reader.
	 */
	private String description;

	/**
	 * The include of a reader.
	 */
	private String include;

	/**
	 * The ReaderObject constructor.
	 * 
	 * @param name
	 * @param desc
	 * @param include
	 */
	public ReaderObject(String name, String desc, String include) {
		this.name = name;
		this.description = desc;
		this.include = include;
	}

	/**
	 * Default constructor. All of the values will be set to null.
	 */
	public ReaderObject() {
		this(null, null, null);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the include
	 */
	public String getInclude() {
		return include;
	}

	/**
	 * @param include
	 *            the include to set
	 */
	public void setInclude(String include) {
		this.include = include;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		if (other instanceof ReaderObject) {
			ReaderObject ro = (ReaderObject) other;
			if (ro.getName().equals(name)
					&& ro.getDescription().equals(description)
					&& ro.getInclude().equals(include)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new String(this.name + ", " + this.description + ", "
				+ this.include);
	}
}

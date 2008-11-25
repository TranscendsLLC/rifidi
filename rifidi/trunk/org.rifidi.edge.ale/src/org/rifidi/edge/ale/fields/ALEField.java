/*
 *  ALEField.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.ale.fields;

/**
 * This interface represents an ALE Field. A field specifies a piece of data
 * found on a tag. Fields can be generic (such as fixed) or symbolic (such as
 * the built in 'epc' field).
 * 
 * Fields have valid data types, as well as a default data type and format
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface ALEField {

	/**
	 * Get the data from this field in the default data type and format
	 * 
	 * @return The data in this field
	 */
	public String getData();

	/**
	 * Get the data from this field in the specified data type and format
	 * 
	 * @param type
	 *            The data type for this field
	 * @param format
	 *            the format of the specified data type
	 * @return the data in this field
	 */
	public String getData(String type, String format);

}

/*
 *  ALEDataType.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.ale.datatypes;

import org.rifidi.edge.ale.datatypes.builtin.formats.ALEFormat;

/**
 * An ALE datatype specifies what kind of data that values in a field contain
 * and how they are encoded in Tag Memory.
 * 
 * Fields contain a set of formats that are valid for that data type. For
 * example, the unsigned integer data type can be either 'decimal' or 'hex'
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface ALEDataType {

	/**
	 * Returns the data from this ALEDataType in the specified format
	 * 
	 * @param format
	 *            the format to return the data in
	 * @return the data in the specified format
	 */
	public String getData(ALEFormat format);

}

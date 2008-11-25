/*
 *  UIntFormats.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.ale.datatypes.builtin.formats;

import org.rifidi.edge.ale.identifiers.ALE_Identifiers;

/**
 * The list of valid formats for the uint datatype
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public enum UIntFormats implements ALEFormat {
	HEX, DECIMAL;

	/**
	 * Returns an ENUM for the format name
	 * 
	 * @param formatName
	 *            the name of the format
	 * @return an ENUM of the format
	 * @throws IllegalArgumentException
	 *             if the formatname is not valid
	 */
	public UIntFormats getFormat(String formatName) {
		if (formatName.equals(ALE_Identifiers.FORMAT_UINT_HEX)) {
			return HEX;
		} else if (formatName.equals(ALE_Identifiers.FORMAT_UINT_DECIMAL)) {
			return DECIMAL;
		} else
			throw new IllegalArgumentException(formatName
					+ " is not a valid format for the uint datatype");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.ale.datatypes.builtin.formats.ALEFormat#getFormatName()
	 */
	@Override
	public String getFormatName() {
		return this.name();
	}
}

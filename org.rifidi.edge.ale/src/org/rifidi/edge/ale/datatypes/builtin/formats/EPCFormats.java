/*
 *  EPCFormats.java
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
 * This list of valid formats for the epc datatype
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public enum EPCFormats implements ALEFormat {
	EPC_PURE, EPC_TAG, EPC_HEX, EPC_DECIMAL;

	public EPCFormats getFormat(String formatName) {
		if (formatName.equals(ALE_Identifiers.FORMAT_EPC_PURE)) {
			return EPC_PURE;
		} else if (formatName.equals(ALE_Identifiers.FORMAT_EPC_TAG)) {
			return EPC_TAG;
		} else if (formatName.equals(ALE_Identifiers.FORMAT_EPC_HEX)) {
			return EPC_HEX;
		} else if (formatName.equals(ALE_Identifiers.FORMAT_EPC_DECIMAL)) {
			return EPC_DECIMAL;
		} else
			throw new IllegalArgumentException(formatName
					+ " is not a valid format for the epc datatype");
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

/*
 *  StringFormats.java
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
 * The list of valid formats for the String datatype
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public enum StringFormats implements ALEFormat {
	STRING;

	public StringFormats getFormat(String formatName) {
		if (formatName.equals(ALE_Identifiers.FORMAT_STRING_STRING)) {
			return STRING;
		} else
			throw new IllegalArgumentException(
					formatName
							+ " is not a valid format for the iso-15962-string datatype");
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

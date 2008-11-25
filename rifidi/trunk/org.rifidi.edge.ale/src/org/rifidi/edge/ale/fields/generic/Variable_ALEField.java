/*
 *  Variable_ALEField.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.ale.fields.generic;

import org.rifidi.edge.ale.datatypes.ALEDataTypes;
import org.rifidi.edge.ale.datatypes.builtin.String_ALEDataType;
import org.rifidi.edge.ale.datatypes.builtin.formats.ALEFormat;
import org.rifidi.edge.ale.datatypes.builtin.formats.StringFormats;
import org.rifidi.edge.ale.fields.ALEField;
import org.rifidi.edge.ale.identifiers.ALE_Identifiers;

/**
 * ALE field for Variable Addresses.
 * 
 * See 6.1.9.2 in ALE 1.1.
 * 
 * Default datatype: iso-15962-string
 * 
 * Default format: string
 * 
 * Allowable dataypes: iso-15962-string
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class Variable_ALEField implements ALEField {

	private String _data;

	/**
	 * Construct a new Variable ALE field
	 * 
	 * @param data
	 *            the data for the variable ALE field
	 * @throws IllegalArgumentException
	 *             if data is null
	 */
	public Variable_ALEField(String data) {
		if (null == data) {
			throw new IllegalArgumentException("data cannot be null");
		}
		_data = data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.ale.fields.ALEField#getData()
	 */
	@Override
	public String getData() {
		return getData(ALEDataTypes.STRING, StringFormats.STRING);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.ale.fields.ALEField#getData(org.rifidi.edge.ale.datatypes
	 * .ALEDataTypes, org.rifidi.edge.ale.datatypes.builtin.formats.ALEFormat)
	 */
	@Override
	public String getData(ALEDataTypes type, ALEFormat format) {
		switch (type) {
		case BITS:
			throw new IllegalArgumentException(ALE_Identifiers.DATATYPE_BITS
					+ " is not a valid datatype for variable address fields");
		case EPC:
			throw new IllegalArgumentException(ALE_Identifiers.DATATYPE_EPC
					+ " is not a valid datatype for variable address fields");
		case STRING:
			return new String_ALEDataType(_data).getData(format);
		case UINT:
			throw new IllegalArgumentException(ALE_Identifiers.DATATYPE_UINT
					+ " is not a valid datatype for variable address fields");
		default:
			throw new IllegalArgumentException("Unrecognized ALE Datatype: "
					+ type);
		}

	}

}

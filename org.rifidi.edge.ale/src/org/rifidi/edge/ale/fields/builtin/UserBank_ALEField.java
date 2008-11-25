/*
 *  UserBank_ALEField.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.ale.fields.builtin;

import org.rifidi.edge.ale.datatypes.ALEDataTypes;
import org.rifidi.edge.ale.datatypes.builtin.UnsignedInteger_ALEDataType;
import org.rifidi.edge.ale.datatypes.builtin.formats.ALEFormat;
import org.rifidi.edge.ale.datatypes.builtin.formats.UIntFormats;
import org.rifidi.edge.ale.fields.ALEField;
import org.rifidi.edge.ale.identifiers.ALE_Identifiers;
import org.rifidi.edge.tags.util.BitVector;

/**
 * ALE field for built-in field 'userBank', as described by section 6.1.6 in ALE
 * 1.1
 * 
 * Default datatype: uint
 * 
 * Default format: hex
 * 
 * Allowable dataypes: uint
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class UserBank_ALEField implements ALEField {

	/**
	 * The User Bank as a BitVector
	 */
	private BitVector _userBank;

	/**
	 * Construct a new UserBank_ALEField from a BitVector
	 * 
	 * @param userBank
	 *            the User Bank as a BitVector
	 * @throws IllegalArgumentException
	 *             if userBank is null
	 */
	public UserBank_ALEField(BitVector userBank) {
		if (null == userBank) {
			throw new IllegalArgumentException("userBank cannot be null");
		}
		_userBank = userBank;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.ale.fields.ALEField#getData()
	 */
	@Override
	public String getData() {
		return getData(ALEDataTypes.UINT, UIntFormats.HEX);
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
					+ " is not a valid data type for "
					+ ALE_Identifiers.FIELDNAME_USERBANK);
		case EPC:
			throw new IllegalArgumentException(ALE_Identifiers.DATATYPE_EPC
					+ " is not a valid data type for "
					+ ALE_Identifiers.FIELDNAME_USERBANK);
		case STRING:
			throw new IllegalArgumentException(ALE_Identifiers.DATATYPE_STRING
					+ " is not a valid data type for "
					+ ALE_Identifiers.FIELDNAME_USERBANK);
		case UINT:
			return new UnsignedInteger_ALEDataType(_userBank).getData(format);
		default:
			throw new IllegalArgumentException("Unrecognized ALE Datatype: "
					+ type);
		}
	}
}

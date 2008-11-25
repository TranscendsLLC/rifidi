/*
 *  Fixed_ALEField.java
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
import org.rifidi.edge.ale.datatypes.builtin.Bits_ALEDataType;
import org.rifidi.edge.ale.datatypes.builtin.EPC_ALEDataType;
import org.rifidi.edge.ale.datatypes.builtin.UnsignedInteger_ALEDataType;
import org.rifidi.edge.ale.datatypes.builtin.formats.ALEFormat;
import org.rifidi.edge.ale.datatypes.builtin.formats.UIntFormats;
import org.rifidi.edge.ale.fields.ALEField;
import org.rifidi.edge.ale.identifiers.ALE_Identifiers;
import org.rifidi.edge.tags.util.BitVector;

/**
 * ALE Field for Absolute Addresses. See 6.1.9.1 in ALE 1.1.
 * 
 * Default datatype: UINT
 * 
 * Default format: HEX
 * 
 * Allowable dataypes: uint, bits, epc
 * 
 * @author Kyle Neumeier kyle@pramari.com
 * 
 */
public class Fixed_ALEField implements ALEField {

	private BitVector _bits;

	/**
	 * Construct a new Absolute Address ALE Field
	 * 
	 * @param bits
	 *            the bits as a BitVector
	 * @throws IllegalArgumentException
	 *             if bits is null
	 */
	public Fixed_ALEField(BitVector bits) {
		if (null == bits) {
			throw new IllegalArgumentException("bits cannot be null");
		}
		_bits = bits;
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
			return new Bits_ALEDataType(_bits).getData(format);
		case EPC:
			return new EPC_ALEDataType(_bits).getData(format);
		case STRING:
			throw new IllegalArgumentException(ALE_Identifiers.DATATYPE_STRING
					+ " is not a valid datatype for absolute address fields");
		case UINT:
			return new UnsignedInteger_ALEDataType(_bits).getData(format);
		default:
			throw new IllegalArgumentException("Unrecognized ALE Datatype: "
					+ type);
		}

	}
}

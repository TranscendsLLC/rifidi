/*
 *  EPC_ALEField.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.ale.fields.builtin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.ale.datatypes.ALEDataTypes;
import org.rifidi.edge.ale.datatypes.builtin.EPC_ALEDataType;
import org.rifidi.edge.ale.datatypes.builtin.formats.ALEFormat;
import org.rifidi.edge.ale.datatypes.builtin.formats.EPCFormats;
import org.rifidi.edge.ale.fields.ALEField;
import org.rifidi.edge.ale.identifiers.ALE_Identifiers;
import org.rifidi.edge.tags.util.BitVector;

/**
 * ALE field for built-in field 'epc', as described by section 6.1.1 in ALE 1.1
 * 
 * Default datatype: epc
 * 
 * Default format: epc-tag
 * 
 * Allowable dataypes: epc
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class EPC_ALEField implements ALEField {

	/**
	 * The EPC as a Bit Vector
	 */
	private BitVector _epc;

	/**
	 * The PCBits as a bit vector. Must be 16 bits long
	 */
	private BitVector _pcBits;

	/**
	 * The logger for this class
	 */
	private Log logger = LogFactory.getLog(EPC_ALEField.class);

	/**
	 * Construct a new EPC_ALEField from an epc bit vector and a PCBits bit
	 * vector. If PCBits are not used, null should be passed in. If pcBits does
	 * not have 16 bits, it will be treated as if null was passed in for pcBits
	 * 
	 * @param epc
	 *            the epc as a bit vector
	 * @param pcBits
	 *            the PCBits. If not used, null should be passed in
	 * @throws IllegalArgumentException
	 *             if null is passed in for epc
	 */
	public EPC_ALEField(BitVector epc, BitVector pcBits) {
		if (null == epc) {
			throw new IllegalArgumentException("epc cannot be null");
		}
		_epc = epc;
		if (null != pcBits) {
			if (pcBits.bitLength() == 16) {
				_pcBits = pcBits;
			} else {
				logger.error("Length of PC Bits is not 16.  Ignoring PC Bits");
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.ale.fields.ALEField#getData()
	 */
	@Override
	public String getData() {
		return getData(ALEDataTypes.EPC, EPCFormats.EPC_TAG);
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
					+ ALE_Identifiers.FIELDNAME_EPC);
		case EPC:
			EPC_ALEDataType epcDT;
			if (null == _pcBits) {
				epcDT = new EPC_ALEDataType(_epc);
			} else {
				epcDT = new EPC_ALEDataType(_epc, _pcBits);
			}
			return epcDT.getData(format);
		case STRING:
			throw new IllegalArgumentException(ALE_Identifiers.DATATYPE_STRING
					+ " is not a valid data type for "
					+ ALE_Identifiers.FIELDNAME_EPC);
		case UINT:
			throw new IllegalArgumentException(ALE_Identifiers.DATATYPE_UINT
					+ " is not a valid data type for "
					+ ALE_Identifiers.FIELDNAME_EPC);
		default:
			throw new IllegalArgumentException("Unrecognized ALE Datatype: "
					+ type);
		}
	}

}

/*
 *  EPC_ALEDataType.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.ale.datatypes.builtin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.ale.datatypes.ALEDataType;
import org.rifidi.edge.ale.datatypes.builtin.formats.ALEFormat;
import org.rifidi.edge.ale.datatypes.builtin.formats.EPCFormats;
import org.rifidi.edge.tags.encodings.epc.data.EPCFactory;
import org.rifidi.edge.tags.encodings.epc.data.PCBits;
import org.rifidi.edge.tags.encodings.epc.exceptions.CannotConvertEPCException;
import org.rifidi.edge.tags.util.BitVector;

/**
 * This class represents the epc datatype as specified by section 6.2.1 in the
 * ALE 1.1 specification.
 * 
 * This class does not yet support writing for RW formats
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class EPC_ALEDataType implements ALEDataType {

	/**
	 * The logger for this class
	 */
	private Log logger = LogFactory.getLog(EPC_ALEDataType.class);

	/**
	 * The incoming EPC as a bit vector
	 */
	private BitVector _epc;

	/**
	 * The PCBits for this epc. If the PCBits are not available, this will be
	 * null
	 */
	private PCBits _pcBits;

	/**
	 * Construct a new EPC_ALEDataType from an EPC
	 * 
	 * @param epc
	 *            the epc as a bitvector
	 */
	public EPC_ALEDataType(BitVector epc) {
		_epc = epc;
	}

	/**
	 * Construct a new EPC_ALEDataType from and EPC and PCBits
	 * 
	 * @param epc
	 *            the epc as a bitvector
	 * @param pcBits
	 *            the PCBits
	 */
	public EPC_ALEDataType(BitVector epc, BitVector pcBits) {
		_epc = epc;
		_pcBits = new PCBits(pcBits.toString(2));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.ale.datatypes.ALEDataType#getData(java.lang.String)
	 */
	@Override
	public String getData(ALEFormat format) {
		EPCFormats epcFormat = Enum.valueOf(EPCFormats.class, format
				.getFormatName());
		switch (epcFormat) {
		case EPC_DECIMAL:
			return getEPCAsRawDecimalURI();
		case EPC_HEX:
			return getEPCAsRawHexURI();
		case EPC_PURE:
			return getEPCAsPureURI();
		case EPC_TAG:
			return getEPCAsTagURI();
		}
		logger.error("ALEFormat: " + format
				+ " is not a valid format for EPC_ALEDatatType");
		throw new IllegalArgumentException("ALEFormat: " + format
				+ " is not a valid format for EPC_ALEDatatType");
	}

	/**
	 * Values are formatted according to the procedure in Section 5.1 of [TDS
	 * 1.4], or Section 5.3 of [TDS 1.4] if a toggle bit and AFI are available
	 * (as when reading from a Gen2 Tag). If the procedure in Section 5.1 of
	 * [TDS1.3.1] results in an error, then the value is formatted as a raw
	 * hexadecimal value following Step 26 of the procedure in Section 5.2 of
	 * [TDS 1.4], or following Steps 6 through 8 of the procedure in Section 5.4
	 * of [TDS 1.4] if a toggle bit and AFI are available and the toggle bit is
	 * a one.
	 * 
	 * @return the epc as a pure format, or as a raw format if it cannot be
	 *         converted to a pure
	 */
	private String getEPCAsPureURI() {
		String epcBitString = _epc.toString(2);
		EPCFactory factory = new EPCFactory();

		// if pcBits are not available, try to convert to pure. If that does not
		// work, convert to raw, which always works
		if (null == _pcBits) {
			try {
				return factory.toPureFromBitString(epcBitString);
			} catch (CannotConvertEPCException e) {
				return factory.toRawFromBitString(epcBitString, true);
			}
		}

		// if pcBits are available, try to convert to pure. If that does not
		// work (because togglebit==1), convert to raw, which should always work

		else {
			try {
				return factory.toPureFromG2Memory(epcBitString, _pcBits);
			} catch (CannotConvertEPCException e) {
				try {
					return factory.toRawFromG2Memory(epcBitString, _pcBits);
				} catch (CannotConvertEPCException e1) {
					// this should never happen because toRawFromG2Memory only
					// fails if togglebit==0, but that is the reason we failed
					// above
					logger.error("Cannot convert epc to Pure.  "
							+ "This should never happen", e1);
					return null;
				}
			}

		}
	}

	/**
	 * Values are formatted according to the procedure in Section 5.2 of
	 * [TDS1.4], or Section 5.4 of [TDS1.4] if a toggle bit and AFI are
	 * available (as when reading from a Gen2 Tag).
	 * 
	 * Currently writing is not supported
	 * 
	 * @return the epc in tag uri format, unless there was a problem, then
	 *         return it as a raw uri
	 */
	private String getEPCAsTagURI() {
		String epcBitString = _epc.toString(2);
		EPCFactory factory = new EPCFactory();

		if (null == _pcBits) {
			try {
				// try to form the epc as a tag uri
				return factory.toTagFromBitString(epcBitString);
			} catch (CannotConvertEPCException ex) {
				// if there was a problem, form the epc as a raw uri
				return factory.toRawFromBitString(epcBitString, true);
			}
		}

		else {
			try {
				// do steps 1-4 of 5.4
				return factory.toTagFromG2Memory(epcBitString, _pcBits);
			} catch (CannotConvertEPCException e) {
				// do steps 5-8 of 5.4
				try {
					return factory.toRawFromG2Memory(epcBitString, _pcBits);
				} catch (CannotConvertEPCException e1) {
					// this should never happen because toRawFromG2Memory only
					// fails if togglebit==0, but that is the reason we failed
					// above
					logger.error("Cannot convert epc to Pure.  "
							+ "This should never happen", e1);
					return null;
				}
			}
		}
	}

	/**
	 * Values are formatted according to Step 20 of the procedure in Section 5.2
	 * of [TDS1.3.1], or following Steps 6 through 8 of the procedure in Section
	 * 5.4 of [TDS1.3.1] if a toggle bit and AFI are available and the toggle
	 * bit is a one.
	 * 
	 * Writing is not currently supported
	 * 
	 * @return an EPC formated as a raw URI (urn:epc:raw:N.V, or
	 *         urn:epc:raw:N.A.V where V is a hex number)
	 */
	private String getEPCAsRawHexURI() {
		String epcBitString = _epc.toString(2);
		EPCFactory factory = new EPCFactory();

		// if pcbits are not available, return as urn:epc:raw:N.V
		if (null == _pcBits) {
			return factory.toRawFromBitString(epcBitString, true);
		}

		else {
			try {
				// if pcbits are available and toggle==1, return as
				// urn:epc:raw:N.A.V
				return factory.toRawFromG2Memory(epcBitString, _pcBits);
			} catch (CannotConvertEPCException e) {
				// if pcbits are available, but toggle==0, return as
				// urn:epc:raw:N.V
				return factory.toRawFromBitString(epcBitString, true);
			}

		}
	}

	/**
	 * Values are formatted according to Step 20 of the procedure in Section 5.2
	 * of [TDS1.3.1], or following Steps 6 through 8 of the procedure in Section
	 * 5.4 of [TDS1.3.1] if a toggle bit and AFI are available and the toggle
	 * bit is a one. The only difference is that the V portion of the URI does
	 * not include a leading 'x'
	 * 
	 * Writing is not currently supported
	 * 
	 * @return an EPC formated as a raw URI (urn:epc:raw:N.V, or
	 *         urn:epc:raw:N.A.V where V is a decimal number)
	 */
	private String getEPCAsRawDecimalURI() {
		String epcBitString = _epc.toString(2);
		EPCFactory factory = new EPCFactory();

		// if pcbits are not available, return as urn:epc:raw:N.V
		if (null == _pcBits) {
			return factory.toRawFromBitString(epcBitString, false);
		}

		else {
			try {
				// if pcbits are available and toggle==1, return as
				// urn:epc:raw:N.A.V
				return factory.toRawFromG2Memory(epcBitString, _pcBits);
			} catch (CannotConvertEPCException e) {
				// if pcbits are available, but toggle==0, return as
				// urn:epc:raw:N.V
				return factory.toRawFromBitString(epcBitString, false);
			}

		}
	}
}

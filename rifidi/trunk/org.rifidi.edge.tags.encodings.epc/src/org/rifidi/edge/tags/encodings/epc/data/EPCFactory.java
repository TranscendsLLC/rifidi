/*
 *  EPCFactory.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.tags.encodings.epc.data;

import java.math.BigInteger;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fosstrak.tdt.exported.LevelTypeList;
import org.fosstrak.tdt.exported.TDTEngine;
import org.fosstrak.tdt.exported.TDTException;
import org.fosstrak.tdt.pool.TDTEnginePool;
import org.rifidi.edge.tags.encodings.epc.exceptions.CannotConvertEPCException;
import org.rifidi.edge.tags.encodings.epc.util.EPC_Utilities;
import org.rifidi.edge.tags.util.ConvertingUtil;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * This class allows the user to convert between various formats for EPCs. It
 * relies on the TDT library from fosstrak (see www.fosstrak.org). It is not
 * thread safe; a new instance should be created whenever a conversion is
 * necessary.
 * 
 * The following conversions are possible
 * 
 * 
 * BITS -> PURE
 * 
 * BITS -> TAG
 * 
 * BITS -> RAW
 * 
 * BITS -> GEN2TagMem
 * 
 * TAG -> TAG
 * 
 * TAG -> BITS
 * 
 * TAG -> PURE
 * 
 * TAG -> RAW
 * 
 * TAG -> GEN2TagMem
 * 
 * RAW -> GEN2TagMem
 * 
 * RAW -> BITS
 * 
 * GEN2TagMem -> TAG
 * 
 * GEN2TagMem -> PURE
 * 
 * GEN2TagMem -> RAW
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class EPCFactory {

	private static final Log logger = LogFactory.getLog(EPCFactory.class);

	/**
	 * The pool of TDT engines
	 */
	private TDTEnginePool enginePool = null;

	/**
	 * Create a new instance of the factory
	 */
	public EPCFactory() {
		ServiceRegistry.getInstance().service(this);
	}

	/**
	 * This method follows procedure 5.1 in TDS 1.4
	 * 
	 * @param epcBitString
	 *            a string of ones and zeros that represents the epc
	 * @return a string in the form urn:epc:id:
	 * @throws IllegalArgumentException
	 *             if epcBitString is null
	 * @throws CannotConvertEPCException
	 *             if there was a problem when converting to to a Pure URI
	 */
	public String toPureFromBitString(String epcBitString)
			throws CannotConvertEPCException {
		if (null == epcBitString) {
			throw new IllegalArgumentException("epcBitString cannot be null");
		}
		if (EPC_Utilities.isBits(epcBitString)) {
			return convert(epcBitString, LevelTypeList.PURE_IDENTITY);
		} else {
			throw new CannotConvertEPCException(epcBitString
					+ " is not in the correct format");
		}
	}

	/**
	 * This method follows procedure 5.3 in TDS 1.4
	 * 
	 * @param epcBitString
	 *            a string of ones and zeros that represents the epc
	 * @param PCBitsString
	 *            a string of 16 ones and zeros that represent the pcBits from
	 *            the Gen2TagMemory
	 * @return string in the form of urn:epc:id:
	 * @throws IllegalArgumentException
	 *             if one of the arguments is null
	 * @throws CannotConvertEPCException
	 *             if there was a problem when converting to to a Pure URI
	 */
	public String toPureFromG2Memory(String epcBitString, String PCBitsString)
			throws CannotConvertEPCException {
		if (null == epcBitString || null == PCBitsString) {
			throw new IllegalArgumentException("arguments cannot be null");
		}
		try {
			PCBits pcbits = new PCBits(PCBitsString);
			return toPureFromG2Memory(epcBitString, pcbits);
		} catch (IllegalArgumentException ex) {
			throw new CannotConvertEPCException(
					"pcbits is not in the correct format");
		}

	}

	public String toPureFromG2Memory(String epcBitString, PCBits pcbits)
			throws CannotConvertEPCException {
		if (null == epcBitString || null == pcbits) {
			throw new IllegalArgumentException("arguments cannot be null");
		}
		if (EPC_Utilities.isBits(epcBitString)) {
			int N = pcbits.getLength();
			if (pcbits.getToggleBit()) {
				throw new CannotConvertEPCException(
						"Cannot convert Gen2Mem->Pure when toggle bit is set");
			}

			try {
				String bitsToConvert = epcBitString.substring(0, N);
				return convert(bitsToConvert, LevelTypeList.PURE_IDENTITY);
			} catch (IndexOutOfBoundsException ex) {
				throw new CannotConvertEPCException("N > number of bits in EPC");
			}
		} else {
			throw new CannotConvertEPCException(epcBitString
					+ " is not in the correct format");
		}
	}

	/**
	 * This method converts a epc in a Tag URI (in the form urn:epc:tag:) to an
	 * EPC in the pure URI (urn:epc:id:).
	 * 
	 * @param epcPureURI
	 *            a epc in the form urn:epc:tag:
	 * @return an epc in the form urn:epc:id:
	 * @throws IllegalArgumentException
	 *             if epcTagURI is null
	 * @throws CannotConvertEPCException
	 *             if there was a problem when converting to to a Pure URI
	 */
	public String toPureFromTag(String epcTagURI)
			throws CannotConvertEPCException {
		if (null == epcTagURI) {
			throw new IllegalArgumentException("epcTagURI cannot be null");
		}
		if (EPC_Utilities.isTag(epcTagURI)) {
			return convert(epcTagURI, LevelTypeList.PURE_IDENTITY);
		} else {
			throw new CannotConvertEPCException(epcTagURI
					+ " is not in the correct format");
		}
	}

	/**
	 * This method converts an EPC in bit string (a string of ones and zeros) to
	 * Tag URI form (urn:epc:tag:). It follows the procedure 5.2 in TDS 1.4,
	 * except that if it fails, it throws an exception and does not return a tag
	 * in the raw URI form
	 * 
	 * @param epcBitString
	 *            a string of ones and zeros that represents the epc
	 * @return a string in the form of urn:epc:tag:
	 * @throws IllegalArgumentException
	 *             if epcBitString is null
	 * @throws CannotConvertEPCException
	 *             if there was a problem when converting to Tag URI Format
	 */
	public String toTagFromBitString(String epcBitString)
			throws CannotConvertEPCException {
		if (null == epcBitString) {
			throw new IllegalArgumentException("epcBitString cannot be null");
		}
		if (EPC_Utilities.isBits(epcBitString)) {
			return convert(epcBitString, LevelTypeList.TAG_ENCODING);
		} else {
			throw new CannotConvertEPCException(epcBitString
					+ " is not in the correct format");
		}
	}

	/**
	 * This method follows steps 1-4 of procedure 5.4. If the toggle bit is set,
	 * this method throws an error, and does not convert the tag into the raw
	 * format.
	 * 
	 * @param epcBitString
	 *            a string of ones and zeros that represents the epc
	 * @param PCBitsString
	 *            a string of 16 ones and zeros that represent the pcBits from
	 *            the Gen2TagMemory
	 * @return A string in the form urn:epc:tag
	 * @throws IllegalArgumentException
	 *             if one of the arguments are null
	 * @throws CannotConvertEPCException
	 *             if there was a problem when converting to a Tag URI
	 */
	public String toTagFromG2Memory(String epcBitString, String PCBitsString)
			throws CannotConvertEPCException {
		if (null == epcBitString || null == PCBitsString) {
			throw new IllegalArgumentException("arguments cannot be null");
		}

		try {
			PCBits pcbits = new PCBits(PCBitsString);
			return toTagFromG2Memory(epcBitString, pcbits);
		} catch (IllegalArgumentException ex) {
			throw new CannotConvertEPCException(
					"pcbits is not in the correct format");
		}

	}

	/**
	 * This method follows steps 1-4 of procedure 5.4. If the toggle bit is set,
	 * this method throws an error, and does not convert the tag into the raw
	 * format.
	 * 
	 * @param epcBitString
	 *            a string of ones and zeros that represents the epc
	 * @param pcbits
	 *            an object that represents the pcbtis from the epc memory bank
	 *            header on a gen2 tag
	 * @return A string in the form urn:epc:tag
	 * @throws IllegalArgumentException
	 *             if one of the arguments are null
	 * @throws CannotConvertEPCException
	 *             if there was a problem when converting to a Tag URI
	 */
	public String toTagFromG2Memory(String epcBitString, PCBits pcbits)
			throws CannotConvertEPCException {
		if (null == epcBitString || null == pcbits) {
			throw new IllegalArgumentException("arguments cannot be null");
		}
		if (EPC_Utilities.isBits(epcBitString)) {
			int N = pcbits.getLength();
			if (pcbits.getToggleBit()) {
				throw new CannotConvertEPCException(
						"Cannot convert Gen2Mem->Tag when toggle bit is set");
			}

			try {
				String bitsToConvert = epcBitString.substring(0, N);
				return convert(bitsToConvert, LevelTypeList.TAG_ENCODING);
			} catch (IndexOutOfBoundsException ex) {
				throw new CannotConvertEPCException("N > number of bits in EPC");
			}
		} else {
			throw new CannotConvertEPCException(epcBitString
					+ " is not in the correct format");
		}
	}

	/**
	 * This method follows steps 3-60 of procedure 5.5 to convert a Tag URI into
	 * a bit string
	 * 
	 * @param epcTagURI
	 *            An EPC in the form urn:epc:tag
	 * @return a string of ones and zeros that represents the EPC
	 * @throws IllegalArgumentException
	 *             if epcTagURI is null
	 * @throws CannotConvertEPCException
	 *             if there was a problem when converting to a Bit String
	 */
	public String toBitStringFromTag(String epcTagURI)
			throws CannotConvertEPCException {
		if (null == epcTagURI) {
			throw new IllegalArgumentException("epcTagURI cannot be null");
		}
		if (EPC_Utilities.isTag(epcTagURI)) {
			return convert(epcTagURI, LevelTypeList.BINARY);
		} else {
			throw new CannotConvertEPCException(epcTagURI
					+ " is not in the correct format");
		}
	}

	/**
	 * This method follows step 2 of procedure 5.5 to convert a Raw URI
	 * (urn:epc:raw) to a bit string
	 * 
	 * @param epcRawURI
	 *            an epc of the form urn:epc:raw:N.V
	 * @return a string of ones and zeros that represents the EPC
	 * @throws IllegalArgumentException
	 *             if epcRawURI is null
	 * @throws CannotConvertEPCException
	 *             if there was a problem when converting to a Bit String
	 */
	public String toBitStringFromRaw(String epcRawURI)
			throws CannotConvertEPCException {
		if (null == epcRawURI) {
			throw new IllegalArgumentException("epcRawURI cannot be null");
		}

		if (EPC_Utilities.isAFIRaw(epcRawURI)) {
			throw new CannotConvertEPCException(
					"Cannot EPC of form urn:epc:raw:N.A.V convert to bit string ");
		} else {
			boolean isHex = EPC_Utilities.isHexRaw(epcRawURI);
			boolean isDec = EPC_Utilities.isDecimalRaw(epcRawURI);

			// if epc is in correct form
			if (isHex | isDec) {

				// split into urn|epc|raw|N.V
				String[] pieces = epcRawURI.split(":");

				// split N.V into N|V
				String[] numbers = pieces[3].split("\\.");
				Integer N = Integer.parseInt(numbers[0]);

				// if hex, convert V from hex to binary
				String V;
				if (isHex) {
					String hex = numbers[1].substring(1);
					V = ConvertingUtil.toString(hex, 16, 2, N);
				}
				// if decimal, convert V from decimal to binary
				else {
					String decimal = numbers[1];
					V = ConvertingUtil.toString(decimal, 10, 2, N);
				}

				// if number of bits in V is more than N, throw exception
				if (V.length() > N) {
					throw new CannotConvertEPCException(
							"V is to large to fit into an integer of size " + N);
				}

				// return bits
				return V;
			} else {
				throw new CannotConvertEPCException(epcRawURI
						+ " is not in proper raw form");
			}
		}
	}

	/**
	 * Follows step 26 from procedure 5.2 in TDS 1.1 specification. Converts a
	 * bit string to a URN of the form urn:epc:raw:N.V. If hexFormat is true, V
	 * is a hex number, with a leading 'x' character. If it is false, it is a
	 * decimal number
	 * 
	 * @param epcBitString
	 *            the bit string to convert
	 * @param hexFormat
	 *            if true, V is a hex number, other wise V is a decimal
	 * @throws IllegalArgumentException
	 *             if epcBitString is null
	 * @return a URN of the form urn:epc:raw:N.V, where V is either a hex or
	 *         decimal number depending on the <code>hexFormat</code> argument
	 */
	public String toRawFromBitString(String epcBitString, boolean hexFormat) {
		if (epcBitString == null) {
			throw new IllegalArgumentException("epcBitString cannot be null");
		}
		if (!EPC_Utilities.isBits(epcBitString)) {
			throw new IllegalArgumentException(epcBitString
					+ " is not in the correct format");
		}
		int numBits = epcBitString.length();
		if (hexFormat) {
			int minBits = ConvertingUtil.roundUpDivision(numBits, 4);
			String hex = ConvertingUtil.toString(epcBitString, 2, 16, minBits)
					.toUpperCase();
			return EPC_Utilities.EPC_RAW_HEADER + numBits + ".x" + hex;
		} else {
			String decimal = ConvertingUtil.toString(epcBitString, 2, 10, 0);
			return EPC_Utilities.EPC_RAW_HEADER + numBits + "." + decimal;
		}
	}

	/**
	 * This method follows steps 5-8 from procedure 5.4 in TDS 1.4. Converts a
	 * gen2 tag memory to a URN of the form urn:epc:raw:N.A.V.
	 * 
	 * @param epcBitString
	 *            the bit string to convert
	 * @param PCBitsString
	 *            a string of 16 ones and zeros that represent the pcBits from
	 *            the Gen2TagMemory
	 * @return a URN of the form urn:epc:raw:N.A.V
	 * @throws IllegalArgumentException
	 *             if one of the arguments is null
	 * @throws CannotConvertEPCException
	 *             if toggle bit is 0
	 */
	public String toRawFromG2Memory(String epcBitString, String PCBitsString)
			throws CannotConvertEPCException {
		if (null == epcBitString || null == PCBitsString) {
			throw new IllegalArgumentException("arguments cannot be null");
		}

		PCBits pcbits = null;
		try {
			pcbits = new PCBits(PCBitsString);
			return toRawFromG2Memory(epcBitString, pcbits);
		} catch (IllegalArgumentException e) {
			throw new CannotConvertEPCException(
					"pcbits is not in the correct format");
		}

	}

	/**
	 * This method follows steps 5-8 from procedure 5.4 in TDS 1.4. Converts a
	 * gen2 tag memory to a URN of the form urn:epc:raw:N.A.V.
	 * 
	 * @param epcBitString
	 *            the bit string to convert
	 * @param pcbits
	 *            a pcbits object (16 bits)
	 * @return a URN of the form urn:epc:raw:N.A.V
	 * @throws IllegalArgumentException
	 *             if one of the arguments is null
	 * @throws CannotConvertEPCException
	 *             if toggle bit is 0
	 */
	public String toRawFromG2Memory(String epcBitString, PCBits pcbits)
			throws CannotConvertEPCException {
		if (null == epcBitString || null == pcbits) {
			throw new IllegalArgumentException("arguments cannot be null");
		}
		if (pcbits.getToggleBit() && EPC_Utilities.isBits(epcBitString)) {
			// perform step 5-8 of procedure 5.4 in TDS 1.4
			int N = pcbits.getLength();
			int A = pcbits.getAFIBits().intValue();
			String A_hex = Integer.toHexString(A).toUpperCase();

			int minBits = ConvertingUtil.roundUpDivision(N, 4);
			String ID_hex = ConvertingUtil.toString(epcBitString, 2, 16,
					minBits).toUpperCase();

			return EPC_Utilities.EPC_RAW_HEADER + N + ".x" + A_hex + ".x"
					+ ID_hex;

		} else {
			throw new CannotConvertEPCException(
					"Cannot convert to urn:epc:raw:N.A.V format if toggle bit is 0");
		}
	}

	/**
	 * This method returns an epc in the form urn:epc:tag to a raw in the form
	 * urn:epc:raw. It is equivalent to a call to
	 * <code>toRawFromBitString(toBitStringFromTag(tag))</code>
	 * 
	 * @param epcTagURI
	 *            an epc in the form urn:epc:tag
	 * @param hexFormat
	 *            , if true, V will be in hex format, otherwise it is in decimal
	 * @return an epc in the format urn:epc:raw
	 * @throws IllegalArgumentException
	 *             if epcTagURI is null
	 * @throws CannotConvertEPCException
	 *             If there was a problem converting to raw
	 */
	public String toRawFromTag(String epcTagURI, boolean hexFormat)
			throws CannotConvertEPCException {
		if (null == epcTagURI) {
			throw new IllegalArgumentException("epcTagURI cannot be null");
		}
		String bits = toBitStringFromTag(epcTagURI);
		return toRawFromBitString(bits, hexFormat);
	}

	/**
	 * This method returns a bit string where the first 16 bits are the PCBits.
	 * 
	 * @param epcBitString
	 *            the EPC bits to include as the ID
	 * @return a BitString where the first 16 bits are PC Bits.
	 * @throws IllegalArgumentException
	 *             if epcBitString is null
	 */
	public String toG2MemoryFromBitString(String epcBitString) {
		if (null == epcBitString) {
			throw new IllegalArgumentException("epcBitString cannot be null");
		}
		return new PCBits(epcBitString.length()).toString() + epcBitString;
	}

	/**
	 * This method follows step 1 of procedure 5.6 to convert a raw uri of the
	 * form urn:epc:raw:N.A.V into a Gen 2 Tag Memory bit string, where the
	 * first 16 bits are the PC bits and the rest are the EPC bits
	 * 
	 * @param epcRawURI
	 *            a string of the form urn:epc:raw:N.A.V
	 * @return a bit string where the first 16 bits are PC bits
	 * @throws IllegalArgumentException
	 *             if epcRawURI is null
	 * @throws CannotConvertEPCException
	 *             if there was a problem converting to Gen 2 tag memory format
	 */
	public String toG2MemoryFromRaw(String epcRawURI)
			throws CannotConvertEPCException {
		if (null == epcRawURI) {
			throw new IllegalArgumentException("epcRawURI cannot be null");
		}
		if (EPC_Utilities.isAFIRaw(epcRawURI)) {

			String[] NAV = epcRawURI.split("\\.");

			// get N
			String N = NAV[0].split(":")[3];
			// get A
			String A = NAV[1].substring(1);
			// get V
			String V = NAV[2];

			Integer n = Integer.parseInt(N);
			int L = ConvertingUtil.roundUpDivision(n, 16);
			if (L >= 32) {
				throw new CannotConvertEPCException();
			}
			int afi = Integer.parseInt(A, 16);
			if (afi >= 256) {
				throw new CannotConvertEPCException();
			}
			BigInteger value = new BigInteger(V.substring(1), 16);

			if (value.bitLength() > n) {
				throw new CannotConvertEPCException();
			}
			String pcBits = ConvertingUtil.toString(Integer.toString(L), 10, 2,
					5)
					+ "00"
					+ "1"
					+ ConvertingUtil.toString(Integer.toString(afi), 10, 2, 8);
			return pcBits
					+ ConvertingUtil
							.toString(value.toString(2), 2, 2, (16 * L));

		} else if (EPC_Utilities.isHexRaw(epcRawURI)
				|| EPC_Utilities.isDecimalRaw(epcRawURI)) {
			String epcbits = toBitStringFromRaw(epcRawURI);
			return new PCBits(epcbits.length()).toString() + epcbits;
		}

		throw new CannotConvertEPCException(epcRawURI
				+ " is not in the correct format");
	}

	/**
	 * Convert an EPC in the Tag URI format (urn:epc:tag) to Gen 2 tag memory --
	 * that is a bit string where the first 16 bits are the PC bits. This method
	 * follows step 2 from procedure 5.6 in TDS 1.4
	 * 
	 * This call is equivalent to
	 * <code>toG2MemoryFromBits(toBitStringFromTag(tag))</code>
	 * 
	 * @param epcTagURI
	 *            an EPC in the form of urn:epc:tag
	 * @return a bit string where the first 16 bits are the PC bits, and the
	 *         rest are the epc bits
	 * @throws IllegalArgumentException
	 *             if epcTagURI is null
	 * @throws CannotConvertEPCException
	 *             if there was a problem converting to Gen2 Tag Memory
	 */
	public String toG2MemoryFromTag(String epcTagURI)
			throws CannotConvertEPCException {
		if (null == epcTagURI) {
			throw new IllegalArgumentException("epcTagURI cannot be null");
		}
		String bits = toBitStringFromTag(epcTagURI);
		return toG2MemoryFromBitString(bits);
	}

	/**
	 * A private helper method to convert an EPC into a given format
	 * 
	 * @param input
	 *            The epc to convert
	 * @param level
	 *            the format to convert it to
	 * @return an epc in the specified format
	 * @throws CannotConvertEPCException
	 *             If there was a problem converting the EPC or with the TDT
	 *             engine
	 */
	private String convert(String input, LevelTypeList level)
			throws CannotConvertEPCException {
		TDTEngine engine = null;
		try {
			engine = enginePool.borrowEngine();
			String retVal = engine.convert(input,
					new HashMap<String, String>(), level);
			enginePool.returnEngine(engine);
			return retVal;
		} catch (TDTException ex) {
			logger.error(ex);
			throw new CannotConvertEPCException();
		} catch (Exception e) {
			logger.error(e);
			throw new CannotConvertEPCException();
		} finally {
			try {
				if (engine != null) {
					enginePool.returnEngine(engine);
				}
			} catch (Exception e) {
			}
		}
	}

	@Inject
	public void setEnginePool(TDTEnginePool pool) {
		this.enginePool = pool;
	}

}

/*
 *  Bits_ALEDataType.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.ale.datatypes.builtin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.ale.datatypes.ALEDataType;
import org.rifidi.edge.ale.datatypes.builtin.formats.ALEFormat;
import org.rifidi.edge.ale.datatypes.builtin.formats.BitsFormats;
import org.rifidi.edge.tags.util.BitVector;
import org.rifidi.edge.tags.util.ConvertingUtil;

/**
 * This class represents the bits datatytpe fomat described in 6.2.3 of the ALE
 * 1.1 specification
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class Bits_ALEDataType implements ALEDataType {

	private static Log logger = LogFactory.getLog(Bits_ALEDataType.class);

	/**
	 * This is the regular expression that matches <code>HexBits</code> strings
	 * as described by the grammar in 6.2.3.2. This is used to match input from
	 * the ALE client
	 */
	private static String HEX_BITS = "[1-9][0-9]*:x[0-9 A-F a-f]+";

	private static Pattern regex = Pattern.compile(HEX_BITS);

	/**
	 * The bits for this datatype
	 */
	private BitVector _bits;

	/**
	 * Create a new Bits datatype from a <code>HexBits</code> string
	 * 
	 * @param bitsDTString
	 *            the incoming <code>HexBits</code> string
	 * @throws IllegalArgumentException
	 *             if bitsDTString is not in the correct format
	 */
	public Bits_ALEDataType(String bitsDTString) {
		_bits = parseInput(bitsDTString);
	}

	/**
	 * Create a new Bits datatype from a bit vector
	 * 
	 * @param bits
	 *            the bit vector
	 */
	public Bits_ALEDataType(BitVector bits) {
		_bits = bits;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.ale.datatypes.ALEDataType#getData(java.lang.String)
	 */
	@Override
	public String getData(ALEFormat format) {
		BitsFormats bitsFormat = Enum.valueOf(BitsFormats.class, format
				.getFormatName());

		switch (bitsFormat) {
		case HEX:
			return getInHexFormat();
		}
		logger.error("ALEFormat: " + format
				+ " is not a valid format for Bits_ALEDataType");
		throw new IllegalArgumentException("ALEFormat: " + format
				+ " is not a valid format for Bits_ALEDataType");
	}

	/**
	 * A helper method to return this data type in <code>HexBits</code> format
	 * 
	 * @return the string
	 */
	private String getInHexFormat() {
		int numBits = _bits.bitLength();
		int minDigits = ConvertingUtil.roundUpDivision(numBits, 4);
		String hex = _bits.toString(16, minDigits).toUpperCase();
		return numBits + ":x" + hex;
	}

	/**
	 * A helper method to parse a string in <code>HexBits</code> format into a
	 * BitVector
	 * 
	 * @param bitsDTString
	 *            the input
	 * @return a BitVector
	 * @throws IllegalArgumentException
	 *             if bitsDTString is in an invalid format
	 */
	private BitVector parseInput(String bitsDTString) {
		try {
			Matcher matcher = regex.matcher(bitsDTString);
			if (matcher.matches()) {
				String[] pieces = bitsDTString.split(":x");
				String numBits = pieces[0];
				String value = pieces[1];
				return new BitVector(value, 16, Integer.parseInt(numBits));
			}
		} catch (Exception e) {
			logger.error("Exception when converting " + bitsDTString, e);
		}
		throw new IllegalArgumentException(bitsDTString
				+ " is not in a valid format for the bits data type");
	}

}

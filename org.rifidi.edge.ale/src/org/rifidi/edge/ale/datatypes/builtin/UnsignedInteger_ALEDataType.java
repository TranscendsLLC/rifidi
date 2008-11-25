/*
 *  UnsignedInteger_ALEDataType.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.ale.datatypes.builtin;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.ale.datatypes.ALEDataType;
import org.rifidi.edge.ale.datatypes.builtin.formats.ALEFormat;
import org.rifidi.edge.ale.datatypes.builtin.formats.UIntFormats;
import org.rifidi.edge.ale.datatypes.builtin.patterns.uint.UintPattern;
import org.rifidi.edge.tags.util.BitVector;

/**
 * This class represents the Unsigned integer datatype (uint) as specified by
 * section 6.2.2 of the ALE 1.1 specification
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class UnsignedInteger_ALEDataType implements ALEDataType {

	private Log logger = LogFactory.getLog(UnsignedInteger_ALEDataType.class);

	/**
	 * Regular expression that matches either a hex string beginning with an 'x'
	 * or a decimal string
	 */
	private static String regex = "x[0-9 A-F a-f]+|[0-9]+";

	private static Pattern pattern = Pattern.compile(regex);

	/**
	 * Store this uint as a BigInteger because leading zeros do not matter
	 */
	private BigInteger _uint;

	/**
	 * Construct a new uint from a bit vector
	 * 
	 * @param bits
	 *            a bit vector
	 */
	public UnsignedInteger_ALEDataType(BitVector bits) {
		_uint = new BigInteger(bits.toString(2), 2);
	}

	/**
	 * Construct a new uint from either a hex or a decimal number, as described
	 * in section 6.2.2.2 of the ALE 1.1 specification
	 * 
	 * @param number
	 *            either a hex number (starts with a 'x' character) or a decimal
	 *            number
	 * @throws IllegalArgumentException
	 *             if the number is not either the hex or decimal formats
	 */
	public UnsignedInteger_ALEDataType(String number) {
		Matcher matcher = pattern.matcher(number);
		if (matcher.matches()) {
			if (number.charAt(0) == 'x') {
				_uint = new BigInteger(number.substring(1), 16);
			} else {
				_uint = new BigInteger(number, 10);
			}
		} else {
			throw new IllegalArgumentException(number
					+ " is not a valid hex or decimal number");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.ale.datatypes.ALEDataType#getData(java.lang.String)
	 */
	@Override
	public String getData(ALEFormat format) {
		UIntFormats uintFormat = Enum.valueOf(UIntFormats.class, format
				.getFormatName());
		switch (uintFormat) {
		case DECIMAL:
			return _uint.toString(10);
		case HEX:
			return "x" + _uint.toString(16).toUpperCase();
		}
		logger.error("ALEFormat: " + format
				+ " is not a valid format for UnsignedInteger_ALEDataType");
		throw new IllegalArgumentException("ALEFormat: " + format
				+ " is not a valid format for UnsignedInteger_ALEDataType");
	}

	/**
	 * 
	 * @param pattern
	 * @return
	 */
	public boolean matches(UintPattern pattern) {
		return pattern.matches(_uint);
	}

}

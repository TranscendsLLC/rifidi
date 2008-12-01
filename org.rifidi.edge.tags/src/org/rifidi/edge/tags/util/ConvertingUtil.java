/*
 *  ConvertingUtil.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.tags.util;

import java.math.BigInteger;

/**
 * This class provides some utility methods for converting between various data
 * types that are used for storing bits
 * 
 * @author kyle
 * 
 */
public class ConvertingUtil {

	/**
	 * This function converts a number from one base to another base and assures
	 * that the returning string has at least <code>minDigits</code> digits. If
	 * the value does not have that many digits, it will pad the needed number
	 * of 0's on the left.
	 * 
	 * @param val
	 *            The value to convert
	 * @param fromRadix
	 *            The radix val is currently in
	 * @param toRadix
	 *            the radix the return value should be in
	 * @param minDigits
	 *            the minimum number of digits the return value should have
	 * @return a string of the new number in <code>fromRadix</code> base and
	 *         with at least <code>minDigits</code> digits
	 */
	public static String toString(String val, int fromRadix, int toRadix,
			int minDigits) {
		BigInteger bi = new BigInteger(val, fromRadix);
		StringBuffer retString = new StringBuffer(bi.toString(toRadix));
		while (retString.length() < minDigits) {
			retString.insert(0, "0");
		}
		return retString.toString();
	}

	/**
	 * This method is equivalent to CEILING(dividend/divisor). It is needed
	 * because for this function to return correctly, it needs to be done with
	 * Doubles instead of Integers. It often helps when you need to calculate
	 * the minimum number of hexadecimal digits needed to represent a bit string
	 * (i.e. "the bit pattern shall be represented using N characters, where N
	 * is the length [of the bit string] divided by 4 and rounded up to the next
	 * higher integer, padding with zeros as necessary")
	 * 
	 * @param dividend
	 * @param divisor
	 * @return CEILING(dividend/divisor)
	 */
	public static int roundUpDivision(int dividend, int divisor) {
		Double _dividend = new Double(dividend);
		Double _divisor = new Double(divisor);
		Double _quotient = _dividend / _divisor;
		return new Double(Math.ceil(_quotient)).intValue();
	}

	/**
	 * This method returns a byte array given a string where each character in
	 * the string is a hex digit
	 * 
	 * @param hexString
	 *            a String where each character is a hex digit
	 * @return a byte array where each byte represents one character in the
	 *         string
	 */
	public static byte[] toBytes(String hexString) {
		byte[] bytes = new byte[hexString.length()];
		for(int i=0; i<hexString.length(); i++){
			Character c = hexString.charAt(i);
			int digit = Character.digit(c, 16);
			if(digit==-1){
				throw new IllegalArgumentException();
			}
			bytes[i] = (byte)digit;
		}
		return bytes;
	}

}

/*
 *  ByteAndHexConvertingUtility.java
 *
 *  Created:	Jun 19, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.common.utilities.converter;

public class ByteAndHexConvertingUtility {

	public static String toHexString(byte b) {
		StringBuffer sb = new StringBuffer();
		// look up high nibble char
		sb.append(hexChar[(b & 0xf0) >>> 4]);
		// look up low nibble char
		sb.append(hexChar[b & 0x0f]);
		return sb.toString();
	}

	/**
	 * From http://mindprod.com/jgloss/hex.html
	 * 
	 * @param b
	 * @return
	 */
	public static String toHexString(byte[] b) {
		StringBuffer sb = new StringBuffer(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(toHexString(b[i]));
			sb.append(" ");
		}
		return sb.toString();
	}

	// table to convert a nibble to a hex char.
	private static char[] hexChar = { '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	/**
	 * From http://mindprod.com/jgloss/hex.html
	 * 
	 * @param s
	 * @return
	 */
	public static byte[] fromHexString(String s)
			throws IllegalArgumentException {
		s = s.replaceAll(" ", "");
		int stringLength = s.length();
		if ((stringLength & 0x1) != 0) {
			throw new IllegalArgumentException(
					"Return value bad.  Check return value.");
		}
		byte[] b = new byte[stringLength / 2];

		for (int i = 0, j = 0; i < stringLength; i += 2, j++) {

			int high = charToNibble(s.charAt(i));
			int low = charToNibble(s.charAt(i + 1));
			b[j] = (byte) ((high << 4) | low);

		}
		return b;
	}

	private static int charToNibble(char c) {
		int nibble = correspondingNibble[c];
		if (nibble < 0) {
			throw new IllegalArgumentException("Invalid hex character: " + c);
		}
		return nibble;
	}

	private static byte[] correspondingNibble = new byte['f' + 1];

	static {
		// only 0..9 A..F a..f have meaning. rest are errors.
		for (int i = 0; i <= 'f'; i++) {
			correspondingNibble[i] = -1;
		}
		for (int i = '0'; i <= '9'; i++) {
			correspondingNibble[i] = (byte) (i - '0');
		}
		for (int i = 'A'; i <= 'F'; i++) {
			correspondingNibble[i] = (byte) (i - 'A' + 10);
		}
		for (int i = 'a'; i <= 'f'; i++) {
			correspondingNibble[i] = (byte) (i - 'a' + 10);
		}
	}

	/**
	 * This method converts a 32-bit integer to a byte array, MSB first. It
	 * drops the leading zeros in the int, unless the minSize is greater than
	 * the number of leading non-zero bytes
	 * 
	 * @param num
	 *            The integer to convert to a byte array
	 * @param minSize
	 *            The minimum number of bytes to return. Valid values = 1 -4
	 * @return A byte array containing the bytes in the int, MSB first
	 */
	public static byte[] intToByteArray(int num, int minSize) {

		/* 0< minSize <5 */
		minSize = Math.max(minSize, 1);
		minSize = Math.min(minSize, 4);

		// Calculate number bytes in num that are not leading zeros

		int tempNum = num < 0 ? ~num : num;
		int numbits = 32 - Integer.numberOfLeadingZeros(tempNum);
		int numOfIncomingBytes = 0;
		if (1 <= numbits && numbits <= 8)
			numOfIncomingBytes = 1;
		else if (9 <= numbits && numbits <= 16)
			numOfIncomingBytes = 2;
		else if (17 <= numbits && numbits <= 24)
			numOfIncomingBytes = 3;
		else if (25 <= numbits && numbits <= 32)
			numOfIncomingBytes = 4;

		int numOfOutgoingBytes = Math.max(numOfIncomingBytes, minSize);

		/* We need at least one thing in the array */
		numOfOutgoingBytes = Math.max(numOfOutgoingBytes, 1);

		byte[] byteArray = new byte[numOfOutgoingBytes];

		for (int n = 0; n < numOfIncomingBytes; n++) {
			byteArray[numOfOutgoingBytes - n - 1] = (byte) (num >>> (n * 8));
		}
		/* If minsize is greater than numOfIncomingByes, prepend zeros */
		if (minSize > numOfIncomingBytes) {
			int numZerosToPrepend = minSize - numOfIncomingBytes;
			for (int i = 0; i < numZerosToPrepend; i++) {
				byteArray[i] = 0x00;
			}
		}

		return byteArray;
	}

	/**
	 * Convert 32-bit integer to byte array, MSB first. This method will drop
	 * the leading zeros
	 * 
	 * @param num
	 *            The int to convert
	 * @return a byte array containing the bytes of the int, MSB first
	 */
	public static byte[] intToByteArray(int num) {
		return intToByteArray(num, 1);
	}

	/**
	 * From http://www.rgagnon.com/javadetails/java-0026.html
	 * 
	 * @param b
	 * @return
	 */
	public static int unsignedByteToInt(byte b) {
		return (int) b & 0xFF;
	}

	/**
	 * From http://www.rgagnon.com/javadetails/java-0026.html
	 * 
	 * @param b
	 * @return
	 */
	public static int unsignedByteToInt(byte b1, byte b2, byte b3, byte b4) {
		int i = (unsignedByteToInt(b1) << 24) + (unsignedByteToInt(b2) << 16)
				+ (unsignedByteToInt(b3) << 8) + unsignedByteToInt(b4);
		return i;
	}

}

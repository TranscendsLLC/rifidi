/*
 * 12/12/2015
 * The original work can be found in
 * https://github.com/Fosstrak/fosstrak-fc/blob/master/fc-server/src/main/java/org/fosstrak/ale/server
 * 
 */

package org.rifidi.edge.server.epcglobal.ale;

public final class TagFormatHelper {

	/**
	 * private utility class.
	 */
	private TagFormatHelper() {
	}

	/**
	 * creates a raw epc-decimal representation of a given tag.
	 * @param bitStringLength the length of the bit-string (binary representation).
	 * @param dec the decimal representation of the tag.
	 * @return the formatted epc-decimal representation.
	 */
	public static String formatAsRawDecimal(int bitStringLength, String dec) {
		return String.format("urn:epc:raw:%d.%s", bitStringLength, dec);
	}
	
	/**
	 * creates a epc-hex representation of a given tag.
	 * @param bitStringLength the length of the bit-string (binary representation).
	 * @param hex the hex representation of the tag.
	 * @return the formatted epc-hex representation.
	 */
	public static String formatAsRawHex(int bitStringLength, String hex) {
		return String.format("urn:epc:raw:%d.x%s", bitStringLength, hex);
	}

}
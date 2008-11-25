/*
 *  EPC_Utilities.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.tags.encodings.epc.util;

import java.util.regex.Pattern;

import org.rifidi.edge.tags.util.ConvertingUtil;

/**
 * @author kyle
 * 
 */
public class EPC_Utilities {

	/**
	 * The header for EPC Tag format
	 */
	public static final String EPC_TAG_HEADER = "urn:epc:tag:";

	/**
	 * The header for EPC RAW format
	 */
	public static final String EPC_RAW_HEADER = "urn:epc:raw:";

	/**
	 * This method converts a string of the form urn:epc:raw:N.V where V is a
	 * hex number to the same string where V is a decimal numner
	 * 
	 * @param raw_hex
	 *            a string of the form urn:epc:raw:N.V where V is xHEX_NUMBER
	 * @return a string of the form urn:epc:raw:N.V where V is DECIMAL_NUMBER
	 */
	public static String toDecimalRawFromHexRaw(String raw_hex) {
		String[] rawpieces = raw_hex.split(":");
		String[] numbers = rawpieces[3].split("\\.");
		String decimal = ConvertingUtil.toString(numbers[1].substring(1), 16,
				10, 0);
		return EPC_RAW_HEADER + numbers[0] + "." + decimal;
	}

	/**
	 * 
	 * @param epc
	 *            the epc to test
	 * @return true if the epc is in the bits format
	 */
	public static boolean isBits(String epc) {
		// A string with at least a one and a zero
		Pattern pattern = Pattern.compile("[1,0]+");
		return pattern.matcher(epc).matches();
	}

	/**
	 * 
	 * @param epc
	 *            the epc to test
	 * @return true if the epc is in the tag format
	 */
	public static boolean isTag(String epc) {
		return epc.substring(0, EPC_TAG_HEADER.length()).equals(EPC_TAG_HEADER);
	}

	/**
	 * Tests to see if if EPC is in the form epc:urn:raw:N.A.V See grammar
	 * 4.3.11 in TDS 1.1
	 * 
	 * @param epcURI
	 *            - EPC in raw from
	 * @return true if EPC has AFIRawURIBody, false otherwise.
	 */
	public static boolean isAFIRaw(String epcURI) {
		String nonZero = "[1-9][0-9]*";
		String dot = "\\.";
		String x = "x";
		String hexComponent = "[0-9 A-F]+";

		// example: urn:epc:raw:9.xAF.xA43
		String afiForm = EPC_RAW_HEADER + nonZero + dot + x + hexComponent
				+ dot + x + hexComponent;
		return Pattern.matches(afiForm, epcURI);
	}

	/**
	 * Tests to see if if epc is in the form epc:urn:raw:N.V, where V is a hex
	 * number. See grammar in 4.3.11 in TDS 1.1
	 * 
	 * @param epcURI
	 *            - EPC in raw from
	 * @return true if EPC has HexRawURIBody form, false otherwise.
	 */
	public static boolean isHexRaw(String epcURI) {
		String nonZero = "[1-9][0-9]*";
		String dot = "\\.";
		String x = "x";
		String hexComponent = "[0-9 A-F]+";

		// example: urn:epc:raw:93.xAF2
		String hexForm = EPC_RAW_HEADER + nonZero + dot + x + hexComponent;
		return Pattern.matches(hexForm, epcURI);
	}

	/**
	 * Tests to see if if epc is in the form epc:urn:raw:N.V, where V is a
	 * decimal number. See grammar in 4.3.11 in TDS 1.1
	 * 
	 * @param epcURI
	 *            - EPC in raw from
	 * @return true if EPC has DecimalRawURIBody form, false otherwise.
	 */
	public static boolean isDecimalRaw(String epcURI) {
		String nonZero = "[1-9][0-9]*";
		String dot = "\\.";
		String numericComponent = "(0|" + nonZero + ")";
		// example: urn:epc:raw:92.945
		String decimalForm = EPC_RAW_HEADER + nonZero + dot + numericComponent;
		return Pattern.matches(decimalForm, epcURI);
	}

}

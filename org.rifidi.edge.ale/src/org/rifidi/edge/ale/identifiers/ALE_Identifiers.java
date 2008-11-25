/*
 *  ALE_Identifiers.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.ale.identifiers;

/**
 * @author kyle
 *
 */
public class ALE_Identifiers {
	
	public static String FIELDNAME_EPC = "epc";
	public static String FIELDNAME_KILLPWD = "killPwd";
	public static String FIELDNAME_ACCESSPWD = "accessPwd";
	public static String FIELDNAME_EPCBANK = "epcBank";
	public static String FIELDNAME_TIDBANK = "tidBank";
	public static String FIELDNAME_USERBANK = "userBank";
	public static String FIELDNAME_AFI = "afi";
	public static String FIELDNAME_NSI = "nsi";
	
	public static String DATATYPE_EPC = "epc";
	public static String DATATYPE_UINT = "uint";
	public static String DATATYPE_BIS = "bits";
	public static String DATATYPE_STRING = "iso-15962-string";
	
	public static String FORMAT_EPC_PURE = "epc-pure";
	public static String FORMAT_EPC_TAG = "epc-tag";
	public static String FORMAT_EPC_HEX = "epc-hex";
	public static String FORMAT_EPC_DECIMAL = "epc-decimal";
	
	public static String FORMAT_UINT_HEX = "hex";
	public static String FORMAT_UINT_DECIMAL = "decimal";
	
	public static String FORMAT_BITS_HEX = "hex";
	
	public static String FORMAT_STRING_STRING = "string";
}

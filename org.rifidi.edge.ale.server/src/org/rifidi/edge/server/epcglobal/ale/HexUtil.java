/*
 * 12/12/2015
 * The original work can be found in
 * https://github.com/Fosstrak/fosstrak-fc/blob/master/fc-server/src/main/java/org/fosstrak/ale/server
 * 
 */

package org.rifidi.edge.server.epcglobal.ale;

import java.math.BigInteger;

public class HexUtil {

	/**
	 * This method converts a byte array into a hexadecimal string.
	 * 
	 * @param byteArray to convert
	 * @return hexadecimal string
	 */
	public static String byteArrayToHexString(byte[] byteArray) {
		if (null == byteArray) {
			return "";
		}
		return new BigInteger(byteArray).toString(16).toUpperCase();
				
	}
	
	/**
	 * This method converts a hexadecimal string into a byte array.
	 * 
	 * @param hexString to convert
	 * @return byte array
	 */
	public static byte[] hexStringToByteArray(String hexString) {
		
		return new BigInteger(hexString, 16).toByteArray();
		
	}
	
}

/*
 *  SSCC96.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package tests.encodings;

import org.junit.Test;

/**
 * @author kyle
 *
 */
public class SSCC96 extends AbstractEncodingTest {

	private static String BITS = "00110001" + "000" + "000" + "0100010001001010101110011011000110001011" + "000010011000101110" + "000000000000000000000000"; 
	private static String TAG = "urn:epc:tag:sscc-96:0.293311459723.09774";
	private static String PURE = "urn:epc:id:sscc:293311459723.09774";
	private static String HEX = "urn:epc:raw:96.x3101112AE6C62C262E000000";
	
	/**
	 * @param bits
	 * @param raw
	 * @param pure
	 * @param tag
	 */
	public SSCC96() {
		super(BITS, HEX, PURE, TAG);
	}
	
	@Test
	public void testFromBits() {
		super.testFromBits();
	}

	@Test
	public void testFromTag() {
		super.testFromTag();
	}

	@Test
	public void testFromRaw() {
		super.testFromRaw();
	}
	
	@Test
	public void testFromGen2(){
		super.testFromG2TagMem();
	}

}

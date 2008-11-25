/*
 *  GRAI96Test.java
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
 * This class tests the GRAI-96 encoding. Note that it has been discovered that
 * when the partition value =0, there is an error because TDT encodes the string
 * with a digit in the assetType spot (when there should be 0 digitis)
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class GRAI96Test extends AbstractEncodingTest {
	private static String BITS = "00110011" + "000" + "001"
			+ "0110011001101110101011011001000011111" + "0000010"
			+ "10100010100101100111001010000010100001";
	private static String PURE = "urn:epc:id:grai:54992941599.2.174577197217";
	private static String TAG = "urn:epc:tag:grai-96:0.54992941599.2.174577197217";
	private static String HEX = "urn:epc:raw:96.x330599BAB643E0A8A59CA0A1";

	/**
	 * @param bits
	 * @param raw
	 * @param pure
	 * @param tag
	 */
	public GRAI96Test() {
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

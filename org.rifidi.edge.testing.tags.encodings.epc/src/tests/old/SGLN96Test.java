/*
 *  SGLN96Test.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package tests.old;

import org.junit.Test;

/**
 * @author kyle
 *
 */
public class SGLN96Test extends AbstractEncodingTest{
	
	private static String BITS = "00110010" + "000" + "001" + "1001100001101011101011110100110011101" + "0001" + "01000011111001110101001100100010000111101";
	private static String PURE = "urn:epc:id:sgln:81830209949.1.583287587901";
	private static String TAG = "urn:epc:tag:sgln-96:0.81830209949.1.583287587901";
	private static String HEX = "urn:epc:raw:96.x320661AEBD33A287CEA6443D";

	public SGLN96Test() {
		super(BITS, HEX, PURE, TAG);
		// TODO Auto-generated constructor stub
	}
	
	@Test
	public void testFromBits() {
		super.testFromBits();
	}

	@Test
	public void testFromPure() {
		super.testFromPure();
	}

	@Test
	public void testFromTag() {
		super.testFromTag();
	}

	@Test
	public void testFromRaw() {
		super.testFromRaw();
	}

}

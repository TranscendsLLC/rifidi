/*
 *  EPCDataTypeTest.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package tests.datatypes;

import junit.framework.Assert;

import org.junit.Test;
import org.rifidi.edge.ale.datatypes.builtin.EPC_ALEDataType;
import org.rifidi.edge.ale.datatypes.builtin.formats.EPCFormats;
import org.rifidi.edge.tags.encodings.epc.data.PCBits;
import org.rifidi.edge.tags.encodings.epc.util.EPC_Utilities;
import org.rifidi.edge.tags.util.BitVector;

/**
 * @author kyle
 * 
 */
public class EPCDataTypeTest {

	public static String BITS = "001100000000100000110011101100101101110111011001000000010100000000110101000001010000000000000000";
	public static String PURE = "urn:epc:id:sgtin:0867360217.005.889520128";
	public static String TAG = "urn:epc:tag:sgtin-96:0.0867360217.005.889520128";
	public static String RAW = "urn:epc:raw:96.x300833B2DDD9014035050000";

	@Test
	public void testToPure() {

		// test if no pcBits and valid EPC
		BitVector bits = new BitVector(BITS, 2);
		EPC_ALEDataType epc = new EPC_ALEDataType(bits);
		Assert.assertEquals(PURE, epc.getData(EPCFormats.EPC_PURE));

		// test if no pcBits and invalid EPC
		String invalidEPC = "FFFF";
		BitVector invalidBits = new BitVector(invalidEPC, 16);
		String invalidRaw = "urn:epc:raw:" + invalidBits.bitLength() + ".x"
				+ invalidEPC;
		epc = new EPC_ALEDataType(invalidBits);
		Assert.assertEquals(invalidRaw, epc.getData(EPCFormats.EPC_PURE));

		// test PCBits(toggle=0) and valid EPC
		String extraBits = "11010101001010100010000";
		BitVector pcBitVector = new BitVector(new PCBits(BITS.length())
				.toString(), 2);
		epc = new EPC_ALEDataType(new BitVector(BITS + extraBits, 2),
				pcBitVector);

		// make sure extraBits was not read
		Assert.assertEquals(PURE, epc.getData(EPCFormats.EPC_PURE));

		// test PCBits(toggle=1) and invalid EPC
		PCBits pc = new PCBits("0000100111111111");
		pcBitVector = new BitVector(pc.toString(), 2);
		String N = Integer.toString(pc.getLength());
		String A = pc.getAFIBits().toString(16).toUpperCase();
		String V = invalidBits.toString(16).toUpperCase();

		String returnRaw = "urn:epc:raw:" + N + ".x" + A + ".x" + V;
		epc = new EPC_ALEDataType(invalidBits, pcBitVector);
		Assert.assertEquals(returnRaw, epc.getData(EPCFormats.EPC_PURE));

	}

	@Test
	public void testToTag() {

		// test if no pcBits and valid EPC
		BitVector bits = new BitVector(BITS, 2);
		EPC_ALEDataType epc = new EPC_ALEDataType(bits);
		Assert.assertEquals(TAG, epc.getData(EPCFormats.EPC_TAG));

		// test if no pcBits and invalid EPC
		String invalidEPC = "FFFF";
		BitVector invalidBits = new BitVector(invalidEPC, 16);
		String invalidRaw = "urn:epc:raw:" + invalidBits.bitLength() + ".x"
				+ invalidEPC;
		epc = new EPC_ALEDataType(invalidBits);
		Assert.assertEquals(invalidRaw, epc.getData(EPCFormats.EPC_TAG));

		// test PCBits(toggle=0) and valid EPC
		String extraBits = "11010101001010100010000";
		BitVector pcBitVector = new BitVector(new PCBits(BITS.length())
				.toString(), 2);
		epc = new EPC_ALEDataType(new BitVector(BITS + extraBits, 2),
				pcBitVector);

		// make sure extraBits was not read
		Assert.assertEquals(TAG, epc.getData(EPCFormats.EPC_TAG));

		// test PCBits(toggle=1) and invalid EPC
		PCBits pc = new PCBits("0000100111111111");
		pcBitVector = new BitVector(pc.toString(), 2);
		String N = Integer.toString(pc.getLength());
		String A = pc.getAFIBits().toString(16).toUpperCase();
		String V = invalidBits.toString(16).toUpperCase();

		String returnRaw = "urn:epc:raw:" + N + ".x" + A + ".x" + V;
		epc = new EPC_ALEDataType(invalidBits, pcBitVector);
		Assert.assertEquals(returnRaw, epc.getData(EPCFormats.EPC_TAG));

	}

	@Test
	public void testToRawHex() {

		// test if no pcBits and valid EPC
		BitVector bits = new BitVector(BITS, 2);
		EPC_ALEDataType epc = new EPC_ALEDataType(bits);
		Assert.assertEquals(RAW, epc.getData(EPCFormats.EPC_HEX));

		// test PCBits(toggle=1) and invalid EPC
		String invalidEPC = "FFFF";
		BitVector invalidBits = new BitVector(invalidEPC, 16);
		PCBits pc = new PCBits("0000100111111111");
		BitVector pcBitVector = new BitVector(pc.toString(), 2);
		String N = Integer.toString(pc.getLength());
		String A = pc.getAFIBits().toString(16).toUpperCase();
		String V = invalidBits.toString(16).toUpperCase();

		String returnRaw = "urn:epc:raw:" + N + ".x" + A + ".x" + V;
		epc = new EPC_ALEDataType(invalidBits, pcBitVector);
		Assert.assertEquals(returnRaw, epc.getData(EPCFormats.EPC_HEX));

		// test PCBits(toggle=0) and valid EPC
		bits = new BitVector(BITS, 2);
		pcBitVector = new BitVector(new PCBits(BITS.length())
				.toString(), 2);
		epc = new EPC_ALEDataType(bits, pcBitVector);
		Assert.assertEquals(RAW, epc.getData(EPCFormats.EPC_HEX));

	}
	
	@Test
	public void testToRawDec() {

		String EPC_DEC = EPC_Utilities.toDecimalRawFromHexRaw(RAW);
		// test if no pcBits and valid EPC
		BitVector bits = new BitVector(BITS, 2);
		EPC_ALEDataType epc = new EPC_ALEDataType(bits);
		Assert.assertEquals(EPC_DEC, epc.getData(EPCFormats.EPC_DECIMAL));

		// test PCBits(toggle=1) and invalid EPC
		String invalidEPC = "FFFF";
		BitVector invalidBits = new BitVector(invalidEPC, 16);
		PCBits pc = new PCBits("0000100111111111");
		BitVector pcBitVector = new BitVector(pc.toString(), 2);
		String N = Integer.toString(pc.getLength());
		String A = pc.getAFIBits().toString(16).toUpperCase();
		String V = invalidBits.toString(16).toUpperCase();

		String returnRaw = "urn:epc:raw:" + N + ".x" + A + ".x" + V;
		epc = new EPC_ALEDataType(invalidBits, pcBitVector);
		Assert.assertEquals(returnRaw, epc.getData(EPCFormats.EPC_DECIMAL));

		// test PCBits(toggle=0) and valid EPC
		bits = new BitVector(BITS, 2);
		pcBitVector = new BitVector(new PCBits(BITS.length())
				.toString(), 2);
		epc = new EPC_ALEDataType(bits, pcBitVector);
		Assert.assertEquals(EPC_DEC, epc.getData(EPCFormats.EPC_DECIMAL));

	}

}

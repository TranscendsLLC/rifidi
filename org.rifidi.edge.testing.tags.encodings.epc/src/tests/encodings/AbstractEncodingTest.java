/*
 *  AbstractEncodingTest.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package tests.encodings;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.rifidi.edge.tags.encodings.epc.data.EPCFactory;
import org.rifidi.edge.tags.encodings.epc.data.PCBits;
import org.rifidi.edge.tags.encodings.epc.exceptions.CannotConvertEPCException;
import org.rifidi.edge.tags.encodings.epc.util.EPC_Utilities;

/**
 * 
 * This method serves as an abstract base class for junits that test conversions
 * of a particular EPC encoding scheme, such as SGTIN-96. To use it call the
 * constructor and supply four strings in each of the four formats (bits, pure,
 * tag, and raw) that are for the same tag.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class AbstractEncodingTest {

	private static Log logger = LogFactory.getLog(AbstractEncodingTest.class);

	/**
	 * The bits representation for this tag
	 */
	private String BITS;

	/**
	 * The raw representation for this tag where V is a hex number
	 */
	private String RAW_HEX;

	/**
	 * The raw representation for this tag where V is a decimal number
	 */
	private String RAW_DEC;

	/**
	 * The Pure URI representation for this tag
	 */
	private String PURE;

	/**
	 * The Tag URI representation for this tag
	 */
	private String TAG;

	/**
	 * The constructor for the tests for this encoding type. Supply the four
	 * representations for a tag encoded in the scheme
	 * 
	 * @param bits
	 *            the tag in bits format
	 * @param raw
	 *            the tag in raw format
	 * @param pure
	 *            the tag in pure URI format
	 * @param tag
	 *            the tag in tag URI format
	 */
	public AbstractEncodingTest(String bits, String raw, String pure, String tag) {
		BITS = bits;
		RAW_HEX = raw;
		PURE = pure;
		TAG = tag;
		RAW_DEC = EPC_Utilities.toDecimalRawFromHexRaw(RAW_HEX);
	}

	/**
	 * Test the conversions from BITS->PURE, BITS->TAG, BITS->RAW, and
	 * BITS->GEN2TagMem
	 */
	public void testFromBits() {
		EPCFactory factory = new EPCFactory();
		try {
			Assert.assertEquals(PURE, factory.toPureFromBitString(BITS));
		} catch (CannotConvertEPCException e) {
			logger.error("Error BITS->PURE", e);
			Assert.fail();
		}
		try {
			Assert.assertEquals(TAG, factory.toTagFromBitString(BITS));
		} catch (CannotConvertEPCException e) {
			logger.error("Error BITS->TAG", e);
			Assert.fail();
		}

		Assert.assertEquals(RAW_HEX, factory.toRawFromBitString(BITS, true));
		Assert.assertEquals(RAW_DEC, factory.toRawFromBitString(BITS, false));

		Assert.assertEquals(new PCBits(BITS.length()).toString() + BITS,
				factory.toG2MemoryFromBitString(BITS));

	}

	/**
	 * Test the conversion from TAG->BITS, TAG->PURE, TAG->RAW, TAG->GEN2TagMem
	 */
	public void testFromTag() {
		EPCFactory factory = new EPCFactory();
		try {
			Assert.assertEquals(PURE, factory.toPureFromTag(TAG));
		} catch (CannotConvertEPCException e) {
			logger.error("Error TAG->PURE", e);
			Assert.fail();
		}
		try {
			Assert.assertEquals(BITS, factory.toBitStringFromTag(TAG));
		} catch (CannotConvertEPCException e) {
			logger.error("Error TAG->BITS", e);
			Assert.fail();
		}

		try {
			Assert.assertEquals(RAW_HEX, factory.toRawFromTag(TAG, true));
		} catch (CannotConvertEPCException e) {
			logger.error("Error TAG->RAW", e);
			Assert.fail();
		}
		try {
			Assert.assertEquals(RAW_DEC, factory.toRawFromTag(TAG, false));
		} catch (CannotConvertEPCException e) {
			logger.error("Error TAG->RAW", e);
			Assert.fail();
		}

		try {
			Assert.assertEquals(new PCBits(BITS.length()).toString() + BITS,
					factory.toG2MemoryFromTag(TAG));
		} catch (CannotConvertEPCException e) {
			logger.error("Error TAG->GEN2TagMem", e);
			Assert.fail();
		}
	}

	/**
	 * Test conversion from RAW->RAW. Make sure RAW->BITS, RAW->TAG, and
	 * RAW->PURE fail
	 */
	public void testFromRaw() {
		EPCFactory factory = new EPCFactory();
		try {
			Assert.assertEquals(BITS, factory.toBitStringFromRaw(RAW_HEX));
		} catch (CannotConvertEPCException e) {
			logger.error("Error RAW->BITS", e);
			Assert.fail();
		}

		try {
			Assert.assertEquals(BITS, factory.toBitStringFromRaw(RAW_DEC));
		} catch (CannotConvertEPCException e) {
			logger.error("Error RAW->BITS", e);
			Assert.fail();
		}

	}

	public void testFromG2TagMem() {
		EPCFactory factory = new EPCFactory();
		String pcbits = new PCBits(BITS.length()).toString();

		try {
			Assert.assertEquals(PURE, factory.toPureFromG2Memory(BITS, pcbits));
		} catch (CannotConvertEPCException e) {
			logger.error("Error Gen2TagMem->PURE");
			Assert.fail();
		}

		// This test should fail because toRawFromG2Memory only works if the
		// toggle bit is set
		boolean cannotConvertToRAW = false;
		try {
			Assert.assertEquals(RAW_HEX, factory
					.toRawFromG2Memory(BITS, pcbits));
		} catch (CannotConvertEPCException e) {
			cannotConvertToRAW = true;
		}
		Assert.assertTrue(cannotConvertToRAW);

		try {
			Assert.assertEquals(TAG, factory.toTagFromG2Memory(BITS, pcbits));
		} catch (CannotConvertEPCException e) {
			logger.error("Error Gen2TagMem->TAG", e);
			Assert.fail();
		}
	}
}

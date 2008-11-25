/*
 *  FieldLengthBug.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package tests.bugs;

import java.util.HashMap;
import java.util.regex.Pattern;

import org.fosstrak.tdt.exported.LevelTypeList;
import org.fosstrak.tdt.exported.TDTEngine;
import org.fosstrak.tdt.exported.TDTException;
import org.junit.Assert;
import org.junit.Test;

/**
 * There is a known bug in the TDT library where encoding types where a certain
 * field has a decimal length of 0 if the partition = 0 are not encoded
 * correctly. For example, with the GRAI-96 encoding, when the partition value
 * is 0, then the asset type has a decimal length of 0. However, when the binary
 * string is translated into the tag encoding, the asset_type still occupies at
 * least one decimal digit.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class FieldLengthBug {

	public static String HEADER = "00110011";
	public static String FILTER = "000";
	public static String PARTITION = "000";
	public static String CO_PREFIX = "0000000100100011010001010110011110001001";
	public static String ASSET_TYPE = "1111";
	public static String SERIAL_NUM = "10100010100101100111001010000010100001";
	public static String GRAI96 = HEADER + FILTER + PARTITION + CO_PREFIX
			+ ASSET_TYPE + SERIAL_NUM;

	public static String pattern = "urn:epc:tag:grai-96:([0-7]{1})\\.([0-9]{12})\\.([0-9]{0})\\.([0-9]*)";

	@Test
	public void testFieldLengthBug() {
		try {
			TDTEngine engine = new TDTEngine();
			String tag = engine.convert(GRAI96, new HashMap<String, String>(),
					LevelTypeList.TAG_ENCODING);
			boolean matches = Pattern.matches(pattern, tag);
			Assert.assertTrue(matches);
		} catch (TDTException e) {
			e.printStackTrace();
			Assert.fail();
		}

	}

}

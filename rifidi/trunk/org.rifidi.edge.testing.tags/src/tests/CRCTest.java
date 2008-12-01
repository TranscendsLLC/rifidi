/*
 *  CRCTest.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package tests;

import junit.framework.Assert;

import org.junit.Test;
import org.rifidi.edge.tags.util.CRC16;
import org.rifidi.edge.tags.util.ConvertingUtil;

/**
 * @author kyle
 * 
 */
public class CRCTest {


	@Test
	public void TestCRC(){
		String hex = "3000300833B2DDD9014035050000";
		String crc = "42E7";
		
		int retVal = CRC16.calculateCRC(hex);
		String retValHex = ConvertingUtil.toString(Integer.toString(retVal), 10, 16, 4).toUpperCase();
		
		Assert.assertEquals(crc, retValHex);
	}
}

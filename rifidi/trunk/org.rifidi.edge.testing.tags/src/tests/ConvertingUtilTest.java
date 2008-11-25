/*
 *  ConvertingUitTest.java
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
import org.rifidi.edge.tags.util.ConvertingUtil;


/**
 * @author kyle
 *
 */
public class ConvertingUtilTest {
	
	@Test
	public void testToString(){
		String val = "0011";
		String answer = "03";
		String retVal = ConvertingUtil.toString(val, 2, 10, 2);
		Assert.assertEquals(answer, retVal);
		answer = ConvertingUtil.toString(val, 2, 16, 2);
		Assert.assertEquals(answer, retVal);
		
		val = "1111";
		answer = "015";
		retVal = ConvertingUtil.toString(val, 2, 10, 3);
		Assert.assertEquals(answer, retVal);
		
		answer = "00F";
		retVal = ConvertingUtil.toString(val, 2, 16, 3).toUpperCase();
		Assert.assertEquals(answer, retVal);
	}

}

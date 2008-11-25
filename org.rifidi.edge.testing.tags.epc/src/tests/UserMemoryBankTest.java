/*
 *  UserMemoryBankTest.java
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
import org.rifidi.edge.tags.epc.c1g2.C1G2UserBank;


/**
 * @author kyle
 *
 */
public class UserMemoryBankTest {
	
	public static final String MEMORY_BANK = "1010101001010100101010101";
	
	@Test
	public void testMemoryBank(){
		C1G2UserBank bank = new C1G2UserBank(MEMORY_BANK);
		Assert.assertEquals(MEMORY_BANK, bank.toString());
	}

}

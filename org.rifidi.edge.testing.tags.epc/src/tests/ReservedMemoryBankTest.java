/*
 *  ReservedMemoryBankTest.java
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
import org.rifidi.edge.tags.epc.c1g2.C1G2ReservedBank;


/**
 * @author kyle
 *
 */
public class ReservedMemoryBankTest {
	
	public static final String KILL_PWD = "10110011101100111011001110110011";
	public static final String ACCESS_PWD = "00010011000100110001001100010011";
	public static final String MEMORY_BANK = KILL_PWD + ACCESS_PWD;
	
	@Test
	public void testKillPwd(){
		C1G2ReservedBank bank = new C1G2ReservedBank(MEMORY_BANK);
		Assert.assertEquals(KILL_PWD, bank.getKillPwd().toString(2));
	}
	
	@Test
	public void testAccessPwd(){
		C1G2ReservedBank bank = new C1G2ReservedBank(MEMORY_BANK);
		Assert.assertEquals(ACCESS_PWD, bank.getAccessPwd().toString(2));
	}
	
	@Test
	public void testMemoryBank(){
		C1G2ReservedBank bank = new C1G2ReservedBank(MEMORY_BANK);
		Assert.assertEquals(MEMORY_BANK, bank.toString());
	}

}

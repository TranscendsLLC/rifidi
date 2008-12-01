/*
 *  AllTests.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package suite;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;
import tests.c1g1.C1G1EPCBankTest;
import tests.c1g1.C1G1PhysicalTagModelTest;
import tests.c1g2.C1G2PhysicalTagModelTest;
import tests.c1g2.EPCMemoryBankTest;
import tests.c1g2.EPCMemoryBankTestWithoutHeaderNSI;
import tests.c1g2.EPCMemoryBankWithoutHeaderTest;
import tests.c1g2.ReservedMemoryBankTest;
import tests.c1g2.TIDMemoryBankTest;
import tests.c1g2.UserMemoryBankTest;

/**
 * @author kyle
 *
 */
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for suite");
		//$JUnit-BEGIN$
		suite.addTest(new JUnit4TestAdapter(C1G1EPCBankTest.class));
		suite.addTest(new JUnit4TestAdapter(C1G1PhysicalTagModelTest.class));
		suite.addTest(new JUnit4TestAdapter(C1G2PhysicalTagModelTest.class));
		suite.addTest(new JUnit4TestAdapter(ReservedMemoryBankTest.class));
		suite.addTest(new JUnit4TestAdapter(EPCMemoryBankTest.class));
		suite.addTest(new JUnit4TestAdapter(EPCMemoryBankWithoutHeaderTest.class));
		suite.addTest(new JUnit4TestAdapter(EPCMemoryBankTestWithoutHeaderNSI.class));
		suite.addTest(new JUnit4TestAdapter(TIDMemoryBankTest.class));
		suite.addTest(new JUnit4TestAdapter(UserMemoryBankTest.class));
		//$JUnit-END$
		return suite;
	}

}

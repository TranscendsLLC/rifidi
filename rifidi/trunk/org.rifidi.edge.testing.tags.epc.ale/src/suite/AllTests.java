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

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author kyle
 *
 */
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for suite");
		//$JUnit-BEGIN$

		//$JUnit-END$
		return suite;
	}

}

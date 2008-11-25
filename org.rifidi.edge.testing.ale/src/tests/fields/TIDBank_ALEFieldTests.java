/*
 *  TIDBank_ALEField.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package tests.fields;

import org.junit.Test;
import org.rifidi.edge.ale.fields.ALEField;
import org.rifidi.edge.ale.fields.builtin.TIDBank_ALEField;
import org.rifidi.edge.tags.util.BitVector;

/**
 * @author kyle
 *
 */
public class TIDBank_ALEFieldTests extends AbstractUintDefaultFieldTests {

	/* (non-Javadoc)
	 * @see tests.fields.AbstractUintDefaultFieldTests#getField(org.rifidi.edge.tags.util.BitVector)
	 */
	@Override
	protected ALEField getField(BitVector bv) {
		return new TIDBank_ALEField(bv);
	}

	/* (non-Javadoc)
	 * @see tests.fields.AbstractUintDefaultFieldTests#testBits()
	 */
	@Override
	@Test
	public void testBits() {
		super.doTestBits();
		
	}

	/* (non-Javadoc)
	 * @see tests.fields.AbstractUintDefaultFieldTests#testDefault()
	 */
	@Override
	@Test
	public void testDefault() {
		super.doTestDefault();
		
	}

	/* (non-Javadoc)
	 * @see tests.fields.AbstractUintDefaultFieldTests#testEPC()
	 */
	@Override
	@Test
	public void testEPC() {
		super.doTestEPC();
		
	}

	/* (non-Javadoc)
	 * @see tests.fields.AbstractUintDefaultFieldTests#testString()
	 */
	@Override
	@Test
	public void testString() {
		super.doTestString();
		
	}

	/* (non-Javadoc)
	 * @see tests.fields.AbstractUintDefaultFieldTests#testUint()
	 */
	@Override
	@Test
	public void testUint() {
		super.doTestUint();
		
	}

}

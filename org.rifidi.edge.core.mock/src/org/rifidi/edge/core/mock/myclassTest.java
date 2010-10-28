package org.rifidi.edge.core.mock;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class myclassTest {

	@Test
	public final void testSetReadZone() {
		ReadZoneMock mymock = new ReadZoneMock("mymock");
		myclass myclass = new myclass();
		myclass.setReadZone(mymock);
		myclass.doSomethingIwantToTest();
		
		Iterator<String> i = mymock.mSetReader.iterator();
		assertEquals("my read", i.next());
		assertEquals("my read1", i.next());
		assertEquals("my read2", i.next());
		assertEquals(false, i.hasNext());
		
		Iterator<Boolean> j = mymock.mSetInclude.iterator();
		assertEquals(false, j.next());
		assertEquals(true, j.next());
		
		
		assertEquals(true, mymock.mCloneCalled);
	}

}

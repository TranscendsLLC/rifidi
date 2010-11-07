/**
 * 
 */
package org.rifidi.edge.core.test.app.api;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rifidi.edge.core.app.api.service.EsperUtil;

/**
 * @author manoj
 *
 */
public class EsperUtilTest {
	@Test
	public void test() {		
		assertEquals("50.0 sec", EsperUtil.timeUnitToEsperTime(50, TimeUnit.SECONDS));

		Float f = new Float(50.0);
		assertEquals(f, EsperUtil.esperTimetoTime("50"));

		assertEquals(TimeUnit.SECONDS, EsperUtil.esperTimetoTimeUnit("1.5 sec"));
	}
}

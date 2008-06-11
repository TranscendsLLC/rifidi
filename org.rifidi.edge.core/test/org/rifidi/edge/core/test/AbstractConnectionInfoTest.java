/**
 * 
 */
package org.rifidi.edge.core.test;

import org.junit.Assert;
import org.junit.Test;
import org.rifidi.edge.core.adapter.dummyadapter.DummyConnectionInfo;

/**
 * @author kyle
 *
 */
public class AbstractConnectionInfoTest {
	
	
	/**
	 * Uses DummyAbstractionConnectionInfo to test the getter and setter for IP
	 */
	@Test
	public void testSetGetIP(){
		DummyConnectionInfo info = new DummyConnectionInfo();
		info.setIPAddress("127.0.0.1");
		Assert.assertTrue(info.getIPAddress().equals("127.0.0.1"));
	}
	
	/**
	 * Uses DummyAbstractConnectionInfo to test the getter and setter for port
	 */
	@Test
	public void testsetgetPort(){
		DummyConnectionInfo info = new DummyConnectionInfo();
		info.setPort(8080);
		Assert.assertTrue(info.getPort() == 8080);
	}

}

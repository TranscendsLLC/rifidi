/**
 * 
 */
package org.rifidi.edge.core.junit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rifidi.edge.core.communication.service.impl.CommunicationServiceImpl;
import org.rifidi.edge.jms.service.impl.JMSServiceImpl;
import org.rifidi.edge.readerplugin.dummy.DummyReaderInfo;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * @author kyle
 *
 */
public class AbstractConnectionInfoTest {
	@Before
	public void setUp() throws Exception {
		ServiceRegistry.getInstance().service(this);
		
		System.out.println(CommunicationServiceImpl.class.getName());
		System.out.println(JMSServiceImpl.class.getName());
	}
	
	/**
	 * Uses DummyAbstractionConnectionInfo to test the getter and setter for IP
	 */
	@Test
	public void testSetGetIP(){
		DummyReaderInfo info = new DummyReaderInfo();
		info.setIPAddress("127.0.0.1");
		Assert.assertTrue(info.getIPAddress().equals("127.0.0.1"));
	}
	
	/**
	 * Uses DummyAbstractConnectionInfo to test the getter and setter for port
	 */
	@Test
	public void testsetgetPort(){
		DummyReaderInfo info = new DummyReaderInfo();
		info.setPort(8080);
		Assert.assertTrue(info.getPort() == 8080);
	}

}

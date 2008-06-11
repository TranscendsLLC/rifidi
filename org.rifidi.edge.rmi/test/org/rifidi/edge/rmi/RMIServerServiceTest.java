/**
 * 
 */
package org.rifidi.edge.rmi;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rifidi.edge.rmi.service.RMIServerService;
import org.rifidi.edge.rmi.service.impl.RMIServerServiceImpl;

/**
 * @author andreas
 * 
 */
public class RMIServerServiceTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testStartStopRMIServer() {
		RMIServerService rmiServer = new RMIServerServiceImpl();
		try {
			rmiServer.start();
		} catch (RemoteException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (AlreadyBoundException e) {
			e.printStackTrace();
			Assert.fail();
		}
		
		rmiServer.stop();
	}

}

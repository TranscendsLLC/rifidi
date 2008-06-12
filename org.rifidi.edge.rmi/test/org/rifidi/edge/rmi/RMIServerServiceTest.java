/**
 * 
 */
package org.rifidi.edge.rmi;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rifidi.edge.rmi.service.RMIServerService;
import org.rifidi.edge.rmi.session.RemoteSessionRegistry;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * @author andreas
 * 
 */
public class RMIServerServiceTest {

	private Log logger = LogFactory.getLog(RMIServerServiceTest.class);
	private RMIServerService rmiServerService;
	private RemoteSessionRegistry sessionRegistry;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		ServiceRegistry.getInstance().service(this);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void startRMIServer() {
		if(rmiServerService != null)
		{
			try {
				rmiServerService.start();
			} catch (RemoteException e) {
				Assert.fail();
				e.printStackTrace();
			} catch (AlreadyBoundException e) {
				Assert.fail();
				e.printStackTrace();
			}
		}else
		{
			Assert.fail();
		}
	}

	@Test
	public void getRemoteSessionRegistry() {
		// if (System.getSecurityManager() == null) {
		// System.setSecurityManager(new SecurityManager());
		// }
		try {
			Registry registry = LocateRegistry.getRegistry("127.0.0.1", 1099);
			logger.info("Getting RemoteSessionRegistry");
			sessionRegistry = (RemoteSessionRegistry) registry
					.lookup(RemoteSessionRegistry.class.getName());
			if (sessionRegistry == null) {
				Assert.fail();
			}
		} catch (RemoteException e) {
			Assert.fail("Cound not establish connection "
					+ "to RMI Registry on loaclhost  port 1099");
		} catch (NotBoundException e) {
			Assert.fail("Unable to lookup RemoteSessionRegistry via RMI");
		}

	}

	@Test
	public void createRemoteSession() {
		if (sessionRegistry == null) {
			Assert.fail();
		} else {
			// TODO there is no DummyAdapter to test this functionality
			Assert.fail();
		}
	}

	@Test
	public void stopRMIServer() {
		if (rmiServerService == null) {
			Assert.fail();
		} else {
			rmiServerService.stop();
		}

	}

	/**
	 * @param rmiServerService
	 */
	@Inject
	public void setRmiServerService(RMIServerService rmiServerService) {
		this.rmiServerService = rmiServerService;
	}
}

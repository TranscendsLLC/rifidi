/**
 * 
 */
package org.rifidi.edge.rmi.junit;

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
import org.rifidi.edge.core.readerPluginService.ReaderPluginRegistryService;
import org.rifidi.edge.readerplugin.dummy.DummyReaderInfo;
import org.rifidi.edge.readerplugin.dummy.DummyReaderPluginFactory;
import org.rifidi.edge.rmi.ReaderConnection.RemoteReaderConnection;
import org.rifidi.edge.rmi.ReaderConnection.RemoteReaderConnectionRegistry;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * @author andreas
 * 
 */
public class RMIServerServiceTest {

	private Log logger = LogFactory.getLog(RMIServerServiceTest.class);

	private RemoteReaderConnectionRegistry remoteReaderConnectionRegistry;

	private ReaderPluginRegistryService readerPluginRegistryService;

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
	public void getRemoteReaderConnectionRegistry() {
		// if (System.getSecurityManager() == null) {
		// System.setSecurityManager(new SecurityManager());
		// }
		try {
			Registry registry = LocateRegistry.getRegistry("127.0.0.1", 1099);
			logger.info("Getting RemoteReaderConnectionRegistry");
			remoteReaderConnectionRegistry = (RemoteReaderConnectionRegistry) registry
					.lookup(RemoteReaderConnectionRegistry.class.getName());
			if (remoteReaderConnectionRegistry == null) {
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
	public void createRemoteReaderConnection() {
		readerPluginRegistryService.registerReaderAdapter(
				DummyReaderInfo.class, new DummyReaderPluginFactory());

		getRemoteReaderConnectionRegistry();

		if (remoteReaderConnectionRegistry == null) {
			Assert.fail();
		} else {
			try {
				RemoteReaderConnection readerConnection = remoteReaderConnectionRegistry
						.createReaderConnection(new DummyReaderInfo());

				readerConnection.connect();

				readerConnection.startTagStream();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				readerConnection.stopTagStream();
				readerConnection.disconnect();
				remoteReaderConnectionRegistry
						.deleteReaderConnection(readerConnection);
			} catch (RemoteException e) {
				e.printStackTrace();
				Assert.fail();
			}
		}
	}

	@Inject
	public void setReaderPluginRegistryService(
			ReaderPluginRegistryService readerPluginRegistryService) {
		this.readerPluginRegistryService = readerPluginRegistryService;
	}

}

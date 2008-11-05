package org.rifidi.edge.adminclient.junit;

import java.io.StringWriter;
import java.io.Writer;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rifidi.edge.adminclient.testreaderthread.AbstractThread;
import org.rifidi.edge.core.exceptions.RifidiCommandInterruptedException;
import org.rifidi.edge.core.exceptions.RifidiCommandNotFoundException;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.exceptions.RifidiInvalidConfigurationException;
import org.rifidi.edge.core.exceptions.RifidiReaderInfoNotFoundException;
import org.rifidi.edge.core.rmi.readerconnection.ReaderSessionStub;
import org.rifidi.edge.core.rmi.readerconnection.EdgeServerStub;
import org.rifidi.edge.readerplugin.dummy.plugin.DummyReaderInfo;

/**
 * @author Jerry Maine - jerry@pramari.com
 * 
 */
public class TestClientTest {
	String hostname = "localhost";
	int port = 1099;
	BackgroundThread thread;

	@Before
	public void setUp() throws Exception {
		thread = new BackgroundThread("Background");
		thread.start();
	}

	@After
	public void tearDown() throws Exception {
		thread.stop();
	}

	@Test
	public void testClientTest() {

		Registry registry = null;

		EdgeServerStub remoteReaderConnectionRegistry = null;

		try {
			registry = LocateRegistry.getRegistry(hostname, port);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		try {
			remoteReaderConnectionRegistry = (EdgeServerStub) registry
					.lookup(EdgeServerStub.class.getName());
		} catch (AccessException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}

		for (int x = 0; x < 200; x++) {

			DummyReaderInfo dummyInfo1 = new DummyReaderInfo();
			dummyInfo1.setIpAddress("127.0.0.1");
			dummyInfo1.setPort(10000 + x);

			ReaderSessionStub remoteReader1 = null;
			try {
				JAXBContext context = JAXBContext.newInstance(dummyInfo1.getClass());
				Marshaller m = context.createMarshaller();
				Writer writer = new StringWriter();
				m.marshal(dummyInfo1, writer);
				
				remoteReader1 = remoteReaderConnectionRegistry
						.createReaderSession(writer.toString());
			} catch (RemoteException e) {
				e.printStackTrace();
				Assert.fail();
			} catch (JAXBException e) {
				e.printStackTrace();
				Assert.fail();
			} catch (RifidiReaderInfoNotFoundException e) {
				e.printStackTrace();
				Assert.fail();
			}

			try {
				remoteReader1.executeCommand("<TagStreamCommand/>");
			} catch (RemoteException e) {
				e.printStackTrace();
				Assert.fail();
			} catch (RifidiConnectionException e) {
				e.printStackTrace();
				Assert.fail();
			} catch (RifidiCommandInterruptedException e) {
				e.printStackTrace();
				Assert.fail();
			} catch (RifidiCommandNotFoundException e) {
				e.printStackTrace();
				Assert.fail();
			} catch (RifidiInvalidConfigurationException e) {
				e.printStackTrace();
				Assert.fail();
			}
		}
	}

	private class BackgroundThread extends AbstractThread {

		protected BackgroundThread(String name) {
			super(name);
		}

		@Override
		public void run() {
			System.out.println("Background thread started");
			Registry registry2 = null;
			EdgeServerStub remoteReaderConnectionRegistry2 = null;
			try {
				registry2 = LocateRegistry.getRegistry(hostname, port);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			try {
				remoteReaderConnectionRegistry2 = (EdgeServerStub) registry2
						.lookup(EdgeServerStub.class.getName());
			} catch (AccessException e) {
				e.printStackTrace();
				running = false;
			} catch (RemoteException e) {
				e.printStackTrace();
				running = false;
			} catch (NotBoundException e) {
				e.printStackTrace();
				running = false;
			}
			while (running) {
				// System.out.println("Getting reader connections");
				try {
					remoteReaderConnectionRegistry2.getAllReaderSessions();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// e.printStackTrace();
				}
			}
		}

	}
}

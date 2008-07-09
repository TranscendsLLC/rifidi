package org.rifidi.edge.adminclient.junit;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rifidi.edge.adminclient.testreaderthread.AbstractThread;
import org.rifidi.edge.readerplugin.dummy.DummyReaderInfo;
import org.rifidi.edge.rmi.ReaderConnection.RemoteReaderConnection;
import org.rifidi.edge.rmi.ReaderConnection.RemoteReaderConnectionRegistry;

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
		
		RemoteReaderConnectionRegistry remoteReaderConnectionRegistry = null;
		
		
		try {
			registry = LocateRegistry.getRegistry(hostname, port);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			remoteReaderConnectionRegistry = (RemoteReaderConnectionRegistry) registry
					.lookup(RemoteReaderConnectionRegistry.class.getName());
		} catch (AccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		for (int x = 0; x < 200; x++){
			
			DummyReaderInfo dummyInfo1 = new DummyReaderInfo();
			dummyInfo1.setIpAddress("127.0.0.1");
			dummyInfo1.setPort(10000 + x);
			
			RemoteReaderConnection remoteReader1 = null;
			try {
				remoteReader1 = remoteReaderConnectionRegistry.createReaderConnection(dummyInfo1);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Assert.fail();
			}
			
			try {
				remoteReader1.connect();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Assert.fail();
			}
			try {
				remoteReader1.startTagStream();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Assert.fail();
			}
		}
	}
	
	private class BackgroundThread extends AbstractThread {
		
		protected BackgroundThread(String name){
			super(name);
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			System.out.println("Background thread started");
			Registry registry2 = null;
			RemoteReaderConnectionRegistry remoteReaderConnectionRegistry2 = null;
			try {
				registry2 = LocateRegistry.getRegistry(hostname, port);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				remoteReaderConnectionRegistry2 = (RemoteReaderConnectionRegistry) registry2
						.lookup(RemoteReaderConnectionRegistry.class.getName());
			} catch (AccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				running = false;
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				running = false;
			} catch (NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				running = false;
			}
			while(running){
				//System.out.println("Getting reader connections");
				try {
					remoteReaderConnectionRegistry2.getAllReaderConnections();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					//e.printStackTrace();
				}
			}
		}
		
	}
}

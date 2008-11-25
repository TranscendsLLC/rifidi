import java.lang.reflect.InvocationTargetException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;


import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;
import org.rifidi.edge.rmi.ReaderConnection.RemoteReaderConnection;
import org.rifidi.edge.rmi.ReaderConnection.RemoteReaderConnectionRegistry;


public class TestRun implements Runnable {
	String hostname = "127.0.0.1";
	int port = 1099;
	int threadPortStart = 100;
	
	public void run() {
		

		Registry registry = null;
		
		RemoteReaderConnectionRegistry remoteReaderConnectionRegistry = null;
		
		
		try {
			registry = LocateRegistry.getRegistry(hostname, port);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		try {
			remoteReaderConnectionRegistry = (RemoteReaderConnectionRegistry) registry
					.lookup(RemoteReaderConnectionRegistry.class.getName());
		} catch (AccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
			
		for (int x = 0; x < 50; x++){
			
			Class<?> readerInfoClazz;
			try {
				
				readerInfoClazz = Class.forName("org.rifidi.edge.readerplugin.dummy.DummyReaderInfo");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			AbstractReaderInfo readerInfo = null;
			try {
				readerInfo = (AbstractReaderInfo) readerInfoClazz
				.getConstructor(new Class[0])
				.newInstance(new Object[0]);
			} catch (IllegalArgumentException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				throw new RuntimeException(e1);
			} catch (SecurityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				throw new RuntimeException(e1);
			} catch (InstantiationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				throw new RuntimeException(e1);
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				throw new RuntimeException(e1);
			} catch (InvocationTargetException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				throw new RuntimeException(e1);
			} catch (NoSuchMethodException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				throw new RuntimeException(e1);
			}
			readerInfo.setIPAddress("127.0.0.1");
			readerInfo.setPort(10000 + (threadPortStart * 100 ) + x);
			
			RemoteReaderConnection remoteReader1 = null;
			try {
				remoteReader1 = remoteReaderConnectionRegistry.createReaderConnection(readerInfo);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			
			try {
				remoteReader1.connect();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			try {
				remoteReader1.startTagStream();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//System.setSecurityManager(new SecurityManager());

		// TODO Auto-generated method stub
		List<Thread> threads = new ArrayList<Thread>();
		
		for (int x = 0; x < 3; x++){
			TestRun tr = new TestRun();
			tr.threadPortStart = x;
			Thread t = new Thread(tr);
			threads.add(t);
		}
		
		
		for (Thread t : threads){
			t.start();
		}
	}

}
